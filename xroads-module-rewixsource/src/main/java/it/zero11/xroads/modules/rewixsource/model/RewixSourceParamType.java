package it.zero11.xroads.modules.rewixsource.model;

import it.zero11.xroads.model.IParamType;

public enum RewixSourceParamType implements IParamType{
	API_ENDPOINT(""),
	API_PASSWORD(""),
	API_USER(""),
	CATALOG_LASTQUANTITYREF(""),
	CATALOG_LASTSYNCSTATUS(""),
	CURRENCY(""),
	DESCRIPTION_PLATFORMS("[]"),
	DOWNLOAD_CACHE_PATH(""),
	FEATURES_MARKUP_RULE_PRICE_FILTER("false"),
	LAST_SYNCED_PRODUCT_SOURCE_ID(""),
	LISTING_PLATFORMS("[]"),
	LOCALE_MAP("{}"),
	MERCHANT_CODE(""),
	MARKUP_RULES("{\n" + "\"TAXABLE\" : [],\n" + "\"SUGGESTED\" : [],\n" + "\"RETAIL\" : []\n" + "}"),
	MIN_MARKUP_BASED_ON_BEST_TAXABLE("false"),
	NAME_PLATFORMS("[]"),
	PREFIX_SOURCE_ID(""),
	REWIX_CURRENCY_CONVERSION("1"),
	REWIX_LOCALE("en_US"),
	SUPPLIER_NAME(""),
	TARGET_API_ENDPOINT(""),
	TARGET_API_PASSWORD(""),
	TARGET_API_USER(""),
	TAG_MAP("{}"),
	VAT_CLASS_MAP("{}"),
	VIRTUAL_QUANTITIES("false"),
	WAREHOUSE_NAME("")
	;
	
	public final String defaultValue;
	
	private RewixSourceParamType(String defaultValue){
		this.defaultValue = defaultValue;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}
}