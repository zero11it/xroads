package it.zero11.xroads.utils.modules.core.cron;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Stock;

@CronSchedule(hour={}, minute={0,10,20,30,40,50}, second={0}, onDeploy=false)
public class SyncStockCron extends AbstractSyncCron<Stock> {

	@Override
	protected Class<Stock> getEntity() {
		return Stock.class;
	}

	@Override
	protected void afterConsume(Stock entity) {	
	}

}
