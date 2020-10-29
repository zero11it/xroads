package it.zero11.xroads.modules;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.AbstractProductGroupedEntity;
import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.EntityProductGroupedConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.webservice.XRoadsWebservice;

public interface XRoadsModule {

	public void configure(String name, XRoadsCoreService xRoadsCoreService);

	public <T extends AbstractEntity> EntityConsumer<T> getEntityConsumer(Class<T> entityClass);
	
	public <T extends AbstractProductGroupedEntity> EntityProductGroupedConsumer<T> getEntityProductGroupedConsumer(Class<T> entityClass);

	public String getName();
	
	public Map<String, Class<? extends Runnable>> getCrons();

	public List<IParamType> getParamTypes();

	public InputStream getResource(URI url) throws SyncException;

	public Set<Class<? extends XRoadsWebservice<?>>> getWebservices();
}
