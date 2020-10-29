package it.zero11.xroads.modules.rewix.api.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productTaxable")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTaxableBean {
	@XmlAttribute
	private String platformUid;
	
	@XmlAttribute
	private String country;
	
	//@XmlAttribute
	//private String currency;

	@XmlAttribute
	private String username;

	@XmlAttribute
	private Integer groupId;

	@XmlAttribute
	private Integer pricePriority;
	
	@XmlAttribute
	private String color;
	
	@XmlAttribute
	private String size;
	
	@XmlAttribute
	private Integer minimumQuantity;
	
	@XmlAttribute
	private BigDecimal taxable;

	@XmlAttribute
	private BigDecimal retailPrice;

	@XmlAttribute
	private BigDecimal suggestedPrice;

	public String getPlatformUid() {
		return platformUid;
	}

	public void setPlatformUid(String platformUid) {
		this.platformUid = platformUid;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	/*public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}*/

	public Integer getPricePriority() {
		return pricePriority;
	}

	public void setPricePriority(Integer pricePriority) {
		this.pricePriority = pricePriority;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getMinimumQuantity() {
		return minimumQuantity;
	}

	public void setMinimumQuantity(Integer minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}

	public BigDecimal getTaxable() {
		return taxable;
	}

	public void setTaxable(BigDecimal taxable) {
		this.taxable = taxable;
	}

	public BigDecimal getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(BigDecimal retailPrice) {
		this.retailPrice = retailPrice;
	}

	public BigDecimal getSuggestedPrice() {
		return suggestedPrice;
	}

	public void setSuggestedPrice(BigDecimal suggestedPrice) {
		this.suggestedPrice = suggestedPrice;
	}
}
