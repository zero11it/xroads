package it.zero11.xroads.modules.rewix.consumers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.RewixAPI;
import it.zero11.xroads.modules.rewix.api.model.GroupBean;
import it.zero11.xroads.modules.rewix.utils.GroupSearchBean;
import it.zero11.xroads.sync.SyncException;

public abstract class AbstractRewixConsumer {
	protected final Logger log;
	protected RewixAPI api;
	protected XRoadsRewixModule xRoadsModule;
	
	public AbstractRewixConsumer(XRoadsRewixModule xRoadsModule) {
		this.xRoadsModule = xRoadsModule;
		log = Logger.getLogger(getClass());
		api = new RewixAPI(xRoadsModule.getConfiguration().getUsername(), xRoadsModule.getConfiguration().getPassword(), xRoadsModule.getConfiguration().getEndpoint());
	}
	
	public XRoadsRewixModule getXRoadsModule() {
		return xRoadsModule;
	}
	
	/**
	 * Estrae l'id dei gruppi (e se non ci sono li crea) in batch
	 * @param names
	 * @return
	 * @throws SyncException 
	 */
	protected Map<GroupSearchBean, Integer> getOrCreateGroupIds(Set<GroupSearchBean> searchBeans) throws SyncException {
		Map<GroupSearchBean, Integer> result = new HashMap<>();

		for (GroupSearchBean bean : searchBeans) {
			result.put(bean, getOrCreateGroup(bean.getPlatform(), bean.getName()));	
		}

		return result;
	}


	private Integer getOrCreateGroup(String platform, String name) throws SyncException {
		if (name == null) {
			throw new SyncException("Group name mandatory");
		}
		Integer groupId = getGroup(platform, name);	
		if (groupId == null) {
			GroupBean newGrupBean = new GroupBean();	
			newGrupBean.setName(name);
			newGrupBean.setPlatformUid(platform);
			groupId = api.updateUserGroup(newGrupBean);		
		}
		return groupId;
	}

	public Integer getGroup(String platform, String name) throws SyncException {
		if (name == null) {
			throw new SyncException("Group name mandatory");
		}
		return api.getUserGroup(platform, name);
	}
}
