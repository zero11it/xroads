package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;

import javax.persistence.Embeddable;



@Embeddable
public class OrderPropertyPK implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer order_id, property_id;

	
	public OrderPropertyPK(){}


	public OrderPropertyPK(Integer orderId, Integer propertyId) {
		order_id = orderId;
		property_id = propertyId;
	}


	public Integer getOrder_id() {
		return order_id;
	}


	public void setOrder_id(Integer orderId) {
		order_id = orderId;
	}


	public Integer getProperty_id() {
		return property_id;
	}


	public void setProperty_id(Integer propertyId) {
		property_id = propertyId;
	}


	/*

	@ManyToOne
	private Country country;
	
	@ManyToOne
	private StockProduct product;
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public StockProduct getProduct() {
		return product;
	}

	public void setProduct(StockProduct product) {
		this.product = product;
	}
	
	*/
	



}
