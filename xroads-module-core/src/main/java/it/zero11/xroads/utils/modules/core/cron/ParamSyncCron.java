package it.zero11.xroads.utils.modules.core.cron;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;

@CronSchedule(hour={0}, minute={0}, second={0}, onDeploy=true)
public class ParamSyncCron implements Runnable {
	@Override
	public void run() {
		ParamDao.getInstance().syncDB();
	}
}
