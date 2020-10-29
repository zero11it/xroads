package it.zero11.xroads.utils.modules.core.cron;

import it.zero11.xroads.cron.CronSchedule;

@CronSchedule(hour={}, minute={0,10,20,30,40,50}, second={0}, force=true, allNodes = true, onDeploy = true)
public class RefreshClusterSettingsCron implements Runnable{
	@Override
	public void run() {
		CronScheduler.get().updateClusterSettings();
	}
}