package it.zero11.xroads.utils.modules.core.cron;

import java.util.Arrays;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Stock;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;

@CronSchedule(hour={}, minute={0,10,20,30,40,50}, second={0}, onDeploy=false)
public class SyncModelCron extends AbstractSyncCron<Model> {

	@Override
	protected Class<Model> getEntity() {
		return Model.class;
	}

	@Override
	protected void afterConsume(Model entity) {
		if (EntityDao.getInstance().updateExternalReferenceMarkForRetryInAllModulesByModelGroup(Stock.class, Arrays.asList(entity.getSourceId()))) {
			CronDao.getInstance().addScheduleNowIfNotScheduled(SyncStockCron.class.getSimpleName());
		}
	}

}
