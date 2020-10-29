package it.zero11.xroads.utils.modules.core.cron;

import java.util.Arrays;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Price;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;

@CronSchedule(hour={}, minute={0,10,20,30,40,50}, second={0}, onDeploy=false)
public class SyncProductCron extends AbstractSyncCron<Product> {

	@Override
	protected Class<Product> getEntity() {
		return Product.class;
	}

	@Override
	protected void afterConsume(Product entity) {
		if (EntityDao.getInstance().updateExternalReferenceMarkForRetryInAllModulesByProductGroup(Model.class, Arrays.asList(entity.getSourceId()))) {
			CronDao.getInstance().addScheduleNowIfNotScheduled(SyncModelCron.class.getSimpleName());
		}
		if (EntityDao.getInstance().updateExternalReferenceMarkForRetryInAllModulesByProductGroup(Price.class, Arrays.asList(entity.getSourceId()))) {
			CronDao.getInstance().addScheduleNowIfNotScheduled(SyncPriceCron.class.getSimpleName());
		}
	}

}
