package it.zero11.xroads.utils.modules.core.utils;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LocalCache {
	public static final long LONG_CACHE_TIME = 1L * 60L * 60L * 1000L;
	public static final long MEDIUM_CACHE_TIME = 30L * 60L * 1000L;
	public static final long SHORT_CACHE_TIME = 10L * 60L * 1000L;
	public static final long FORCE_REGENERATION_CACHE_TIME = 0;

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

	private long statCacheHit = 0;
	private long statCacheHitForUpdate = 0;
	private long statCacheMiss = 0;

	private final ThreadPoolExecutor backgroundRefreshPool;
	private final LocalCacheMulticastListener multiCastListener;
	private final LocalCacheHashMap cache;
	final String name; 
	
	public LocalCache(String name, int size) {
		this.name = name;
		
		cache = new LocalCacheHashMap(size);
		backgroundRefreshPool = new ThreadPoolExecutor(2, 2, 20L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {			
			@Override
			public Thread newThread(Runnable runnable) {
                final Thread thread = new Thread(runnable, "LocalCacheRefresh-" + threadCounter.incrementAndGet());
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
			}
		});
		backgroundRefreshPool.allowCoreThreadTimeOut(true);
		
		multiCastListener = LocalCacheMulticastListener.getInstance();
	}

	public void invalidate(String key) {
		synchronized (cache) {
			cache.remove(key);
		}
		
		multiCastListener.invalidate(this, key);
	}
	
	void invalidateLocalOnly(String key) {
		synchronized (cache) {
			cache.remove(key);
		}
	}

	public void clear() {
		synchronized (cache) {
			cache.clear();
			statCacheHit = 0;
			statCacheHitForUpdate = 0;
			statCacheMiss = 0;
		}
	}

	public long getStatCacheHit() {
		return statCacheHit;
	}

	public long getStatCacheHitForUpdate() {
		return statCacheHitForUpdate;
	}

	public long getStatCacheHitForUpdateBackgroundCount() {
		return backgroundRefreshPool.getCompletedTaskCount();
	}

	public long getStatCacheMiss() {
		return statCacheMiss;
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
		return cache.maximumCapacity;
	}
	
	public <T> T getOrGenerateWeak(String key, long ttl, LocalCacheGenerator<T> generator) {
		AtomicReference<T> generatedValue = new AtomicReference<>();
		WeakReference<T> reference = getOrGenerate(key, ttl, ()->{
			generatedValue.set(generator.generate());
			return new WeakReference<>(generatedValue.get());
		}, true);
		T value = generatedValue.get();
		if (value != null) {
			return value;
		}else if (reference != null) {
			value = reference.get();
			if (value != null) {
				return value;
			}else if (ttl != FORCE_REGENERATION_CACHE_TIME) {
				//Soft reference was GC
				return getOrGenerateWeak(key, FORCE_REGENERATION_CACHE_TIME, generator);
			}else {
				return null;
			}
		}else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOrGenerate(String key, long ttl, LocalCacheGenerator<T> generator, boolean executeInCallerThread) {
		LocalCacheObject obj;
		boolean needGeneration = false;
		boolean immediateGeneration = false;
		long now = System.currentTimeMillis();
		synchronized (cache) {
			obj = (LocalCacheObject) cache.get(key);
			if (obj == null){
				statCacheMiss++;
				obj = new LocalCacheObject(now + ttl + (System.nanoTime() & 0x2FFFFL /* "random" between 0 - 262144 */), true, false, null);
				cache.put(key, obj);
				needGeneration = true;
				immediateGeneration = true;
			}else if ((obj.expireTime < now || ttl == FORCE_REGENERATION_CACHE_TIME) && obj.updating == false){
				statCacheHitForUpdate++;
				obj.updating = true;
				obj.expireTime = now + ttl + (System.nanoTime() & 0x2FFFFL /* "random" between 0 - 262144 */);
				needGeneration = true;
				immediateGeneration = (obj.valid == false) || (ttl == FORCE_REGENERATION_CACHE_TIME);
			}else if (obj.valid == false && obj.updating == false){
				statCacheMiss++;
				obj.updating = true;
				obj.expireTime = now + ttl + (System.nanoTime() & 0x2FFFFL /* "random" between 0 - 262144 */);
				needGeneration = true;
				immediateGeneration = true;
			}else{
				statCacheHit++;
			}
		}

		if (needGeneration == false){
			if (obj.valid == false){
				//wait for up to 60 seconds for object to generate
				int retry = 600;
				while(retry-- > 0 && obj.valid == false){
					try{Thread.sleep(100);}catch(InterruptedException e){}
				}
				if (obj.valid == false){
					new LocalCacheGeneratorRunnable<T>(obj, generator).run();
				}
				return (T) obj.object;
			}else{
				return (T) obj.object;
			}
		}else{
			if(executeInCallerThread || immediateGeneration) {
				new LocalCacheGeneratorRunnable<T>(obj, generator).run();
			} else {
				backgroundRefreshPool.submit(new LocalCacheGeneratorRunnable<T>(obj, generator));
			}
			return (T) obj.object; 
		}
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
		LOCATIONS("LOCATIONS"),
		REWIXCATALOG("REWIXCATALOG");

		final String value;
		LocalCacheType(String value) {
			this.value = value;
		}
	}

	//@FunctionalInterface
	public static interface LocalCacheGenerator<T> {
		public T generate();
	}

	private static class LocalCacheGeneratorRunnable<T> implements Runnable{
		private final LocalCacheObject result;
		private final LocalCacheGenerator<T> generator;

		public LocalCacheGeneratorRunnable(LocalCacheObject result, LocalCacheGenerator<T> generator) {
			this.result = result;
			this.generator = generator;
		}

		public final void run() {
			try{
				long generationTime = System.currentTimeMillis();
				result.object = generator.generate();
				generationTime = System.currentTimeMillis() - generationTime;
				result.expireTime += generationTime;
				result.valid = true;
			}catch(Exception e){
				e.printStackTrace();
				result.object = null;
				result.valid = false;
			}finally{
				result.updating = false;
			}
		}
	}

	public static String buildKey(LocalCacheType type, String platform_uid, String rawKey) {
		return platform_uid + type.value + rawKey;
	}

	private static class LocalCacheHashMap extends LinkedHashMap<String, LocalCacheObject> {
		private static final long serialVersionUID = 1L;
		
		int maximumCapacity;

		LocalCacheHashMap(int maximumCapacity) {
			super(maximumCapacity, 1.05f, true);
			this.maximumCapacity = maximumCapacity;
		}

		protected boolean removeEldestEntry(Map.Entry<String, LocalCacheObject> eldest) {
			return size() > maximumCapacity || eldest.getValue().expireTime + LONG_CACHE_TIME < System.currentTimeMillis();
		}
	}
}
