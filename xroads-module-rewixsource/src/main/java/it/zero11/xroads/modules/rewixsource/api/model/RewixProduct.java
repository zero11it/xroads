package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class RewixProduct implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String name, code, brand;
	private BigDecimal streetPrice, suggestedPrice, cost, taxable /*original taxable*/ ;

	private Integer catalogRuleId;
	
	private Integer vatClassId;
	
	//Best price including sale/channel/personal discounts
	private BigDecimal bestTaxable;
	
	private String currency;
	
	//private String description, plainDescription;
	@Deprecated
	private Float weight;
	private Integer availability;

	private String madein;
	
	private String intra;

	private boolean online;
	
	private boolean intangible;

	private List<Image> images;
	
	private List<RewixModel> models;
	
	//private List<Translation> descriptions;
	
	private ProductLocalizations productLocalizations;
	
	private List<ProductTag> tags;
	
	private Integer[] hiddenToGroups;
	
	private Integer[] visibleToGroups;
	
	public RewixProduct(){}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
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
/*
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}*/

	@Deprecated
	public Float getWeight() {
		return weight;
	}

	@Deprecated
	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public void setImages(List<Image> images) {
		this.images = images;
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
	
	public boolean isIntangible() {
		return intangible;
	}

	public void setIntangible(boolean intangible) {
		this.intangible = intangible;
	}

	@XmlElementWrapper(name="pictures")
	@XmlElement(name="image")
	public List<Image> getImages() {
		return images;
	}

	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}
	
	@XmlElementWrapper(name="models")
	@XmlElement(name="model")
	public List<RewixModel> getModels() {
		return models;
	}

	public void setModels(List<RewixModel> models) {
		this.models = models;
	}

	public String getIntra() {
		return intra;
	}

	public void setIntra(String intra) {
		this.intra = intra;
	}
	
	public String getMadein() {
		return madein;
	}

	public void setMadein(String madein) {
		this.madein = madein;
	}
/*
	@Deprecated
	@XmlElementWrapper(name="descriptions")
	@XmlElement(name="description")
	public List<Translation> getDescriptions() {
		return descriptions;
	}

	@Deprecated
	public void setDescriptions(List<Translation> descriptions) {
		this.descriptions = descriptions;
	}*/

	@XmlElementWrapper(name="tags")
	@XmlElement(name="tag")
	public List<ProductTag> getTags() {
		return tags;
	}

	public void setTags(List<ProductTag> tags) {
		this.tags = tags;
	}

	@XmlTransient
	public Integer[] getHiddenToGroups() {
		return hiddenToGroups;
	}

	public void setHiddenToGroups(Integer[] hiddenToGroups) {
		this.hiddenToGroups = hiddenToGroups;
	}

	@XmlTransient
	public Integer[] getVisibleToGroups() {
		return visibleToGroups;
	}

	public void setVisibleToGroups(Integer[] visibleToGroups) {
		this.visibleToGroups = visibleToGroups;
	}

	public Integer getCatalogRuleId() {
		return catalogRuleId;
	}

	public void setCatalogRuleId(Integer catalogRuleId) {
		this.catalogRuleId = catalogRuleId;
	}

	public Integer getVatClassId() {
		return vatClassId;
	}

	public void setVatClassId(Integer vatClassId) {
		this.vatClassId = vatClassId;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public BigDecimal getBestTaxable() {
		return bestTaxable;
	}

	public void setBestTaxable(BigDecimal bestTaxable) {
		this.bestTaxable = bestTaxable;
	}
/*
	public String getPlainDescription() {
		return plainDescription;
	}

	public void setPlainDescription(String plainDescription) {
		this.plainDescription = plainDescription;
	}
	*/
	@XmlElement(name="localizations")
	public ProductLocalizations getProductLocalizations() {
		return productLocalizations;
	}

	public void setProductLocalizations(ProductLocalizations productLocalizations) {
		this.productLocalizations = productLocalizations;
	}
}