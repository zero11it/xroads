package it.zero11.xroads.modules.rewix.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.model.RewixParamType;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixConfig {
	
	private String endpoint = "";
	private String username = "";
	private String password = "";
	private Integer timeout = 0;
	private Map<String, Integer> suppliers = new HashMap<>();
	private Map<String, Integer> warehouses = new HashMap<>();
	private Map<String, Integer> vat = new HashMap<>();
	private List<String> orderPlatforms = new ArrayList<>();
	private Integer orderFrequencyMin = 60;
	private Integer checkNewModelsFrequencyMin = 60;
	private boolean sendEmailOnNewUsers = false;
	private boolean ignoreMissingImages = false;
	private List<Integer> orderStatusToSync = new ArrayList<>();
	private List<Integer> orderSubStatusToSync = new ArrayList<>();
	private Map<String, Integer> tagMap;
	private Map<String, String> rewixTradeAgentMap;
	private Map<String, Integer> merchantMap;
	
	public RewixConfig(XRoadsRewixModule module) {
		endpoint = module.getXRoadsCoreService().getParameter(module, RewixParamType.ENDPOINT);
		username = module.getXRoadsCoreService().getParameter(module, RewixParamType.USERNAME);
		password = module.getXRoadsCoreService().getParameter(module, RewixParamType.PASSWORD);
		timeout = module.getXRoadsCoreService().getParameterAsInteger(module, RewixParamType.TIMEOUT);
		sendEmailOnNewUsers = module.getXRoadsCoreService().getParameterAsBoolean(module, RewixParamType.SEND_EMAIL_ON_NEW_USERS);
		ignoreMissingImages = module.getXRoadsCoreService().getParameterAsBoolean(module, RewixParamType.IGNORE_MISSING_IMAGES);
		module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.SUPPLIERS).fields().forEachRemaining((Map.Entry<String, JsonNode> entry ) -> {
			suppliers.put(entry.getKey(), entry.getValue().asInt());
		});
		module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.WAREHOUSES).fields().forEachRemaining((Map.Entry<String, JsonNode> entry ) -> {
			warehouses.put(entry.getKey(), entry.getValue().asInt());
		});	
		module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.VAT).fields().forEachRemaining((Map.Entry<String, JsonNode> entry ) -> {
			vat.put(entry.getKey(), entry.getValue().asInt());
		});	
		
		module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.ORDER_PLATFORMS).elements().forEachRemaining((element) -> {
			orderPlatforms.add(element.asText());
		}); 

		module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.ORDER_STATUS_TO_SYNC).elements().forEachRemaining((element) -> {
			orderStatusToSync.add(element.asInt());
		}); 
		module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.ORDER_SUBSTATUS_TO_SYNC).elements().forEachRemaining((element) -> {
			orderSubStatusToSync.add(element.asInt());
		}); 
		if (orderStatusToSync.size() == 0) {
			orderStatusToSync.add(Constants.ORDER_DROPSHIPPING);
			if (orderSubStatusToSync.size() == 0) {
				orderSubStatusToSync.add(Constants.ORDER_TODISPATCH);
			}
		}
		
		orderFrequencyMin = module.getXRoadsCoreService().getParameterAsInteger(module, RewixParamType.ORDER_FREQUENCY_MIN);
		checkNewModelsFrequencyMin = module.getXRoadsCoreService().getParameterAsInteger(module, RewixParamType.CHECK_NEW_MODELS_FREQUENCY_MIN);

		JsonNode tagMapJson = module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.TAG_MAP);
		tagMap = XRoadsUtils.OBJECT_MAPPER.convertValue(tagMapJson, new TypeReference<Map<String, Integer>>(){});
		
		JsonNode tradeAgentMap = module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.TRADE_AGENT_MAP);
		rewixTradeAgentMap = XRoadsUtils.OBJECT_MAPPER.convertValue(tradeAgentMap, new TypeReference<Map<String, String>>(){});
		
		JsonNode merchantMapJson = module.getXRoadsCoreService().getParameterAsJsonNode(module, RewixParamType.MERCHANT_MAP);
		merchantMap = XRoadsUtils.OBJECT_MAPPER.convertValue(merchantMapJson, new TypeReference<Map<String, Integer>>(){});		
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Map<String, Integer> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(Map<String, Integer> suppliers) {
		this.suppliers = suppliers;
	}

	public Map<String, Integer> getWarehouses() {
		return warehouses;
	}

	public void setWarehouses(Map<String, Integer> warehouses) {
		this.warehouses = warehouses;
	}

	public List<String> getOrderPlatforms() {
		return orderPlatforms;
	}

	public void setOrderPlatforms(List<String> orderPlatforms) {
		this.orderPlatforms = orderPlatforms;
	}

	public Integer getOrderFrequencyMin() {
		return orderFrequencyMin;
	}

	public void setOrderFrequencyMin(Integer orderFrequencyMin) {
		this.orderFrequencyMin = orderFrequencyMin;
	}

	public Integer getCheckNewModelsFrequencyMin() {
		return checkNewModelsFrequencyMin;
	}

	public void setCheckNewModelsFrequencyMin(Integer checkNewModelsFrequencyMin) {
		this.checkNewModelsFrequencyMin = checkNewModelsFrequencyMin;
	}

	public Map<String, Integer> getVat() {
		return vat;
	}

	public void setVat(Map<String, Integer> vat) {
		this.vat = vat;
	}

	public boolean isSendEmailOnNewUsers() {
		return sendEmailOnNewUsers;
	}

	public void setSendEmailOnNewUsers(boolean sendEmailOnNewUsers) {
		this.sendEmailOnNewUsers = sendEmailOnNewUsers;
	}

	public List<Integer> getOrderStatusToSync() {
		return orderStatusToSync;
	}

	public void setOrderStatusToSync(List<Integer> orderStatusToSync) {
		this.orderStatusToSync = orderStatusToSync;
	}

	public Map<String, String> getRewixTradeAgentMap() {
		return rewixTradeAgentMap;
	}

	public List<Integer> getOrderSubStatusToSync() {
		return orderSubStatusToSync;
	}

	public void setOrderSubStatusToSync(List<Integer> orderSubStatusToSync) {
		this.orderSubStatusToSync = orderSubStatusToSync;
	}

	public Map<String, Integer> getTagMap() {
		return tagMap;
	}

	public boolean isIgnoreMissingImages() {
		return ignoreMissingImages;
	}

	public Map<String, Integer> getMerchantMap() {
		return merchantMap;
	}
	
}
