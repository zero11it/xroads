package it.zero11.xroads.utils.modules.core.dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.model.Param;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.utils.EntityManagerUtils;
import it.zero11.xroads.utils.XRoadsUtils;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;
import it.zero11.xroads.utils.modules.core.utils.LocalCache;
import it.zero11.xroads.utils.modules.core.utils.LocalCache.LocalCacheType;
import it.zero11.xroads.utils.modules.core.utils.TransactionWrapper;

public class ParamDao {
	private static ParamDao instance = null;

	public static ParamDao getInstance() {
		if (instance == null) {
			synchronized (ParamDao.class){
				if (instance == null){
					instance = new ParamDao();
				}
			}
		}
		return instance;
	}

	private ParamDao(){
	}	
	
	private String getParamName(XRoadsModule module, IParamType paramType) {
		return module.getName() + "_" + paramType;
	}

	private static String getCacheKey(XRoadsModule module, IParamType paramType) {
		return LocalCache.buildKey(LocalCacheType.PARAMS, module.getName(), paramType.name());
	}
	
	public void updateParam(XRoadsModule module, IParamType paramType, String value) {
		try (TransactionWrapper etw = new TransactionWrapper()){
			try {
				Param param = (Param) etw.getEm().createQuery("from Param where name = :name")
						.setParameter("name", getParamName(module, paramType))
						.getSingleResult();
				param.setValue(value);	
			} catch (NoResultException e) {
				Param param = new Param();
				param.setName( getParamName(module, paramType));
				param.setValue(value);			
				etw.getEm().persist(param);
			}
			etw.commit();
		}

		LocalCache.getInstance().invalidate(getCacheKey(module, paramType));
	}

	public List<Param> getAll() {
		EntityManager em = EntityManagerUtils.createEntityManager();
		try {
			return em.createQuery("from Param order by name", Param.class).getResultList();
		}finally {
			em.close();
		}
	}

	public String getParameter(XRoadsModule module, IParamType paramType){
		return getParameter(module, paramType, true);
	}
	
	public String getParameter(XRoadsModule module, IParamType paramType, final boolean cached){
		LocalCache localCache = LocalCache.getInstance();
		String parval = localCache.getOrGenerate(getCacheKey(module, paramType), (cached) ? LocalCache.LONG_CACHE_TIME : LocalCache.FORCE_REGENERATION_CACHE_TIME, new LocalCache.LocalCacheGenerator<String>() {
			@Override
			public String generate() {
				EntityManager em = EntityManagerUtils.createEntityManager();
				try {
					Param param = em.createQuery("from Param where name = :name ", Param.class)
							.setParameter("name", getParamName(module, paramType))
							.getSingleResult();
					if (param == null){
						return null;
					}else{
						return param.getValue();
					}
				} catch (NoResultException n) {
					return null;
				}finally {
					em.close();
				}
			}
		}, true);
		return parval;
	}
	
	public Integer getParameterAsInteger(XRoadsModule module, IParamType paramType){
		try{
			return Integer.valueOf(getParameter(module, paramType, true));
		}catch(NumberFormatException ex){
			throw new IllegalArgumentException(ex);
		}
	}

	public Float getParameterAsFloat(XRoadsModule module, IParamType paramType){
		try{
			return Float.valueOf(getParameter(module, paramType, true));
		}catch(NumberFormatException ex){
			throw new IllegalArgumentException(ex);
		}
	}

	public boolean getParameterAsBoolean(XRoadsModule module, IParamType paramType){
		String value = getParameter(module, paramType, true);
		return (value != null && (value.equals("yes") || value.equals("1") || value.equals("true")));
	}
	
	public JsonNode getParameterAsJsonNode(XRoadsModule module, IParamType paramType){
		String value = getParameter(module, paramType, true);
		if (value == null) {
			return XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		}else {
			try {
				return XRoadsUtils.OBJECT_MAPPER.readTree(value);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public BigDecimal getParameterAsBigDecimal(XRoadsModule module, IParamType paramType) {
		String value = getParameter(module, paramType, true);
		if (value == null){
			return null;
		}else{
			return new BigDecimal(value);
		}
	}

	public void syncDB(){
		try (TransactionWrapper etw = new TransactionWrapper()){
			Set<String> allParamsNames = new TreeSet<>();
			for (XRoadsModule module : XRoadsCoreServiceBean.getInstance().getEnabledModules(true)){
				allParamsNames.addAll(createParams(etw, module, module.getParamTypes()));
			}

			etw.getEm().flush();
			
			/*List<Param> allParams = etw.getEm().createQuery("from Param").getResultList();
			for (Param param:allParams){
				if (!allParamsNames.contains(param.getName())){
					etw.getEm().remove(param);
				}
			}*/

			etw.commit();
		}
	}

	private List<String> createParams(TransactionWrapper etw, XRoadsModule module, List<IParamType> allParamType) {
		List<String> names = new ArrayList<>();
		for (IParamType paramType:allParamType){
			String name = getParamName(module, paramType);
			names.add(name);
			
			List<Param> params = etw.getEm().createQuery("from Param where name = :name", Param.class)
					.setParameter("name", name)
					.getResultList();
			Param param = null;
			for (Param currentParam:params){
				if (param != null){
					etw.getEm().remove(currentParam);
				}else{
					param = currentParam;
				}
			}

			if (param == null){
				param = new Param();
				param.setName(name);
				param.setValue(paramType.getDefaultValue());
				etw.getEm().persist(param);
			}
		}
		return names;
	}
}
