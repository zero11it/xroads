package it.zero11.xroads.modules.rewixsource;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.AbstractProductGroupedEntity;
import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.modules.AbstractXRoadsModule;
import it.zero11.xroads.modules.rewixsource.consumers.RewixSourceOrderConsumer;
import it.zero11.xroads.modules.rewixsource.cron.RewixSourceOrderStatusUpdateCron;
import it.zero11.xroads.modules.rewixsource.cron.RewixSourceProductCron;
import it.zero11.xroads.modules.rewixsource.cron.RewixSourceQuantitySyncCron;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.modules.rewixsource.utils.RewixSourceConfig;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.EntityProductGroupedConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.utils.CronUtils;
import it.zero11.xroads.webservice.XRoadsWebservice;

public class XRoadsRewixSourceModule extends AbstractXRoadsModule {	

	public static int MAX_ENTITIES_TO_SYNC = 12000;
	public static int BATCH_SIZE = 200;

	private static final Map<String, Class<? extends Runnable>> CRON_INSTANCES = CronUtils
			.buildCronMap(RewixSourceProductCron.class, RewixSourceQuantitySyncCron.class, RewixSourceOrderStatusUpdateCron.class);

	protected RewixSourceConfig configuration;

	@Override
	public List<IParamType> getParamTypes() {
		return Arrays.asList(RewixSourceParamType.values());
	}
	
	public RewixSourceConfig getConfiguration() {
		if (configuration == null) {
			configuration = new RewixSourceConfig(this);
		}
		return configuration;
	}

	@Override
	public InputStream getResource(URI url) throws SyncException {
		throw new SyncException("Resource not found " + url);
	}

	@Override
	public Set<Class<? extends XRoadsWebservice<?>>> getWebservices() {
		return Collections.emptySet();
	}

	@Override
	public <T extends AbstractEntity> EntityConsumer<T> getEntityConsumer(Class<T> entityClass) {
		if (Order.class.equals(entityClass)) {
			return (EntityConsumer<T>) new RewixSourceOrderConsumer(this);
		}
		return null;
	}

	@Override
	public <T extends AbstractProductGroupedEntity> EntityProductGroupedConsumer<T> getEntityProductGroupedConsumer(
			Class<T> entityClass) {
		return null;
	}

	@Override
	public Map<String, Class<? extends Runnable>> getCrons() {
		return CRON_INSTANCES;
	}
}
