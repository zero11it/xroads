package it.zero11.xroads.utils.modules.core.cron;


import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Order;

@CronSchedule(hour={}, minute={0,3,6,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57}, second={0}, onDeploy=false)
public class SyncOrderCron extends AbstractSyncCron<Order>{

	@Override
	protected Class<Order> getEntity() {
		return Order.class;
	}

	@Override
	protected void afterConsume(Order entity) {		
	}

}
