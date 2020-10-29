package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productModelUpdateProduct")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductModelUpdateProductBean {
	@XmlAttribute(required=true)
	private Integer stockModelId;

	@XmlAttribute(required=true)
	private Integer currentStockProductId;
	
	@XmlAttribute(required=true)
	private Integer newStockProductId;

	public Integer getStockModelId() {
		return stockModelId;
	}

	public void setStockModelId(Integer stockModelId) {
		this.stockModelId = stockModelId;
	}

	public Integer getCurrentStockProductId() {
		return currentStockProductId;
	}

	public void setCurrentStockProductId(Integer currentStockProductId) {
		this.currentStockProductId = currentStockProductId;
	}

	public Integer getNewStockProductId() {
		return newStockProductId;
	}

	public void setNewStockProductId(Integer newStockProductId) {
		this.newStockProductId = newStockProductId;
	}
}
