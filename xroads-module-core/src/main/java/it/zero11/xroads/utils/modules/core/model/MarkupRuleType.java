package it.zero11.xroads.utils.modules.core.model;

public enum MarkupRuleType {

	TAXABLE("taxable"),
	SUGGESTED("suggested"),
	RETAIL("retail");

	public final String value;

	private MarkupRuleType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
