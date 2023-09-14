package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "model")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class RewixModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	
	private String model, code, barcode;
	
	@Deprecated
	private String color, size;
	
	private String[] additionalBarcodes;
	
	private String option1;
	
	private String option2;
	
	private String option3;
	
	private Long points;
	
	private BigDecimal streetPrice,suggestedPrice,cost,taxable,bestTaxable;
	
	private BigDecimal price;
	private BigDecimal priceNoDiscount;
	
	private BigDecimal minPrice;
	
	private String currency;
	
	private BigDecimal modelWeight;
	private Integer availability;

	private boolean backorder;
	
	private ModelLocalizations modelLocalizations;
	
	private List<ProductTag> modelTags;

	private Date lastUpdate;
	
	private Integer merchantId;
	
	public RewixModel(){}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String name) {
		this.model = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getTaxable() {
		return taxable;
	}

	public void setTaxable(BigDecimal taxable) {
		this.taxable = taxable;
	}

	public BigDecimal getBestTaxable() {
		return bestTaxable;
	}

	public void setBestTaxable(BigDecimal bestTaxable) {
		this.bestTaxable = bestTaxable;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getPriceNoDiscount() {
		return priceNoDiscount;
	}

	public void setPriceNoDiscount(BigDecimal priceNoDiscount) {
		this.priceNoDiscount = priceNoDiscount;
	}

	public BigDecimal getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	public BigDecimal getModelWeight() {
		return modelWeight;
	}
	
	public void setModelWeight(BigDecimal modelWeight) {
		this.modelWeight = modelWeight;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public BigDecimal getStreetPrice() {
		return streetPrice;
	}

	public void setStreetPrice(BigDecimal streetPrice) {
		this.streetPrice = streetPrice;
	}

	public BigDecimal getSuggestedPrice() {
		return suggestedPrice;
	}

	public void setSuggestedPrice(BigDecimal suggestedPrice) {
		this.suggestedPrice = suggestedPrice;
	}
	
	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	@Deprecated
	@XmlElement(name="color")
	public String getColor() {
		return color;
	}
	
	@Deprecated
	public void setColor(String color) {
		this.color = color;
	}

	@Deprecated
	@XmlElement(name="size")
	public String getSize() {
		return size;
	}

	public String getOption1() {
		return option1;
	}

	public void setOption1(String option1) {
		this.option1 = option1;
		this.size = option1;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	@XmlJavaTypeAdapter(value=JAXBStringURLEncodedToArray.class)
	public String[] getAdditionalBarcodes() {
		return additionalBarcodes;
	}

	public void setAdditionalBarcodes(String[] additionalBarcodes) {
		this.additionalBarcodes = additionalBarcodes;
	}

	public boolean isBackorder() {
		return backorder;
	}

	public void setBackorder(boolean backorder) {
		this.backorder = backorder;
	}

	@XmlElement(name="localizations")
	public ModelLocalizations getModelLocalizations() {
		return modelLocalizations;
	}

	public void setModelLocalizations(ModelLocalizations modelLocalizations) {
		this.modelLocalizations = modelLocalizations;
	}
	
	@XmlElementWrapper(name="modeltags")
	@XmlElement(name="modeltag")
	public List<ProductTag> getModelTags() {
		return modelTags;
	}

	public void setModelTags(List<ProductTag> modelTags) {
		this.modelTags = modelTags;
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}
}
