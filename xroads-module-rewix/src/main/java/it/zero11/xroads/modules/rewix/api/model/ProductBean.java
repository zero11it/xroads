package it.zero11.xroads.modules.rewix.api.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductBean {
	@XmlAttribute
	private Integer stockProductId;

	@XmlAttribute
	private String code;
	
	@XmlAttribute
	private String brand;
	
	@XmlAttribute
	private String name;

	@XmlAttribute
	private BigDecimal cost;
	
	@XmlAttribute
	private Integer vatClassId;

	@XmlAttribute
	private Boolean intangible;
	
	@XmlAttribute
	private Boolean hiddenFromCatalog;
	
	@XmlAttribute
	private Float weight;
	
	@XmlAttribute
	private Integer priority;
	
	@XmlAttribute
	private String intra;
	
	@XmlAttribute
	private String madeIn;
	
	@XmlAttribute
	private Boolean online;
	
	@XmlAttribute
	private String supplierCode;

	

	public Integer getStockProductId() {
		return stockProductId;
	}

	public void setStockProductId(Integer stockProductId) {
		this.stockProductId = stockProductId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public Integer getVatClassId() {
		return vatClassId;
	}

	public void setVatClassId(Integer vatClassId) {
		this.vatClassId = vatClassId;
	}

	public Boolean getIntangible() {
		return intangible;
	}

	public void setIntangible(Boolean intangible) {
		this.intangible = intangible;
	}

	public Boolean getHiddenFromCatalog() {
		return hiddenFromCatalog;
	}

	public void setHiddenFromCatalog(Boolean hiddenFromCatalog) {
		this.hiddenFromCatalog = hiddenFromCatalog;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getIntra() {
		return intra;
	}

	public void setIntra(String intra) {
		this.intra = intra;
	}

	public String getMadeIn() {
		return madeIn;
	}

	public void setMadeIn(String madeIn) {
		this.madeIn = madeIn;
	}

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}
}
