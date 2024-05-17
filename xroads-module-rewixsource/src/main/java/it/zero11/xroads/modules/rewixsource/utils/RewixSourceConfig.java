package it.zero11.xroads.modules.rewixsource.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixSourceConfig {
	
	private final String apiEndpoint;
	private final String apiUser;
	private final String apiPassword;
	private final String targetApiEndpoint;
	private final String targetApiUser;
	private final String targetApiPassword;
	private final String currency;
	private final String downloadCachePath;
	private final String locale;
	private final JsonNode localeMap;
	private String merchantCode;
	private final String prefixSourceId;
	private final JsonNode tagMap;
	private final String supplierName;
	private final Boolean virtualQuantities;
	private final String warehouseName;

	private List<String> listingPlatforms = new ArrayList<>();
	private List<String> descriptionPlatforms = new ArrayList<>();
	private List<String> namePlatforms = new ArrayList<>();
	private List<String> optionPlatforms = new ArrayList<>();
	private Map<String, String> vatClassMap;
	
	public RewixSourceConfig(XRoadsRewixSourceModule xRoadsModule) {
		apiEndpoint = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.API_ENDPOINT);
		apiUser = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.API_USER);
		apiPassword = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.API_PASSWORD);
		targetApiEndpoint = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.TARGET_API_ENDPOINT);
		targetApiUser = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.TARGET_API_USER);
		targetApiPassword = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.TARGET_API_PASSWORD);
		currency = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.CURRENCY);
		downloadCachePath = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.DOWNLOAD_CACHE_PATH);
		locale = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.REWIX_LOCALE);
		localeMap = xRoadsModule.getXRoadsCoreService().getParameterAsJsonNode(xRoadsModule, RewixSourceParamType.LOCALE_MAP);
		merchantCode = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.MERCHANT_CODE);
		prefixSourceId =  xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.PREFIX_SOURCE_ID);
		JsonNode vatClassJsonMap = xRoadsModule.getXRoadsCoreService().getParameterAsJsonNode(xRoadsModule, RewixSourceParamType.VAT_CLASS_MAP);
		vatClassMap = XRoadsUtils.OBJECT_MAPPER.convertValue(vatClassJsonMap, new TypeReference<Map<String, String>>(){});
		
		xRoadsModule.getXRoadsCoreService().getParameterAsJsonNode(xRoadsModule, RewixSourceParamType.LISTING_PLATFORMS)
		.elements().forEachRemaining((element) -> {
			listingPlatforms.add(element.asText());
		});
		xRoadsModule.getXRoadsCoreService().getParameterAsJsonNode(xRoadsModule, RewixSourceParamType.DESCRIPTION_PLATFORMS)
		.elements().forEachRemaining((element) -> {
			descriptionPlatforms.add(element.asText());
		});
		xRoadsModule.getXRoadsCoreService().getParameterAsJsonNode(xRoadsModule, RewixSourceParamType.NAME_PLATFORMS)
		.elements().forEachRemaining((element) -> {
			namePlatforms.add(element.asText());
		});
		xRoadsModule.getXRoadsCoreService().getParameterAsJsonNode(xRoadsModule, RewixSourceParamType.OPTION_PLATFORMS)
		.elements().forEachRemaining((element) -> {
			optionPlatforms.add(element.asText());
		});
		tagMap = xRoadsModule.getXRoadsCoreService().getParameterAsJsonNode(xRoadsModule, RewixSourceParamType.TAG_MAP);
		supplierName = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.SUPPLIER_NAME);
		virtualQuantities = xRoadsModule.getXRoadsCoreService().getParameterAsBoolean(xRoadsModule, RewixSourceParamType.VIRTUAL_QUANTITIES);
		warehouseName = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.WAREHOUSE_NAME);
	}

	public String getApiEndpoint() {
		return apiEndpoint;
	}

	public String getApiUser() {
		return apiUser;
	}

	public String getApiPassword() {
		return apiPassword;
	}

	public String getTargetApiEndpoint() {
		return targetApiEndpoint;
	}

	public String getTargetApiUser() {
		return targetApiUser;
	}

	public String getTargetApiPassword() {
		return targetApiPassword;
	}

	public String getCurrency() {
		return currency;
	}

	public String getDownloadCachePath() {
		return downloadCachePath;
	}

	public String getLocale() {
		return locale;
	}

	public JsonNode getLocaleMap() {
		return localeMap;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public String getPrefixSourceId() {
		return prefixSourceId;
	}

	public JsonNode getTagMap() {
		return tagMap;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public Boolean getVirtualQuantities() {
		return virtualQuantities;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public List<String> getListingPlatforms() {
		return listingPlatforms;
	}

	public List<String> getDescriptionPlatforms() {
		return descriptionPlatforms;
	}

	public List<String> getNamePlatforms() {
		return namePlatforms;
	}

	public List<String> getOptionPlatforms() {
		return optionPlatforms;
	}

	public Map<String, String> getVatClassMap() {
		return vatClassMap;
	}

}
