package it.zero11.xroads.utils.modules.core.sync;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.AbstractEntityRevision;
import it.zero11.xroads.model.AbstractProductGroupedEntity;
import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.model.Invoice;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.ModuleStatus;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.model.Price;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.Stock;
import it.zero11.xroads.modules.XRoadsCoreService;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.cron.SyncCustomerCron;
import it.zero11.xroads.utils.modules.core.cron.SyncModelCron;
import it.zero11.xroads.utils.modules.core.cron.SyncOrderCron;
import it.zero11.xroads.utils.modules.core.cron.SyncPriceCron;
import it.zero11.xroads.utils.modules.core.cron.SyncProductCron;
import it.zero11.xroads.utils.modules.core.cron.SyncStockCron;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.utils.modules.core.model.WrapFilter;
import it.zero11.xroads.utils.modules.core.utils.LocalCache;
import it.zero11.xroads.utils.modules.core.utils.XRoadsCoreUtils;

public class XRoadsCoreServiceBean implements XRoadsCoreService {
	private static XRoadsCoreServiceBean instance = null;

	public static XRoadsCoreServiceBean getInstance() {
		if (instance == null) {
			synchronized (CronDao.class){
				if (instance == null){
					instance = new XRoadsCoreServiceBean();
				}
			}
		}
		return instance;
	}

	public Map<String, XRoadsModule> getEnabledModules(boolean includeCore){
		return LocalCache.getInstance().getOrGenerate("XROADSMODULES" + includeCore, LocalCache.LONG_CACHE_TIME, ()->{
			Map<String, XRoadsModule> enabledModules = new HashMap<String, XRoadsModule>();
			if (includeCore) {
				XRoadsCoreModule core = XRoadsCoreModule.INSTANCE;
				enabledModules.put(core.getName(), core);
			}
			JsonNode modules = ParamDao.getInstance().getParameterAsJsonNode(XRoadsCoreModule.INSTANCE, ParamType.MODULES);
			modules.fields().forEachRemaining((Map.Entry<String, JsonNode> entry) -> {
				try {
					XRoadsModule xRoadsModule = (XRoadsModule) Class.forName(entry.getValue().asText()).newInstance();
					xRoadsModule.configure(entry.getKey(), XRoadsCoreServiceBean.getInstance());
					enabledModules.put(xRoadsModule.getName(), xRoadsModule);
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			});
			return enabledModules;
		}, false);
	}
	
	@Override
	public <T extends AbstractXRoadsCronRunnable<?>> void addScheduleNowIfNotScheduled(Class<T> cronClass) {
		CronDao.getInstance().addScheduleNowIfNotScheduled(cronClass.getSimpleName());
	}

	@Override
	public <T extends AbstractEntity> String getEntityIdByModuleAndSourceId(Class<T> entityClass, XRoadsModule module, String sourceId) {
		return EntityDao.getInstance().getEntityIdByModule(entityClass, module, sourceId);
	}
	
	@Override
	public String getParameter(XRoadsModule module, IParamType paramType) {
		return ParamDao.getInstance().getParameter(module, paramType);
	}

	@Override
	public Integer getParameterAsInteger(XRoadsModule module, IParamType paramType) {
		return ParamDao.getInstance().getParameterAsInteger(module, paramType);
	}

	@Override
	public boolean getParameterAsBoolean(XRoadsModule module, IParamType paramType) {
		return ParamDao.getInstance().getParameterAsBoolean(module, paramType);
	}

	@Override
	public JsonNode getParameterAsJsonNode(XRoadsModule module, IParamType paramType) {
		return ParamDao.getInstance().getParameterAsJsonNode(module, paramType);
	}

	@Override
	public void updateParam(XRoadsModule module, IParamType paramType, String value) {
		ParamDao.getInstance().updateParam(module, paramType, value);
	}
	
	@Override
	public <T extends AbstractEntity> T getEntity(Class<T> entityClass, String id) {
		return EntityDao.getInstance().getEntity(entityClass, id);
	}

	@Override
	public <T extends AbstractEntityRevision<?>> T getEntityRevision(Class<T> entityRevisionClass, String id,
			Integer version) {
		return EntityDao.getInstance().getEntityRevision(entityRevisionClass, id, version);
	}

	@Override
	public <T extends AbstractEntity> void updateEntityInTransaction(T entity, XRoadsModule module, Consumer<T> consumer) {
		EntityDao.getInstance().updateEntityInTransaction(entity, module, consumer);
	}
	
	
	@Override
	public <T extends AbstractEntity> void updateExternalReference(XRoadsModule module, T entity) {
		EntityDao.getInstance().updateExternalReference(entity, module);
	}

	@Override
	public <T extends AbstractEntity> void updateExternalReferenceId(XRoadsModule module, T entity, String id) {
		EntityDao.getInstance().updateExternalReferenceId(entity, module, id);
	}

	@Override
	public <T extends AbstractEntity> void updateExternalReferenceIdAndVersion(XRoadsModule module, T entity,
			String id, int version) {
		EntityDao.getInstance().updateExternalReferenceIdAndVersion(entity, module, id, version);
	}

	@Override
	public <T extends AbstractEntity> void consume(XRoadsModule module, T entity) throws SyncException {
		if (entity instanceof Product) {
			if (EntityDao.getInstance().consume(module, (Product) entity, XRoadsCoreUtils::productHasChanged)) {
				CronDao.getInstance().addScheduleNowIfNotScheduled(SyncProductCron.class.getSimpleName());
			}
		}else if (entity instanceof Model) {
			if (EntityDao.getInstance().consume(module, (Model) entity, XRoadsCoreUtils::modelHasChanged)) {
				CronDao.getInstance().addScheduleNowIfNotScheduled(SyncModelCron.class.getSimpleName());
			}
		}else if (entity instanceof Price) {
			if (EntityDao.getInstance().consume(module, (Price) entity, XRoadsCoreUtils::priceHasChanged)) {
				CronDao.getInstance().addScheduleNowIfNotScheduled(SyncPriceCron.class.getSimpleName());
			}
		}else if (entity instanceof Stock) {
			if (EntityDao.getInstance().consume(module, (Stock) entity, XRoadsCoreUtils::stockHasChanged)) {
				CronDao.getInstance().addScheduleNowIfNotScheduled(SyncStockCron.class.getSimpleName());
			}
		}else if (entity instanceof Customer) {
			if (EntityDao.getInstance().consume(module, (Customer) entity, XRoadsCoreUtils::customerHasChanged)) {
				//FIXME
				CronDao.getInstance().addScheduleNowIfNotScheduled(SyncCustomerCron.class.getSimpleName());
			}
		}else if(entity instanceof Order) {	
			if (EntityDao.getInstance().consume(module, (Order) entity, XRoadsCoreUtils::orderkHasChanged)) {
				//FIXME add cron here
				CronDao.getInstance().addScheduleNowIfNotScheduled(SyncOrderCron.class.getSimpleName());
			}
		}else if(entity instanceof Invoice) {	
			if (EntityDao.getInstance().consume(module, (Invoice) entity, XRoadsCoreUtils::invoiceHasChanged)) {
				//FIXME add cron here
			}
			
		}else {
			throw new SyncException("Entity not supported");
		}
	}

	@Override
	public <T extends AbstractProductGroupedEntity> void consumeProductGroupped(XRoadsModule module, String groupId, List<T> entities) throws SyncException {
		if (entities.get(0) instanceof Model) {
			if (EntityDao.getInstance().consumeProductGroupped(module, groupId, (List<Model>) entities, XRoadsCoreUtils::modelHasChanged)) {
				CronDao.getInstance().addScheduleNowIfNotScheduled(SyncModelCron.class.getSimpleName());
			}
		}else if (entities.get(0) instanceof Price) {
			if (EntityDao.getInstance().consumeProductGroupped(module, groupId, (List<Price>) entities, XRoadsCoreUtils::priceHasChanged)) {
				CronDao.getInstance().addScheduleNowIfNotScheduled(SyncPriceCron.class.getSimpleName());
			}
		}else {
			throw new SyncException("Entity not supported");
		}
	}

	@Override
	public BigInteger getNextXRoadSequenceNumber() {
		return EntityDao.getInstance().getNextXRoadSequenceNumber();
	}

	@Override
	public InputStream getResource(URI url) throws SyncException, IOException {
		if (url.getScheme().equals("xroads")) {
			for (XRoadsModule module : getEnabledModules(false).values()) {
				if (module.getName().equals(url.getHost())) {
					return module.getResource(url);
				}
			}

			throw new SyncException("Unknown module " + url.getHost());
		} else {
			if (url.getScheme().contains("http")) {
				HttpGet httpget = new HttpGet(url);
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(httpget);

				return new FilterInputStream(response.getEntity().getContent()) {
					@Override
					public void close() throws IOException {
						try {
							super.close();
						} finally {
							response.close();
							httpClient.close();
						}
					}
				};

			} else {
				return url.toURL().openStream();
			}
		}
	}

	@Override
	public <T extends AbstractEntity> List<T> getActiveEntities(Class<T> entityClass, String lastSourceId, int limit) {
		return EntityDao.getInstance().getActiveEntities(entityClass, lastSourceId, limit);
	}

	@Override
	public <T extends AbstractProductGroupedEntity> Map<String, List<T>> getEntitiesByProductGroup(Class<T> entityClass, Collection<String> productIds) {
		return EntityDao.getInstance().getEntitiesByProductGroup(entityClass, productIds);
	}

	@Override
	public <T extends AbstractEntity> List<T> getEntities(Class<T> class1, String lastSourceId, Integer limit, ModuleStatus filter, XRoadsModule module) {
		return EntityDao.getInstance().getEntities(class1, lastSourceId, limit, new WrapFilter(filter), module);
	}
	
}
