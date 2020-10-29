package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productCountryRestriction")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductCountryRestrictionBean {
	@XmlAttribute
	private Integer stockProductId;
	
    @XmlElement(name = "countryRestriction")
	private List<String> countryRestrictions;

	public Integer getStockProductId() {
		return stockProductId;
	}

	public void setStockProductId(Integer stockProductId) {
		this.stockProductId = stockProductId;
	}

	public List<String> getCountryRestrictions() {
		return countryRestrictions;
	}

	public void setCountryRestrictions(List<String> countryRestrictions) {
		this.countryRestrictions = countryRestrictions;
	}
}
