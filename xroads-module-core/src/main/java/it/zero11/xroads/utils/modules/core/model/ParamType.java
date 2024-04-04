package it.zero11.xroads.utils.modules.core.model;

import it.zero11.xroads.model.IParamType;

public enum ParamType implements IParamType{
	BASE_URL(null),
	OAUTH_CLIENT_AUTH_URL(null),
	OAUTH_CLIENT_TOKEN_URL(null),
	OAUTH_CLIENT_ID(null),
	OAUTH_CLIENT_SECRET(null),
	NAME("xroads"),
	SMTP_HOST(null),
	SMTP_FROM(null),
	SMTP_USER(null),
	SMTP_PASSWORD(null),
	WARNING_NOTIFICATION_EMAILS(null),
	CRON_POOL_SETTING("{}"),
	MODULES("{}"),
	AUTO_RETRY_INTERVAL_MINUTES("60"),
	LOCALE_BUNDLE_PREFIX("default"),
	;
	
	public final String defaultValue;
	
	private ParamType(String defaultValue){
		this.defaultValue = defaultValue;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}
}