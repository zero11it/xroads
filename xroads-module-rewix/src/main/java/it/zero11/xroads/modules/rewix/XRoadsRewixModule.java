package it.zero11.xroads.modules.rewix;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.AbstractProductGroupedEntity;
import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.model.Price;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.Stock;
import it.zero11.xroads.modules.AbstractXRoadsModule;
import it.zero11.xroads.modules.rewix.consumers.RewixCustomerConsumer;
import it.zero11.xroads.modules.rewix.consumers.RewixModelConsumer;
import it.zero11.xroads.modules.rewix.consumers.RewixOrderStatusConsumer;
import it.zero11.xroads.modules.rewix.consumers.RewixPricesConsumer;
import it.zero11.xroads.modules.rewix.consumers.RewixProductConsumer;
import it.zero11.xroads.modules.rewix.consumers.RewixStockConsumer;
import it.zero11.xroads.modules.rewix.cron.RewixCustomerCron;
import it.zero11.xroads.modules.rewix.cron.RewixInvoiceCron;
import it.zero11.xroads.modules.rewix.cron.RewixOrderCron;
import it.zero11.xroads.modules.rewix.model.RewixParamType;
import it.zero11.xroads.modules.rewix.utils.RewixConfig;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.EntityProductGroupedConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.utils.CronUtils;
import it.zero11.xroads.webservice.XRoadsWebservice;

public class XRoadsRewixModule extends AbstractXRoadsModule {
	private static final Map<String, Class<? extends Runnable>> CRON_INSTANCES = CronUtils.buildCronMap(
			RewixOrderCron.class,
			RewixCustomerCron.class,
			RewixInvoiceCron.class
			);
	protected RewixConfig configuration;
	
	@Override
	public List<IParamType> getParamTypes() {
		return Arrays.asList(RewixParamType.values());
	}

	@Override
	public Map<String, Class<? extends Runnable>> getCrons() {
		return CRON_INSTANCES;
	}

	@Override
	public Set<Class<? extends XRoadsWebservice<?>>> getWebservices() {
		return Collections.emptySet();
	}
	
	public RewixConfig getConfiguration() {
		if (configuration == null) {
			configuration = new RewixConfig(this);
		}
		return configuration;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractEntity> EntityConsumer<T> getEntityConsumer(
			Class<T> entityClass) {
		if (getXRoadsCoreService().getParameterAsBoolean(this, RewixParamType.ENABLE_PRODUCT_IMPORT)) {
			if (Product.class.equals(entityClass)){
				return (EntityConsumer<T>) new RewixProductConsumer(this);
			}else if (Model.class.equals(entityClass)) {
				return (EntityConsumer<T>) new RewixModelConsumer(this);
			}else if (Stock.class.equals(entityClass)) {
				return (EntityConsumer<T>) new RewixStockConsumer(this);
			} else if(Order.class.equals(entityClass) && getXRoadsCoreService().getParameterAsBoolean(this, RewixParamType.ENABLE_ORDER_STATUS_UPDATE)) {
				return (EntityConsumer<T>) new RewixOrderStatusConsumer(this);
			} else if(Customer.class.equals(entityClass) && getXRoadsCoreService().getParameterAsBoolean(this, RewixParamType.IS_UPDATE_CUSTOMERS)) {
				return (EntityConsumer<T>) new RewixCustomerConsumer(this);
			}
		
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractProductGroupedEntity> EntityProductGroupedConsumer<T> getEntityProductGroupedConsumer(
			Class<T> entityClass) {
		if (getXRoadsCoreService().getParameterAsBoolean(this, RewixParamType.ENABLE_PRODUCT_IMPORT)) {
			if (Price.class.equals(entityClass)){
				return (EntityProductGroupedConsumer<T>) new RewixPricesConsumer(this);
			}		
		}
		
		return null;
	}

	@Override
	public InputStream getResource(URI url) throws SyncException {
		throw new SyncException("Resource not found " + url);
	}
}
