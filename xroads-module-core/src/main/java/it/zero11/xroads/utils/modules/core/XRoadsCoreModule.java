package it.zero11.xroads.utils.modules.core;

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
import it.zero11.xroads.modules.XRoadsCoreService;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.EntityProductGroupedConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.utils.CronUtils;
import it.zero11.xroads.utils.modules.core.cron.CleanOldRevisionCron;
import it.zero11.xroads.utils.modules.core.cron.CleanRunningFreezedCron;
import it.zero11.xroads.utils.modules.core.cron.EmailReportCron;
import it.zero11.xroads.utils.modules.core.cron.GenerateScheduleCron;
import it.zero11.xroads.utils.modules.core.cron.ParamSyncCron;
import it.zero11.xroads.utils.modules.core.cron.RefreshClusterSettingsCron;
import it.zero11.xroads.utils.modules.core.cron.SyncCustomerCron;
import it.zero11.xroads.utils.modules.core.cron.SyncModelCron;
import it.zero11.xroads.utils.modules.core.cron.SyncOrderCron;
import it.zero11.xroads.utils.modules.core.cron.SyncPriceCron;
import it.zero11.xroads.utils.modules.core.cron.SyncProductCron;
import it.zero11.xroads.utils.modules.core.cron.SyncStockCron;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.webservice.XRoadsWebservice;

public class XRoadsCoreModule implements XRoadsModule {
	public static final XRoadsCoreModule INSTANCE = new XRoadsCoreModule();
	private static final Map<String, Class<? extends Runnable>> CRON_INSTANCES = CronUtils.buildCronMap(
			CleanOldRevisionCron.class,
			CleanRunningFreezedCron.class,
			EmailReportCron.class,
			GenerateScheduleCron.class, 
			ParamSyncCron.class,
			SyncModelCron.class,
			SyncPriceCron.class,
			SyncProductCron.class,
			SyncStockCron.class,
			SyncOrderCron.class,
			SyncCustomerCron.class,
			RefreshClusterSettingsCron.class);
	
	private final String name = "core";
	
	private XRoadsCoreModule() {
		
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<String, Class<? extends Runnable>> getCrons() {
		return CRON_INSTANCES;
	}

	@Override
	public Set<Class<? extends XRoadsWebservice<?>>> getWebservices() {
		return Collections.emptySet();
	}

	@Override
	public void configure(String name, XRoadsCoreService xRoadsCoreService) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<IParamType> getParamTypes() {
		return Arrays.asList(ParamType.values());
	}

	@Override
	public <T extends AbstractEntity> EntityConsumer<T> getEntityConsumer(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends AbstractProductGroupedEntity> EntityProductGroupedConsumer<T> getEntityProductGroupedConsumer(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getResource(URI url) throws SyncException {
		throw new SyncException("Resource not found " + url);
	}

}
