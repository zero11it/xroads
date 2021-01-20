package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;

import javax.persistence.Embeddable;



@Embeddable
public class OrderInvoicePK implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer order_id, fattura_id;

	
	public OrderInvoicePK(){}

	public OrderInvoicePK(Integer order_id, Integer fattura_id){
		this.order_id = order_id;
		this.fattura_id = fattura_id;
	}

	public Integer getOrder_id() {
		return order_id;
	}


	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}


	public Integer getFattura_id() {
		return fattura_id;
	}


	public void setFattura_id(Integer fattura_id) {
		this.fattura_id = fattura_id;
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
