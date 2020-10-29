package it.zero11.xroads.modules.rewix.api.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productModelLot")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductModelLotBean {
	@XmlAttribute(required=true)
	private Integer stockModelId;
	
	@XmlAttribute(required=true)
	private ProductModelLotsType type;
	
	@XmlAttribute(required=true)
	private ProductModelLotsOperationType operation;

	@XmlAttribute(required=true)
	private Integer amount;
	
	@XmlAttribute
	private boolean singleLot = true;
	
	@XmlAttribute(required=true)
	private BigDecimal cost;

	@XmlAttribute(required=true)
	private String reference;

	@XmlAttribute(required=true)
	private Integer warehouseId;
	
	@XmlAttribute(required=true)
	private Integer supplierId;
	
	@XmlAttribute
	private String location;
	
	@XmlAttribute
	private boolean removeAllVirtual = false;

	public Integer getStockModelId() {
		return stockModelId;
	}

	public void setStockModelId(Integer stockModelId) {
		this.stockModelId = stockModelId;
	}

	public ProductModelLotsType getType() {
		return type;
	}

	public void setType(ProductModelLotsType type) {
		this.type = type;
	}

	public ProductModelLotsOperationType getOperation() {
		return operation;
	}

	public void setOperation(ProductModelLotsOperationType operation) {
		this.operation = operation;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public boolean isSingleLot() {
		return singleLot;
	}

	public void setSingleLot(boolean singleLot) {
		this.singleLot = singleLot;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isRemoveAllVirtual() {
		return removeAllVirtual;
	}

	public void setRemoveAllVirtual(boolean removeAllVirtual) {
		this.removeAllVirtual = removeAllVirtual;
	}
}
