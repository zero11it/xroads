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
	private String option1;

	@XmlAttribute(required=true)
	private String option2;

	@XmlAttribute(required=true)
	private String option3;
	
	@Deprecated
	@XmlAttribute(required=true)
	private String size;

	@Deprecated
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

	@XmlAttribute
	private Integer amount;

	@XmlAttribute
	private Integer lockedAmount;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Deprecated
	public String getSize() {
		return size;
	}
	
	@Deprecated
	public void setSize(String size) {
		this.size = size;
	}
	
	@Deprecated
	public String getColor() {
		return color;
	}
	
	@Deprecated
	public void setColor(String color) {
		this.color = color;
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

	public String[] getAdditionalBarcode() {
		return additionalBarcode;
	}

	public void setAdditionalBarcode(String[] additionalBarcode) {
		this.additionalBarcode = additionalBarcode;
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

	public Float getModelWeight() {
		return modelWeight;
	}

	public void setModelWeight(Float modelWeight) {
		this.modelWeight = modelWeight;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getLockedAmount() {
		return lockedAmount;
	}

	public void setLockedAmount(Integer lockedAmount) {
		this.lockedAmount = lockedAmount;
	}
}
