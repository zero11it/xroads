package it.zero11.xroads.utils.modules.core.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;

import it.zero11.xroads.model.AbstractCustomerGroupedEntity;
import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.AbstractEntityRevision;
import it.zero11.xroads.model.AbstractEntityRevisionPK;
import it.zero11.xroads.model.AbstractModelGroupedEntity;
import it.zero11.xroads.model.AbstractProductGroupedEntity;
import it.zero11.xroads.model.ModuleOrder;
import it.zero11.xroads.model.ModuleStatus;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.Stock;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.EntityManagerUtils;
import it.zero11.xroads.utils.XRoadsUtils;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.model.EntityStatus;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.utils.modules.core.model.WrapFilter;
import it.zero11.xroads.utils.modules.core.utils.TransactionWrapper;
import it.zero11.xroads.utils.modules.core.utils.XRoadsCoreUtils;

public class EntityDao {
	private static EntityDao instance = null;

	public static EntityDao getInstance() {
		if (instance == null) {
			synchronized (EntityDao.class){
				if (instance == null){
					instance = new EntityDao();
				}
			}
		}
		return instance;
	}

	private EntityDao(){
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AbstractEntity> EntityStatus getStatuses(Class<T> entityClass, XRoadsModule module) {
		EntityManager em = EntityManagerUtils.createEntityManager();

		try {
			Query sqlQuery =  em.createNativeQuery(
					"SELECT "
							+ "COUNT(CASE WHEN (external_references->(:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION +"' IS NULL) "
							+ " and (external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE + "') IS NULL THEN 1 END) as newQueued, " 
							+ "COUNT(CASE WHEN CAST ((external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION +"') AS INTEGER) = version THEN 1 END) as syncronized, " 
							+ "COUNT(CASE WHEN CAST ((external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION +"') AS INTEGER) != version "
							+ "	and (external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE + "') IS NULL THEN 1 END) as updateQueued, "
							+ "COUNT(CASE WHEN (external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE + "') IS NOT NULL THEN 1 END) as syncError "
							+ " from " + getTableName(em, entityClass));
			if(module.getName() != null) {
				sqlQuery.setParameter("moduleName", module.getName());
				List<Object[]> resultList = sqlQuery.getResultList();
				if(resultList != null) {
					EntityStatus result = new EntityStatus();
					result.setEntityClass(entityClass);
					result.setReferenceTime(Instant.now());
					result.setNewQueued(Long.parseLong(resultList.get(0)[0].toString()));
					result.setSyncronized(Long.parseLong(resultList.get(0)[1].toString()));
					result.setUpdateQueued(Long.parseLong(resultList.get(0)[2].toString()));
					result.setSyncError(Long.parseLong(resultList.get(0)[3].toString()));
					return result; 
				} else {
					return null;
				}
			} else {
				return null;
			}
		}finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractEntity> List<T> getActiveEntities(Class<T> entityClass, String lastSourceId, int limit ) {
		EntityManager em = EntityManagerUtils.createEntityManager();
		String sql = "select * from " + getTableName(em, entityClass);

		if(Product.class.isAssignableFrom(entityClass)) {
			sql += (" where online = true");
		} else if(Stock.class.isAssignableFrom(entityClass)) {
			sql += (" where availability > 0");
		} else if(Order.class.isAssignableFrom(entityClass)) {
			sql += (" where status in (0, 2, 3)");
		} else {
			throw new UnsupportedOperationException();
		}

		if(lastSourceId != null && !lastSourceId.isEmpty()) {
			sql += (" and source_id > :source_id");
		}

		sql += (" ORDER BY source_id  LIMIT " + limit);
		Query query = em.createNativeQuery(sql, entityClass);
		if(lastSourceId != null && !lastSourceId.isEmpty()) {
			query.setParameter("source_id", lastSourceId);
		}
		List<T> itemsList = query.getResultList();
		return itemsList;
	}

	public <T extends AbstractEntity> T getEntity(Class<T> entityClass, String id) {
		EntityManager em = EntityManagerUtils.createEntityManager();
		try {
			return em.find(entityClass, id);
		}finally {
			em.close();
		}
	}

	public <T extends AbstractEntityRevision<?>> T getEntityRevision(Class<T> entityRevisionClass, String id,
			Integer version) {
		if (id == null || version == null) {
			return null;
		}
		
		EntityManager em = EntityManagerUtils.createEntityManager();
		try {
			return em.find(entityRevisionClass, new AbstractEntityRevisionPK(id, version));
		}finally {
			em.close();
		}
	}
	
	public <T extends AbstractEntity> void updateEntityInTransaction(T entity, XRoadsModule module, Consumer<T> consumer) {
		updateEntityInTransaction(entity, (em, persistedEntity)->{
			consumer.accept(persistedEntity);
			if (em.unwrap(Session.class).isDirty()) {
				persistedEntity.setVersion(persistedEntity.getVersion() + 1); 
				XRoadsCoreUtils.setExternalReference(persistedEntity, module.getName(), XRoadsUtils.getExternalReferenceId(persistedEntity, module), persistedEntity.getVersion());
			}
		});
	}
	
	private <T extends AbstractEntity> void updateEntityInTransaction(T entity, BiConsumer<EntityManager, T> consumer) {
		try(TransactionWrapper tw = new TransactionWrapper()){
			@SuppressWarnings("unchecked")
			T persistedEntity = tw.getEm().find((Class<T>) entity.getClass(), entity.getSourceId(), LockModeType.PESSIMISTIC_WRITE);
			consumer.accept(tw.getEm(), persistedEntity);
			tw.commit();
		}
	}

	public <T extends AbstractEntity> void updateExternalReference(T entity, XRoadsModule module) {
		updateExternalReferenceIdAndVersion(entity, module, XRoadsUtils.getExternalReferenceId(entity, module), entity.getVersion());
	}

	public <T extends AbstractEntity> void updateExternalReferenceId(T entity, XRoadsModule module, String id) {
		updateExternalReferenceIdAndVersion(entity, module, id, entity.getVersion());
	}

	public <T extends AbstractEntity> void updateExternalReferenceIdAndVersion(T entity, XRoadsModule module, String id, int version) {
		updateEntityInTransaction(entity, (em, persistedEntity)->{
			XRoadsCoreUtils.setExternalReference(persistedEntity, module.getName(), id, version);
		});
	}

	public <T extends AbstractEntity> void updateExternalReferenceLastError(T entity, XRoadsModule module, Throwable e) {
		updateEntityInTransaction(entity, (em, persistedEntity)->{
			XRoadsCoreUtils.setExternalReferenceLastError(persistedEntity, module.getName(), e);
		});
	}

	public <T extends AbstractModelGroupedEntity> boolean updateExternalReferenceMarkForRetryInAllModulesByModelGroup(Class<T> entityClass, List<String> modelIds) {
		boolean changed = false;
		try(TransactionWrapper tw = new TransactionWrapper()){
			List<T> entities = getEntitiesByModelGroup(tw.getEm(), entityClass, modelIds);
			for (T entity : entities) {
				changed |= XRoadsCoreUtils.setExternalReferenceMarkForRetryInAllModules(entity);
			}
			tw.commit();
		}
		return changed;
	}

	public <T extends AbstractCustomerGroupedEntity> boolean updateExternalReferenceMarkForRetryInAllModulesByCustomerGroup(Class<T> entityClass, List<String> customerIds) {
		boolean changed = false;
		try(TransactionWrapper tw = new TransactionWrapper()){
			List<T> entities = getEntitiesByCustomerGroup(tw.getEm(), entityClass, customerIds);
			for (T entity : entities) {
				changed |= XRoadsCoreUtils.setExternalReferenceMarkForRetryInAllModules(entity);
			}
			tw.commit();
		}
		return changed;
	}

	public <T extends AbstractProductGroupedEntity> boolean updateExternalReferenceMarkForRetryInAllModulesByProductGroup(Class<T> entityClass, List<String> productIds) {
		boolean changed = false;
		try(TransactionWrapper tw = new TransactionWrapper()){
			List<T> entities = getEntitiesByProductGroup(tw.getEm(), entityClass, productIds);
			for (T entity : entities) {
				changed |= XRoadsCoreUtils.setExternalReferenceMarkForRetryInAllModules(entity);
			}
			tw.commit();
		}
		return changed;
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractProductGroupedEntity> List<T> getEntitiesByProductGroup(EntityManager em, Class<T> entityClass,
			Collection<String> productIds) {
		String sql = "select * from " + getTableName(em, entityClass) + " e where product_source_id in (:productIds) order by product_source_id for update";
		Query query = em.createNativeQuery(sql, entityClass);
		query.setParameter("productIds", productIds);
		List<T> productGroppedList = query.getResultList();
		return productGroppedList;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends AbstractCustomerGroupedEntity> List<T> getEntitiesByCustomerGroup(EntityManager em, Class<T> entityClass,
			Collection<String> customerIds) {
		String sql = "select * from " + getTableName(em, entityClass) + " e where customer_source_id in (:customerIds) order by customer_source_id for update";
		Query query = em.createNativeQuery(sql, entityClass);
		query.setParameter("customerIds", customerIds);
		List<T> customerGroppedList = query.getResultList();
		return customerGroppedList;
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractModelGroupedEntity> List<T> getEntitiesByModelGroup(EntityManager em, Class<T> entityClass,
			Collection<String> modelIds) {
		String sql = "select * from " + getTableName(em, entityClass) + " e where model_source_id in (:modelIds) order by model_source_id for update";
		Query query = em.createNativeQuery(sql, entityClass);
		query.setParameter("modelIds", modelIds);
		List<T> productGroppedList = query.getResultList();
		return productGroppedList;
	}
	
	public <T extends AbstractEntity> String getEntityIdByModule(Class<T> entityClass, XRoadsModule module, String sourceId) {
		EntityManager em = EntityManagerUtils.createEntityManager();
		String sql = "select source_id"
				+ " from " + getTableName(em, entityClass) + " e where external_references-> (:moduleName) ->> '" + XRoadsJsonKeys.EXTERNAL_REFERENCE_ID + "' = :source_id";
		Query query = em.createNativeQuery(sql);
		query.setParameter("moduleName", module.getName());
		query.setParameter("source_id", sourceId);
		return (String) query.getSingleResult();
	}
	
	public <T extends AbstractProductGroupedEntity> Map<String, List<T>> getEntitiesByProductGroup(Class<T> entityClass, Collection<String> productIds) {
		EntityManager em = EntityManagerUtils.createEntityManager();
		try {
			List<T> productGroppedList = getEntitiesByProductGroup(em, entityClass, productIds);
			return productGroppedList.stream().collect(Collectors.groupingBy(T::getProductSourceId));
		}finally {
			em.close();
		}	
	}
	
	public <T extends AbstractModelGroupedEntity> Map<String, List<T>> getEntitiesByModelGroup(Class<T> entityClass, Collection<String> modelIds) {
		EntityManager em = EntityManagerUtils.createEntityManager();
		try {
			List<T> productGroppedList = getEntitiesByModelGroup(em, entityClass, modelIds);
			return productGroppedList.stream().collect(Collectors.groupingBy(T::getModelSourceId));
		}finally {
			em.close();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AbstractEntity> List<T> getEntities(Class<T> class1, String lastSourceId, Integer limit, WrapFilter filter, XRoadsModule module) {
		EntityManager em = EntityManagerUtils.createEntityManager();
		try {
			Query sqlQuery; 
			String stringQuey = "select * from " + getTableName(em, class1) + " e ";
			if(filter.getModuleStatus() != null) {
				stringQuey += getFilter(filter.getModuleStatus()); 
			}
			if(lastSourceId != null && !lastSourceId.isEmpty()) {
				if(filter.getModuleStatus() != null)
					stringQuey += (" and source_id > :source_id");
				else 
					stringQuey += (" where source_id > :source_id");
			}
			if(filter.getSearchKey() != null) {
				if(filter.getModuleStatus() == null)	{
					stringQuey += " where source_id LIKE :searchKey ";
				} else {
					stringQuey += " and source_id LIKE :searchKey ";
				}
			}
			stringQuey += " ORDER BY source_id";
			if(limit != null) {
				stringQuey += " LIMIT " + limit ;
			}
			sqlQuery = em.createNativeQuery(stringQuey, class1);
			if(filter.getModuleStatus() != null) {
				sqlQuery.setParameter("moduleName", module.getName());
			}
			if(lastSourceId != null && !lastSourceId.isEmpty()) {
				sqlQuery.setParameter("source_id", lastSourceId);
			}
			if(filter != null && filter.getSearchKey() != null) {
				sqlQuery.setParameter("searchKey", "%" + filter.getSearchKey() + "%");
			}
			return  sqlQuery.getResultList();
		}finally {
			em.close();
		}	
		
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AbstractEntity> List<T> getEntities(Class<T> class1, Integer offset, Integer limit, WrapFilter filter, ModuleOrder orderBy, XRoadsModule module) {
		EntityManager em = EntityManagerUtils.createEntityManager();
		try {
			Query sqlQuery; 
			String stringQuey = "select * from " + getTableName(em, class1) + " e ";
			if(filter != null && filter.getModuleStatus() != null) {
				stringQuey += getFilter(filter.getModuleStatus()); 
			}
			if(filter != null && filter.getSearchKey() != null) {
				if(filter.getModuleStatus() == null)	{
					stringQuey += " where source_id LIKE :searchKey ";
				} else {
					stringQuey += " and source_id LIKE :searchKey ";
				}
			}
			stringQuey += getOrder(orderBy);
			if(limit != null) {
				stringQuey += " LIMIT " + limit ;
			}
			if(offset != null) {
				stringQuey += " OFFSET " + offset;
			}
			sqlQuery = em.createNativeQuery(stringQuey, class1);
			if(filter != null && filter.getModuleStatus() != null || (orderBy != null && orderBy.equals(ModuleOrder.LAST_ERROR_DATE)))
				sqlQuery.setParameter("moduleName", module.getName());
			if(filter != null && filter.getSearchKey() != null)
				sqlQuery.setParameter("searchKey", "%" + filter.getSearchKey() + "%");
			return  sqlQuery.getResultList();
		}finally {
			em.close();
		}	
	}

	private String getFilter(ModuleStatus filterType) {
		switch (filterType) {
		case SYNCED:
			return " where CAST ((external_references -> (:moduleName) ->> '" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION + "') AS INTEGER) = version ";
		case NOT_PROCESSED:
			return  " where ((CAST ((external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION + "') AS INTEGER) != version)"
					+ " or (CAST ((external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION + "') AS INTEGER) is Null))"
					+ " and (external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE + "') IS NULL ";
		case DIFFERENT_VERSIONS:
			return " where CAST ((external_references-> (:moduleName) ->> '" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION + "') AS INTEGER) != version ";
		case TO_SYNC:
			OffsetDateTime retryTimeLimit = OffsetDateTime.now().minusMinutes(ParamDao.getInstance().getParameterAsInteger(XRoadsCoreModule.INSTANCE, ParamType.AUTO_RETRY_INTERVAL_MINUTES));
			return " where ((external_references -> (:moduleName) -> '" + XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE + "') IS NULL"  
					+ "     or (external_references -> (:moduleName) ->> '" + XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE + "') < '" + retryTimeLimit.toString() + "') "
					+ "and ((external_references-> (:moduleName) -> '" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION + "') IS NULL"
					+ "     or CAST ((external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION + "') AS INTEGER) != version) ";	
		case SYNC_ERRORS:
			return " where ((CAST ((external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION + "') AS INTEGER) != version)"
					+ " or (CAST ((external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION + "') AS INTEGER) is Null))"
					+ " and (external_references-> (:moduleName)->>'" + XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE + "') IS NOT NULL ";
		default:
			return "";
		}
	}
	
	private String getOrder(ModuleOrder orderBy) {
		switch (orderBy != null ? orderBy : ModuleOrder.SOURCE_ID) {
		case LAST_ERROR_DATE:
			return "ORDER BY external_references-> (:moduleName) -> '" + XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE + "' ASC NULLS FIRST";			
		default:
			return "ORDER BY source_id";
		}
	}

	public <T extends AbstractEntity> Integer countItems(Class<T> entityClass, WrapFilter filter,  XRoadsModule module) {
		EntityManager em = EntityManagerUtils.createEntityManager();
		try {
			Query hqlQuery; 
			String stringQuey = "select count(*) from " + getTableName(em, entityClass) + " e ";
			if(filter != null && filter.getModuleStatus() != null) {
				stringQuey += getFilter(filter.getModuleStatus());
			}
			if(filter != null && filter.getSearchKey() != null) {
				if(filter.getModuleStatus() == null)	{
					stringQuey += " where source_id LIKE :searchKey";
				} else {
					stringQuey += " and source_id LIKE :searchKey";
				}
			}
			hqlQuery = em.createNativeQuery(stringQuey);
			if(filter != null && filter.getModuleStatus() != null)
				hqlQuery.setParameter("moduleName", module.getName());
			if(filter != null && filter.getSearchKey() != null)
				hqlQuery.setParameter("searchKey", "%" + filter.getSearchKey() + "%");
			return  ((BigInteger) hqlQuery.getSingleResult()).intValue();
		}finally {
			em.close();
		}	
	}

	private String getTableName(EntityManager em, Class<?> entityClass) {
		SessionImpl session = em.unwrap(SessionImpl.class);
		EntityPersister entityPersister = session.getMetamodel().entityPersister(entityClass);

		if (entityPersister instanceof AbstractEntityPersister) {
			AbstractEntityPersister persisterImpl = (AbstractEntityPersister) entityPersister;
			//String rootTableName = persisterImpl.getRootTableName();
			return persisterImpl.getTableName();
		} else {
			throw new RuntimeException("Unexpected persister type; a subtype of AbstractEntityPersister expected.");
		}
	}

	public BigInteger getNextXRoadSequenceNumber() {
		try(TransactionWrapper tw = new TransactionWrapper()){
			Query q = tw.getEm().createNativeQuery("SELECT nextval('xroads_id_generator')");
			BigInteger result = (BigInteger) q.getSingleResult();
			tw.commit();
			return result;
		}
	}

	public <T extends AbstractEntity> boolean consume(XRoadsModule module, T entity, BiFunction<T, T, Boolean> hasChangesFuction) {
		@SuppressWarnings("unchecked")
		Class<T> entityClass = (Class<T>) entity.getClass();
		boolean changed = false;
		try(TransactionWrapper tw = new TransactionWrapper()){
			T existingEntity = tw.getEm().find(entityClass, entity.getSourceId(), LockModeType.PESSIMISTIC_WRITE);
			if (existingEntity == null) {
				entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				XRoadsCoreUtils.setExternalReference(entity, module.getName(), entity.getSourceId(), entity.getVersion());
				tw.getEm().persist(entity);
				changed = true;
			}else if (hasChangesFuction.apply(existingEntity, entity)) {
				entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				entity.setVersion(existingEntity.getVersion() + 1);
				entity.setExternalReferences(existingEntity.getExternalReferences());
				String sourceId = XRoadsUtils.getExternalReferenceId(entity, module);
				XRoadsCoreUtils.setExternalReference(entity, module.getName(), sourceId != null ? sourceId : entity.getSourceId(), entity.getVersion());
				tw.getEm().merge(entity);
				changed = true;
			}
			
			if (changed) {
				tw.commit();
			}
		}
		
		return changed;
	}

	public <T extends AbstractProductGroupedEntity> boolean consumeProductGroupped(XRoadsModule module, String groupId, List<T> entities, BiFunction<T, T, Boolean> hasChangesFuction) {
		@SuppressWarnings("unchecked")
		Class<T> entityClass = (Class<T>) entities.get(0).getClass();

		boolean toUpdateAll = false;
		try(TransactionWrapper tw = new TransactionWrapper()){
			String sql = "select * from " + getTableName(tw.getEm(), entityClass) + " e where product_source_id = :groupId order by product_source_id for update";
			Query query = tw.getEm().createNativeQuery(sql, entityClass);
			query.setParameter("groupId", groupId);
			List<T> existingPrices = query.getResultList();
			
			int maxVersion = -1;
			Map<T, T> priceToExistingPrice = new HashMap<T, T>();
			for (T existingPrice : existingPrices) {
				maxVersion = Math.max(maxVersion, existingPrice.getVersion());
				boolean found = false;
				for (T price : entities) {
					if (price.getSourceId().equals(existingPrice.getSourceId())) {
						priceToExistingPrice.put(price, existingPrice);
						found = true;
						try {
							if (hasChangesFuction.apply(price, existingPrice)) {
								toUpdateAll = true;
							}
						}catch (Exception e) {
							// TODO: handle exception
						}
						break;
					}							
				}
				if (!found) {
					toUpdateAll = true;
					//FIXME: we should mark it to be deleted ! and when all module have syncronized really deleting it !
					tw.getEm().remove(existingPrice);
				}
			}
			
			if (existingPrices.size() != entities.size()) {
				toUpdateAll = true;
			}
			
			if (toUpdateAll) {
				maxVersion = Math.max(maxVersion, 1);
				for (T price : entities) {
					T existingEntity = priceToExistingPrice.get(price);
					price.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					if (existingEntity == null) {
						XRoadsCoreUtils.setExternalReference(price, module.getName(), price.getSourceId(), price.getVersion());
						tw.getEm().persist(price);
					}else {
						price.setVersion(maxVersion + 1);
						price.setExternalReferences(existingEntity.getExternalReferences());
						String sourceId = XRoadsUtils.getExternalReferenceId(price, module);
						XRoadsCoreUtils.setExternalReference(price, module.getName(), sourceId != null ? sourceId : price.getSourceId(), price.getVersion());
						tw.getEm().merge(price);
					}		
				}
			}
			
			tw.commit();
		}
		
		return toUpdateAll;
	}

	public <Y extends AbstractEntity, T extends AbstractEntityRevision<Y>> void cleanOldRevision(Class<T> revisionClass, Class<Y> entityClass) {
		int count = 0;
		do {
			try(TransactionWrapper tw = new TransactionWrapper()){
				Query query = tw.getEm().createNativeQuery("delete from " + getTableName(tw.getEm(), revisionClass) + 
						" where (source_id, version) in ( " + 
						"  select source_id , version from "  + getTableName(tw.getEm(), revisionClass) +  " entityrevision " + 
						"    where not exists (" +
						"      select * from (" +
						"        select source_id,cast(((jsonb_each(external_references)).value ->> 'v') as int) as value " +
						"        from "  + getTableName(tw.getEm(), entityClass) +  " entity" + 
						"        where entity.source_id = entityrevision.source_id" +
						"      ) as existingentity" +
						"      where existingentity.value <= entityrevision.\"version\"" +
						"    ) " + 
						"  order by source_id, \"version\"  " + 
						"  limit 1000 " + 
						")");
				
				count = query.executeUpdate();
				tw.commit();
			}
		}while (count > 0);
	}
}
