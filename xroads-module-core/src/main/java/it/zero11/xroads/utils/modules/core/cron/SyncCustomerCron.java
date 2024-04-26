package it.zero11.xroads.utils.modules.core.cron;


import java.util.Arrays;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;

@CronSchedule(hour={}, minute={0,3,6,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57}, second={0}, onDeploy=false)
public class SyncCustomerCron extends AbstractSyncCron<Customer>{

	@Override
	protected Class<Customer> getEntity() {
		return Customer.class;
	}

	@Override
	protected void afterConsume(Customer entity) {		
		if (EntityDao.getInstance().updateExternalReferenceMarkForRetryInAllModulesByCustomerGroup(Order.class, Arrays.asList(entity.getSourceId()))) {
			CronDao.getInstance().addScheduleNowIfNotScheduled(SyncOrderCron.class.getSimpleName(), XRoadsCoreModule.INSTANCE.getName());
		}
	}

}
