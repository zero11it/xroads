package it.zero11.xroads.listener;

import java.lang.reflect.Field;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import it.zero11.xroads.utils.EntityManagerUtils;
import it.zero11.xroads.utils.modules.core.cron.CronScheduler;
import it.zero11.xroads.utils.modules.core.utils.ClusterSettingsUtils;
import it.zero11.xroads.utils.modules.core.utils.LocalCache;
import it.zero11.xroads.utils.modules.core.utils.LocalCacheMulticastListener;
import it.zero11.xroads.webservices.XRoadsRestServlet;

@WebListener
public class XRoadsListener implements ServletContextListener {
	private static Logger log = Logger.getLogger(XRoadsListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		EntityManagerUtils.migrate();
		
		ServletContext context = sce.getServletContext();
		String name = "";
		if (context.getClass().getName().equals("org.apache.catalina.core.StandardContext")){
			try{
				name = (String) context.getClass().getMethod("getDocBase").invoke(context);
			}catch (Exception e) {
			}
		}else if (context.getClass().getName().equals("org.apache.catalina.core.ApplicationContextFacade")){
			try{
				Field field = context.getClass().getDeclaredField("context");
				field.setAccessible(true);
				Object applicationContext = field.get(context);
				field = applicationContext.getClass().getDeclaredField("context");
				field.setAccessible(true);
				Object standardContext = field.get(applicationContext);
				name = (String) standardContext.getClass().getMethod("getDocBase").invoke(standardContext);
			}catch (Exception e) {
			}
		}
		
		LocalCacheMulticastListener.getInstance().start();
		LocalCacheMulticastListener.getInstance().registerCache(LocalCache.getInstance());
		
		ClusterSettingsUtils.INSTANCE_NAME = ((System.getProperty("nodename") != null) ? System.getProperty("nodename") : "Default") + name;
		
		CronScheduler.start(name);
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
		
		try {
			LocalCacheMulticastListener.getInstance().shutdown();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		XRoadsRestServlet.shutdown();
	}

}
