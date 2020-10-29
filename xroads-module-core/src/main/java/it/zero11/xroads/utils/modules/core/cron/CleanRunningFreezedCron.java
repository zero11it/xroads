package it.zero11.xroads.utils.modules.core.cron;

import org.apache.log4j.Logger;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.utils.modules.core.utils.ClusterSettingsUtils;
import it.zero11.xroads.utils.modules.core.utils.SMTPUtils;

@CronSchedule(hour={0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22}, minute={0}, second={0}, onDeploy=true, force=true)
public class CleanRunningFreezedCron implements Runnable {
	private static final Logger log = Logger.getLogger(CleanRunningFreezedCron.class);
	
	@Override
	public void run() {
		log.info("Start clean freezed cron");

		if (CronDao.getInstance().cleanRunningFreezedCron(ClusterSettingsUtils.INSTANCE_NAME, CronScheduler.CLEAN_RUNNING_FREEZED) > 0){
			SMTPUtils.sendMessage(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.WARNING_NOTIFICATION_EMAILS).split(","),
					"XRoads - Found some freezed cron !",
					"XRoads - Found some freezed cron !");
		}
		
		log.info("End clean freezed cron");
	}

}
