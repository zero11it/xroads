package it.zero11.xroads.modules.rewixsource.model;

public enum RewixMarkupPriceType {
	Cost("Cost"),
	Suggested("Suggested"),
	StreetPrice("StreetPrice");
	
	public final String value;

    private RewixMarkupPriceType(String value) {
        this.value = value;
    }
}
