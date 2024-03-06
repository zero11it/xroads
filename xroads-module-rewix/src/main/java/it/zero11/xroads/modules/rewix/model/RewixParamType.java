package it.zero11.xroads.modules.rewix.model;

import it.zero11.xroads.model.IParamType;

public enum RewixParamType implements IParamType{
	ENDPOINT(null),
	USERNAME(null),
	PASSWORD(null),
	SEND_EMAIL_ON_NEW_USERS("false"),
	TIMEOUT("60"),
	SUPPLIERS("{}"),
	IS_UPDATE_CUSTOMERS("false"),
	IGNORE_MISSING_IMAGES("false"),
	VAT("{}"),
	ORDER_PLATFORMS("{}"),
	ORDER_STATUS_TO_SYNC("[]"),
	ORDER_FREQUENCY_MIN("60"),
	CHECK_NEW_MODELS_FREQUENCY_MIN("60"),
	WAREHOUSES("{}"),
	ORDER_SUBSTATUS_TO_SYNC("[]"),
	ENABLE_PRODUCT_IMPORT("false"),
	TAG_MAP("{\n" + 
			"   \"brand\":1,\n" + 
			"   \"category\":4,\n" + 
			"   \"subcategory\":5,\n" + 
			"   \"season\":11,\n" + 
			"   \"color\":13,\n" + 
			"   \"gender\":26,\n" + 
			"   \"productname\":30,\n" + 
			"   \"made_in\":36,\n" + 
			"   \"packaging\":64,\n" + 
			"   \"textile\":65,\n" + 
			"   \"composition\":66\n" + 
			"}"), 
	ENABLE_ORDER_STATUS_UPDATE("false"), 
	ENABLE_EXPORT_CUSTOMERS("false"),
	IS_REWIX_CUSTOMER_SOURCE("false"),
	ENABLE_EXPORT_INVOICES("false"),
	LAST_CUSTOMERS_SWYNC(""),
	LAST_INVOICE_ID(null),
	MERCHANT_MAP("{}"),
	TRADE_AGENT_MAP("{}"),
	ENABLE_FULL_REWIX_UPDATE_PRODUCT("true"),
	ENABLE_MIXED_ORDERS("false")
	;
	
	public final String defaultValue;
	
	private RewixParamType(String defaultValue){
		this.defaultValue = defaultValue;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}
}