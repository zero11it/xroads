package it.zero11.xroads.modules;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.AbstractEntityRevision;
import it.zero11.xroads.model.AbstractModelGroupedEntity;
import it.zero11.xroads.model.AbstractProductGroupedEntity;
import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.model.ModuleStatus;
import it.zero11.xroads.sync.SyncException;

public interface XRoadsCoreService {

	String getParameter(XRoadsModule module, IParamType endpoint);

	Integer getParameterAsInteger(XRoadsModule module, IParamType timeout);

	boolean getParameterAsBoolean(XRoadsModule module, IParamType sendEmailOnNewUsers);

	JsonNode getParameterAsJsonNode(XRoadsModule module, IParamType suppliers);

	void updateParam(XRoadsModule module, IParamType paramType, String value);
	
	<T extends AbstractEntity> T getEntity(Class<T> entityClass, String id);

	<T extends AbstractEntityRevision<?>> T  getEntityRevision(Class<T> entityRevisionClass, String id,
			Integer externalReferenceVersion);

	<T extends AbstractProductGroupedEntity> Map<String, List<T>> getEntitiesByProductGroup(Class<T> entityClass, Collection<String> productIds);
	
	<T extends AbstractModelGroupedEntity> Map<String, List<T>> getEntitiesByModelGroup(Class<T> entityClass, Collection<String> modelIds);
	
	<T extends AbstractEntity> List<T> getActiveEntities(Class<T> entityClass, String lastSourceId, int limit );
	
	<T extends AbstractEntity> List<T> getEntities(Class<T> class1, String lastSourceId, Integer limit, ModuleStatus filter, XRoadsModule module);
	
	<T extends AbstractEntity> String getEntityIdByModuleAndSourceId(Class<T> entityClass, XRoadsModule module, String sourceId);

	<T extends AbstractEntity> void updateEntityInTransaction(T entity, XRoadsModule module, Consumer<T> consumer) throws SyncException;
	
	<T extends AbstractEntity> void updateExternalReference(XRoadsModule module, T entity);

	<T extends AbstractEntity> void updateExternalReferenceIdAndVersion(XRoadsModule module, T entity, String id, int version);

	<T extends AbstractEntity> void updateExternalReferenceId(XRoadsModule module, T entity, String id);

	<T extends AbstractEntity> void consume(XRoadsModule module, T entity) throws SyncException;

	<T extends AbstractProductGroupedEntity> void consumeProductGroupped(XRoadsModule module, String groupId, List<T> value) throws SyncException;

	<T extends AbstractXRoadsCronRunnable<?>> void addSchedule(Class<T> cronClass, XRoadsModule module, Date scheduledTime);
	
	<T extends AbstractXRoadsCronRunnable<?>> void addScheduleNowIfNotScheduled(Class<T> cronClass, XRoadsModule module);
	
	BigInteger getNextXRoadSequenceNumber();

	InputStream getResource(URI url) throws SyncException, IOException;

}
