package it.zero11.xroads.modules.rewixsource.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

//@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class LockModel{
	
	private Integer stock_id, locked, available;
	
	public LockModel(){}
	public LockModel(Integer stock_id, Integer locked, Integer available){
		this.stock_id = stock_id;
		this.locked = locked;
		this.available = available;
	}
	
	@XmlAttribute(name="stock_id")
	public Integer getStock_id() {
		return stock_id;
	}
	public void setStock_id(Integer stockId) {
		stock_id = stockId;
	}
	
	@XmlAttribute(name="locked")
	public Integer getLocked() {
		return locked;
	}
	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	@XmlAttribute(name="available")
	public Integer getAvailable() {
		return available;
	}
	public void setAvailable(Integer available) {
		this.available = available;
	}
}
