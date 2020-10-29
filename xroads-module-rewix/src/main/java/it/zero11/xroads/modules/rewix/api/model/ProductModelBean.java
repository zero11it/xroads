package it.zero11.xroads.modules.rewix.api.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import it.zero11.xroads.modules.rewix.utils.JAXBStringURLEncodedToArray;


@XmlRootElement(name = "productModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductModelBean {
	@XmlAttribute(required=true)
	private Integer stockProductId;
	
	@XmlAttribute
	private Integer stockModelId;

	@XmlAttribute
	private BigDecimal cost;
	
	@XmlAttribute
	private String code;

	@XmlAttribute(required=true)
	private String size;

	@XmlAttribute
	private String color;
	
	@XmlAttribute
	private boolean generateBarcode = false;

	@XmlAttribute
	private String barcode;
	
	@XmlAttribute
	@XmlJavaTypeAdapter(value=JAXBStringURLEncodedToArray.class)
	private String[] additionalBarcode;

	@XmlAttribute
	private Integer priority;
	
	@XmlAttribute
	private Boolean backorder;

	@XmlAttribute
	private Float modelWeight;
	
	public Integer getStockProductId() {
		return stockProductId;
	}

	public void setStockProductId(Integer stockProductId) {
		this.stockProductId = stockProductId;
	}

	public Integer getStockModelId() {
		return stockModelId;
	}

	public void setStockModelId(Integer stockModelId) {
		this.stockModelId = stockModelId;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String[] getAdditionalBarcode() {
		return additionalBarcode;
	}

	public void setAdditionalBarcode(String[] additionalBarcode) {
		this.additionalBarcode = additionalBarcode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isGenerateBarcode() {
		return generateBarcode;
	}

	public void setGenerateBarcode(boolean generateBarcode) {
		this.generateBarcode = generateBarcode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Boolean getBackorder() {
		return backorder;
	}

	public void setBackorder(Boolean backorder) {
		this.backorder = backorder;
	}

	public Float getWeight() {
		return modelWeight;
	}

	public void setWeight(Float weight) {
		this.modelWeight = weight;
	}
	
}
