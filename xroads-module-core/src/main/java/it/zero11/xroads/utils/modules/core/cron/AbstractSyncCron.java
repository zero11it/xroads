package it.zero11.xroads.utils.modules.core.cron;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.AbstractProductGroupedEntity;
import it.zero11.xroads.model.ModuleOrder;
import it.zero11.xroads.model.ModuleStatus;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.EntityProductGroupedConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.utils.XRoadsAsyncUtils;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;
import it.zero11.xroads.utils.modules.core.model.WrapFilter;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;

public abstract class AbstractSyncCron<T extends AbstractEntity> implements Runnable {
	private static int PAGE_SIZE = 50;

	@Override
	public void run() {
		boolean hasEntityToProcess = false;
		for (XRoadsModule module : XRoadsCoreServiceBean.getInstance().getEnabledModules(false).values()) {
			hasEntityToProcess |= !sync(module);
		}
		
		if (hasEntityToProcess) {
			CronDao.getInstance().addScheduleNowIfNotScheduled(getClass().getSimpleName());
		}
	}

	@SuppressWarnings("unchecked")
	private boolean sync(XRoadsModule module) {
		EntityConsumer<T> entityConsumer = module.getEntityConsumer(getEntity());
		if (entityConsumer != null) {
			return syncEntity(module, entityConsumer);
		}
		
		if (AbstractProductGroupedEntity.class.isAssignableFrom(getEntity())) {
			EntityProductGroupedConsumer<?> entityProductGroupedConsumer = module.getEntityProductGroupedConsumer((Class<AbstractProductGroupedEntity>) getEntity());
			if (entityProductGroupedConsumer != null) {
				return syncProductGroupedEntity(module, entityProductGroupedConsumer);
			}
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	private <TG extends AbstractProductGroupedEntity> boolean syncProductGroupedEntity(XRoadsModule module, EntityProductGroupedConsumer<TG> entityProductGroupedConsumer) {
		List<TG> entities = EntityDao.getInstance().getEntities((Class<TG>) getEntity(), 0, PAGE_SIZE, new WrapFilter(ModuleStatus.TO_SYNC), ModuleOrder.LAST_ERROR_DATE, module);
		//We are reloading all cause we need to send all items even the one that may already be in sync
		Map<String, List<TG>> entitiesGroupped = EntityDao.getInstance().getEntitiesByProductGroup((Class<TG>) getEntity(), entities.stream().map(TG::getProductSourceId).collect(Collectors.toSet()));
		
		XRoadsAsyncUtils.getInstance().parallelExecuteAndWait(entitiesGroupped.values(), entityGroup -> {
			try {
				entityProductGroupedConsumer.consume(entityGroup);
			}catch (SyncException e) {
				for (TG entity:entityGroup) {
					EntityDao.getInstance().updateExternalReferenceLastError(entity, module, e);
				}
			}catch (Throwable e) {
				for (TG entity:entityGroup) {
					EntityDao.getInstance().updateExternalReferenceLastError(entity, module, e);
				}
			}
		});
		
		return (entities.size() != PAGE_SIZE);
	}

	private boolean syncEntity(XRoadsModule module, EntityConsumer<T> entityConsumer) {
		List<T> entities = EntityDao.getInstance().getEntities(getEntity(), 0, PAGE_SIZE, new WrapFilter(ModuleStatus.TO_SYNC), ModuleOrder.LAST_ERROR_DATE, module);

		XRoadsAsyncUtils.getInstance().parallelExecuteAndWait(entities, entity -> {
			try {
				entityConsumer.consume(entity);
				afterConsume(entity);
			}catch (SyncException e) {
				EntityDao.getInstance().updateExternalReferenceLastError(entity, module, e);
			}catch (Throwable e) {
				EntityDao.getInstance().updateExternalReferenceLastError(entity, module, e);
			}
		});

		return (entities.size() != PAGE_SIZE);
	}

	protected abstract Class<T> getEntity();
	protected abstract void afterConsume(T entity);

}
