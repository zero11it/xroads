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

	@Deprecated
	@XmlAttribute
	private String username;

	@XmlAttribute
	private Integer merchantId;

	@XmlAttribute
	private Integer groupId;

	@XmlAttribute
	private Integer pricePriority;
	
	@Deprecated
	@XmlAttribute
	private String color;
	
	@Deprecated
	@XmlAttribute
	private String size;

	@XmlAttribute
	private String option1;
	
	@XmlAttribute
	private String option2;
	
	@XmlAttribute
	private String option3;
	
	@XmlAttribute
	private Integer minimumQuantity;
	
	@XmlAttribute
	private Long points;
	
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
	
	@Deprecated
	public String getColor() {
		return color;
	}

	@Deprecated
	public void setColor(String color) {
		this.color = color;
	}

	@Deprecated
	public String getSize() {
		return size;
	}

	@Deprecated
	public void setSize(String size) {
		this.size = size;
	}

	public String getOption1() {
		return option1;
	}

	public void setOption1(String option1) {
		this.option1 = option1;
	}

	public String getOption2() {
		return option2;
	}

	public void setOption2(String option2) {
		this.option2 = option2;
	}

	public String getOption3() {
		return option3;
	}

	public void setOption3(String option3) {
		this.option3 = option3;
	}

	@Deprecated
	public String getUsername() {
		return username;
	}

	@Deprecated
	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
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

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
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
