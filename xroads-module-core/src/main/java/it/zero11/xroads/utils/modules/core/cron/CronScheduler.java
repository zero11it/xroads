package it.zero11.xroads.utils.modules.core.cron;

import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.cron.XRoadsCronRunnable;
import it.zero11.xroads.model.Cron;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.utils.StackTraceUtil;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;
import it.zero11.xroads.utils.modules.core.utils.ClusterSettingsUtils;
import it.zero11.xroads.utils.modules.core.utils.SMTPUtils;

public class CronScheduler extends Thread {
	private static final long MINUTES = 60L * 1000L;
	private static final long HOURS = 60L * MINUTES;
	public static final long SCHEDULE_AHEAD = 10L * MINUTES;
	public static final long SCHEDULE_TENANT_PARTITIONS_DURATION = 3L * MINUTES;
	public static final long MAX_EXECUTION_DELAY = 20L * MINUTES;
	public static final long CLEAN_DELAY_SUCCESS = 8 * HOURS;
	public static final long CLEAN_RUNNING_FREEZED = 6 * HOURS;
	public static final long CLEAN_DELAY_FAILED = 48 * HOURS;
	public static final boolean FORCE_CURRENT_NODE = "true".equalsIgnoreCase(System.getProperty("forcecurrentnode"));
	
	private boolean shutdown = false;
	private boolean onlyNodeCron = false;
	private final ThreadPoolExecutor cronThreadPoolExecutor;

	private static final AtomicInteger threadCounter = new AtomicInteger();
	
	private static CronScheduler scheduler;
	
	public static void start(String name) {
		if (scheduler == null) {
			scheduler = new CronScheduler(name);
			scheduler.start();
		}
	}
	
	public static CronScheduler get() {
		return scheduler;
	}
	
	public static void shutdown() throws InterruptedException {
		if (scheduler != null) {
			scheduler.shutdown(true);
			scheduler = null;
		}
	}

	private CronScheduler(String context) {
		super("CronScheduler" + context);

		cronThreadPoolExecutor = new ThreadPoolExecutor(1, 1, 20L, TimeUnit.SECONDS, new CronQueue(), new ThreadFactory() {			
			@Override
			public Thread newThread(Runnable runnable) {
				final Thread thread = new Thread(runnable, "CronScheduler-Executor-" + context + "-" + threadCounter.incrementAndGet());
				thread.setDaemon(true);
				thread.setPriority(Thread.MIN_PRIORITY);
				return thread;
			}
		});
		cronThreadPoolExecutor.allowCoreThreadTimeOut(true);
		
		updateClusterSettings();
	}
	
	private class CronQueue extends LinkedBlockingQueue<Runnable>{
		private static final long serialVersionUID = 1L;

		private void notifyCronScheduler() {
			synchronized (CronScheduler.this) {
				CronScheduler.this.notify();	
			}
		}
		
		@Override
		public Runnable take() throws InterruptedException {
			notifyCronScheduler();
			return super.take();
		}

		@Override
		public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
			notifyCronScheduler();
			return super.poll(timeout, unit);
		}

		@Override
		public Runnable poll() {
			notifyCronScheduler();
			return super.poll();
		}
		
	}

	private void shutdown(boolean wait) throws InterruptedException {
		shutdown = true;
		cronThreadPoolExecutor.shutdown();
		synchronized (this) {
			notify();	
		}
		if (wait){
			join(5L * 60L * 1000L);
			cronThreadPoolExecutor.awaitTermination(15, TimeUnit.MINUTES);
		}
	}

	private static long nextSlotNotificationDate = 0L;
	   
	@Override
	public void run() {
		for (XRoadsModule xRoadsModule : XRoadsCoreServiceBean.getInstance().getEnabledModules(true).values()) {
			for (Map.Entry<String, Class<? extends Runnable>> entry:xRoadsModule.getCrons().entrySet()){
				CronSchedule cronSchedule = entry.getValue().getAnnotation(CronSchedule.class);
				if (cronSchedule.onDeploy()){
					CronDao.getInstance().addSchedule(entry.getValue().getSimpleName(), ClusterSettingsUtils.INSTANCE_NAME, new Date(), true);
				}
			}
		}

		while (!shutdown){
			try{
				Cron cron = CronDao.getInstance().deQueue(ClusterSettingsUtils.INSTANCE_NAME, onlyNodeCron);
				if (cron != null){
					if (!cron.getForceExecution() && cron.getScheduledTime().before(new Date(new Date().getTime() - MAX_EXECUTION_DELAY))){
						CronDao.getInstance().failed(cron, "Cron too late");
					}else{
						Entry<XRoadsModule, Class<? extends Runnable>> cronEntry = getCronEntry(cron.getName());
						
						if (cronEntry != null){
							cronThreadPoolExecutor.execute(()->{
								String error = null;

								try{
									Runnable cronInstance = cronEntry.getValue().newInstance();
									if (cronInstance instanceof XRoadsCronRunnable){
										((XRoadsCronRunnable) cronInstance).setXRoadsModule(cronEntry.getKey());
									}
									cronInstance.run();
								}catch (Exception e) {
									error = StackTraceUtil.getStackTraceAsHTML(e);
									
									boolean sendNotification = true;
									if (e.getMessage() != null && e.getMessage().contains("slot")) {
								    	if (nextSlotNotificationDate == 0L || nextSlotNotificationDate < System.currentTimeMillis()){
											nextSlotNotificationDate = System.currentTimeMillis() + 15L * 60L * 1000L;
								    	}else {
								    		sendNotification = false;
								    	}
									}

									if (sendNotification) {
										SMTPUtils.sendMessage(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.WARNING_NOTIFICATION_EMAILS).split(","),
											ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.NAME) + " - Cron " + cronEntry.getValue().getSimpleName() + " failed",
											error);
									}
								}finally {
									if (error != null){
										CronDao.getInstance().failed(cron, error);
									}else{
										CronDao.getInstance().successful(cron);
									}
								}
							});
						}else{
							CronDao.getInstance().failed(cron, "Cron not found");
						}
					}
				}else {
					synchronized (this) {
						wait(60000);
					}
				}
				while(cronThreadPoolExecutor.getActiveCount() == cronThreadPoolExecutor.getMaximumPoolSize()){
					synchronized (this) {
						wait(60000);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				try {
					synchronized (this) {
						wait(60000);
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private Entry<XRoadsModule, Class<? extends Runnable>> getCronEntry(String name) {
		for (XRoadsModule xRoadsModule : XRoadsCoreServiceBean.getInstance().getEnabledModules(true).values()) {
			Class<? extends Runnable> cronClass = xRoadsModule.getCrons().get(name);
			if (cronClass != null) {
				return new AbstractMap.SimpleEntry<>(xRoadsModule, cronClass);
			}
		}
		
		return null;
	}

	public void updateClusterSettings() {
		JSONObject nodeSetting = ClusterSettingsUtils.getNodeSetting();
		
		onlyNodeCron = nodeSetting.getBoolean(ClusterSettingsUtils.ONLY_NODE_CRON);
		
		int poolSize = nodeSetting.getInt(ClusterSettingsUtils.POOL_SIZE);
		if (cronThreadPoolExecutor.getMaximumPoolSize() < poolSize) {
			cronThreadPoolExecutor.setMaximumPoolSize(poolSize);
			cronThreadPoolExecutor.setCorePoolSize(poolSize);
		}else {
			cronThreadPoolExecutor.setCorePoolSize(poolSize);
			cronThreadPoolExecutor.setMaximumPoolSize(poolSize);
		}
	}
}
