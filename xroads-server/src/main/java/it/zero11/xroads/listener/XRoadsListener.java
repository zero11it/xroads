package it.zero11.xroads.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import it.zero11.xroads.utils.EntityManagerUtils;
import it.zero11.xroads.utils.modules.core.cron.CronScheduler;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.utils.ClusterSettingsUtils;
import it.zero11.xroads.utils.modules.core.utils.LocalCache;
import it.zero11.xroads.utils.modules.core.utils.LocalCacheUDPListener;
import it.zero11.xroads.webservices.XRoadsRestServlet;

@WebListener
public class XRoadsListener implements ServletContextListener {
	private static Logger log = Logger.getLogger(XRoadsListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		EntityManagerUtils.migrate();
		
		LocalCacheUDPListener.getInstance().setContextName(sce.getServletContext().getVirtualServerName());
		LocalCacheUDPListener.getInstance().start();
		LocalCacheUDPListener.getInstance().setNodeListProvider(() -> CronDao.getInstance().getCurrentNodeList());
		LocalCacheUDPListener.getInstance().registerCache(LocalCache.getInstance());

		CronScheduler.start(ClusterSettingsUtils.INSTANCE_NAME);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			CronScheduler.shutdown();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		try {
			EntityManagerUtils.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		LocalCacheUDPListener.getInstance().shutdown();
		
		try {
			LocalCacheUDPListener.getInstance().join(10L*1000L);
		} catch (InterruptedException e) {
		}

		if (LocalCacheUDPListener.getInstance().isAlive()){
			log.error("Multicast cache listener didn't shutdown");
		}else{
			log.info("Multicast cache listener shutdown completed");
		}
		XRoadsRestServlet.shutdown();
	}

}
