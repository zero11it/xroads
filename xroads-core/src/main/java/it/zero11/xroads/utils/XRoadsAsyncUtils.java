package it.zero11.xroads.utils;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class XRoadsAsyncUtils {
	private static final AtomicInteger threadCounter = new AtomicInteger();
	private final ScheduledThreadPoolExecutor backgroundPool;
	
	private static XRoadsAsyncUtils instance = null;

	public static XRoadsAsyncUtils getInstance() {
		if (instance == null) {
			synchronized (XRoadsAsyncUtils.class){
				if (instance == null){
					instance = new XRoadsAsyncUtils();
				}
			}
		}
		return instance;
	}
	
	private XRoadsAsyncUtils(){
		backgroundPool = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {			
			@Override
			public Thread newThread(Runnable runnable) {
				final Thread thread = new Thread(runnable, "XRoadsAsyncBackgroundPool-" + threadCounter.incrementAndGet());
				thread.setDaemon(true);
				thread.setPriority(Thread.MIN_PRIORITY);
				return thread;
			}
		});
		backgroundPool.setMaximumPoolSize(4);
		backgroundPool.setKeepAliveTime(10L, TimeUnit.SECONDS);
		backgroundPool.allowCoreThreadTimeOut(true);
	}
	
	public int getQueueSize(){
		return backgroundPool.getQueue().size();
	}
	
	public long getProcessedCount(){
		return backgroundPool.getCompletedTaskCount();
	}

	public void submit(Runnable runnable) {
		backgroundPool.submit(runnable);
	}
	
	public void schedule(Runnable runnable, long delay, TimeUnit unit) {
		backgroundPool.schedule(runnable, delay, unit);
	}

	public <T> void parallelExecuteAndWait(Collection<T> entities, Consumer<T> consumer) {
		try {
			backgroundPool.invokeAll(entities.stream().map(entity ->
				(Callable<Void>) () -> {
					consumer.accept(entity);
					return null;
				}).collect(Collectors.toList()));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
