package it.zero11.xroads.utils.modules.core.utils;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class LocalCache {
	static final String PLATFORM_NAME = "Xroads";
	//must be a valid MASK clean up every 1024 cache hit
	private static final long READ_CLEANUP_THRESHOLD = 0x3FF;
	
	private static final AtomicInteger threadCounter = new AtomicInteger();

	private static LocalCache instance;
	public static LocalCache getInstance() {
		if (instance == null) {
			synchronized (LocalCache.class){
				if (instance == null){
					instance = new LocalCache("Default", 32768);
				}
			}
		}
		return instance;
	}

	private Collection<String> statLastMisses = new CircularFifoQueue<>(10);
	private long statCacheHit = 0;
	private long statCacheHitForUpdate = 0;
	private long statCacheMiss = 0;
	private long statCacheCollision = 0;
	private long statCacheInvalidateLocal = 0;
	private long statCacheInvalidateCluster = 0;

	private long currentSeed = System.nanoTime();
	private long nextRandom(long maxValue) {
		long next = currentSeed;
		next ^= next << 21;
		next ^= next >> 35;
		next ^= next << 4;
		currentSeed = next;
		return currentSeed & (Long.highestOneBit(maxValue) - 1);
	}

	private final LocalCacheUDPListener multiCastListener;
	private final ThreadPoolExecutor backgroundRefreshPool;
	private final ReentrantLock lock = new ReentrantLock(false);
	private final TreeMap<LocalCacheObject, String> cacheByExpiring;
	private final LRUMap<String, LocalCacheObject> cache;
	private int maximumCapacity;
	final String name; 
	
	public LocalCache(String name, int size) {
		this.name = name;
		
		maximumCapacity = size;
		cache = new LRUMap<String, LocalCacheObject>(Integer.MAX_VALUE, size, 1.05f);
		cacheByExpiring = new TreeMap<>((a,b) -> Long.compare(a.expireTime, b.expireTime));
		backgroundRefreshPool = new ThreadPoolExecutor(2, 2, 20L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {			
			@Override
			public Thread newThread(Runnable runnable) {
                final Thread thread = new Thread(runnable, "LocalCacheRefresh-" + PLATFORM_NAME + "-" + threadCounter.incrementAndGet());
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
			}
		});
		backgroundRefreshPool.allowCoreThreadTimeOut(true);
		
		multiCastListener = LocalCacheUDPListener.getInstance();
	}
	
	public void setMaxSize(int size) {
		maximumCapacity = size;
	}

	public void invalidate(String key) {
		lock.lock();
		try {
			LocalCacheObject obj = cache.remove(key);
			if (obj != null) {
				cacheByExpiring.remove(obj);
				statCacheInvalidateLocal++;
			}
		}finally {
			lock.unlock();
		}
		
		multiCastListener.invalidate(this, key);
	}
	
	public void invalidateLocalOnly(String key) {
		lock.lock();
		try {
			LocalCacheObject obj = cache.remove(key);
			if (obj != null) {
				cacheByExpiring.remove(obj);
				statCacheInvalidateCluster++;
			}
		}finally {
			lock.unlock();
		}
	}
	
	public void invalidateRemoteOnly(String key) {
		multiCastListener.invalidate(this, key);
	}

	public void clear() {
		lock.lock();
		try {
			cache.clear();
			cacheByExpiring.clear();
			statCacheHit = 0;
			statCacheHitForUpdate = 0;
			statCacheMiss = 0;
			statLastMisses.clear();
			statCacheCollision = 0;
			statCacheInvalidateCluster = 0;
			statCacheInvalidateLocal = 0;
		}finally {
			lock.unlock();
		}
	}

	public long getStatCacheHit() {
		return statCacheHit;
	}

	public long getStatCacheHitForUpdate() {
		return statCacheHitForUpdate;
	}

	public long getStatCacheInvalidateLocal() {
		return statCacheInvalidateLocal;
	}

	public long getStatCacheInvalidateCluster() {
		return statCacheInvalidateCluster;
	}

	public long getStatCacheHitForUpdateBackgroundCount() {
		return backgroundRefreshPool.getCompletedTaskCount();
	}

	public long getStatCacheMiss() {
		return statCacheMiss;
	}

	public List<String> getStatCacheLastMisses() {
		lock.lock();
		try {
			return new ArrayList<String>(statLastMisses);
		}finally {
			lock.unlock();
		}
	}

	public long getStatCacheCollision() {
		return statCacheCollision;
	}

	public int getStatCurrentQueueSize() {
		return backgroundRefreshPool.getQueue().size();
	}
	
	public int getStatCurrentPoolSize() {
		return backgroundRefreshPool.getPoolSize();
	}

	public int getCacheSize() {
		return cache.size();
	}
	
	public int getCacheMaximumCapacity() {
		return maximumCapacity;
	}
	
	public <T> T getOrGenerateWeak(String key, TTL ttl, Supplier<T> generator) {
		AtomicReference<T> generatedValue = new AtomicReference<>();
		WeakReference<T> reference = getOrGenerate(key, UpdateMode.CALLER_THREAD, ttl, ()->{
			generatedValue.set(generator.get());
			return new WeakReference<>(generatedValue.get());
		});
		T value = generatedValue.get();
		if (value != null) {
			return value;
		}else if (reference != null) {
			value = reference.get();
			if (value != null) {
				return value;
			}else if (!ttl.equals(TTL.FORCE_UPDATE)) {
				//Soft reference was GC
				return getOrGenerateWeak(key, TTL.FORCE_UPDATE, generator);
			}else {
				return null;
			}
		}else {
			return null;
		}
	}

	public boolean containsKey(String key) {
		return cache.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getOrGenerate(String key, UpdateMode updateMode, TTL ttl, Supplier<T> supplier) {
		LocalCacheObject obj;
		boolean needGeneration = false;
		boolean immediateGeneration = false;
		long now = System.currentTimeMillis();
		lock.lock();
		try {
			obj = (LocalCacheObject) cache.get(key);
			if (obj == null){
				if (cache.size() >= maximumCapacity) {
					String lruKey = cache.firstKey();
					LocalCacheObject lruLocalCacheObject = cache.remove(lruKey);
					if (cacheByExpiring.remove(lruLocalCacheObject) == null) {
						System.err.println("LocalCache bug removed not existing entry");
					}
				}
				statCacheMiss++;
				statLastMisses.add(key);
				obj = new LocalCacheObject(now + ttl.value + nextRandom(ttl.value), true, false, null);
				cache.put(key, obj);
				while(cacheByExpiring.containsKey(obj)) {
					statCacheCollision++;
					obj.expireTime += 1L;
				}
				cacheByExpiring.put(obj, key);
				needGeneration = true;
				immediateGeneration = true;
			}else if (ttl.equals(TTL.FORCE_UPDATE) && obj.updating == false){
				statCacheHitForUpdate++;
				obj.updating = true;
				updateExpireTime(obj, key, now);
				needGeneration = true;
				immediateGeneration = true;
			}else if (obj.expireTime < now && obj.updating == false){
				statCacheHitForUpdate++;
				obj.updating = true;
				updateExpireTime(obj, key, now + ttl.value + nextRandom(ttl.value));
				needGeneration = true;
				immediateGeneration = (obj.valid == false);
				
				if ((statCacheHitForUpdate & READ_CLEANUP_THRESHOLD) == 0L) {
					long toCleanExpireTime = now - TTL.SHORT.value;
			        while(cacheByExpiring.firstKey().expireTime < toCleanExpireTime) {
			        	String keyToRemove = cacheByExpiring.pollFirstEntry().getValue();
			        	cache.remove(keyToRemove);
			        }
			    }
			}else if (obj.valid == false && obj.updating == false){
				statCacheMiss++;
				statLastMisses.add(key);
				obj.updating = true;
				updateExpireTime(obj, key, now + ttl.value + nextRandom(ttl.value));
				needGeneration = true;
				immediateGeneration = true;
			}else{
				statCacheHit++;
			}
		}finally {
			lock.unlock();
		}

		if (needGeneration == false){
			if (obj.valid == false){
				//wait for up to 60 seconds for object to generate
				int retry = 600;
				while(retry-- > 0 && obj.valid == false){
					try{Thread.sleep(100);}catch(InterruptedException e){}
				}
				if (obj.valid == false){
					new LocalCacheValueUpdater<T>(obj, supplier).run();
				}
				return (T) obj.object;
			}else{
				return (T) obj.object;
			}
		}else{
			if(updateMode.equals(UpdateMode.CALLER_THREAD) || immediateGeneration) {
				new LocalCacheValueUpdater<T>(obj, supplier).run();
			} else {
				backgroundRefreshPool.submit(new LocalCacheValueUpdater<T>(obj, supplier));
			}
			return (T) obj.object; 
		}
	}

	private void updateExpireTime(LocalCacheObject obj, String key, long expireTime) {
		cacheByExpiring.remove(obj);
		obj.expireTime = expireTime;
		while(cacheByExpiring.containsKey(obj)) {
			statCacheCollision++;
			obj.expireTime += 1L;
		}
		cacheByExpiring.put(obj, key);
	}

	private static class LocalCacheObject{
		public LocalCacheObject(long expireTime, boolean updating, boolean valid, Object object) {
			this.expireTime = expireTime;
			this.updating = updating;
			this.object = object;
			this.valid = valid;
		}

		long expireTime;
		boolean updating;
		boolean valid;
		Object object;
	}

	public static enum LocalCacheType{
		SETTINGS("SETTINGS"),
		PARAMS("PARAMS"),
		MARKUPRULE("MARKUPRULE"),
		TAG("TAG"),
		TAGNAMES("TAGNAMES"),
		LOCATIONS("LOCATIONS"),
		REWIXCATALOG("REWIXCATALOG"),
		KEY("KEY");

		final String value;
		LocalCacheType(String value) {
			this.value = value;
		}
	}
	
	public static enum UpdateMode {
		CALLER_THREAD,
		BACKGROUND;
	}
	
	public static enum TTL {
		LONG(1L * 60L * 60L * 1000L),
		MEDIUM(30L * 60L * 1000L),
		SHORT(10L * 60L * 1000L),
		EXTRASHORT(2L * 60L * 1000L),
		FORCE_UPDATE(0L);
		
		public final long value;
		TTL(long value) {
			this.value = value;
		}
	}

	private static class LocalCacheValueUpdater<T> implements Runnable{
		private final LocalCacheObject result;
		private final Supplier<T> supplier;

		public LocalCacheValueUpdater(LocalCacheObject result, Supplier<T> supplier) {
			this.result = result;
			this.supplier = supplier;
		}

		public final void run() {
			try{
				result.object = supplier.get();
				result.valid = true;
			}catch(RuntimeException e){
				e.printStackTrace();
				result.object = null;
				result.valid = false;
				throw e;
			}finally{
				result.updating = false;
			}
		}
	}

	public static String buildKey(LocalCacheType type, String platform_uid, String rawKey) {
		return platform_uid + type.value + rawKey;
	}
}
