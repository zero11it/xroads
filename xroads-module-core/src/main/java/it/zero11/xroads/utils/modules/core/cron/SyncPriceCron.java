package it.zero11.xroads.utils.modules.core.cron;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Price;

@CronSchedule(hour={}, minute={0,10,20,30,40,50}, second={0}, onDeploy=false)
public class SyncPriceCron extends AbstractSyncCron<Price> {

	@Override
	protected Class<Price> getEntity() {
		return Price.class;
	}

	@Override
	protected void afterConsume(Price entity) {	
	}

}
