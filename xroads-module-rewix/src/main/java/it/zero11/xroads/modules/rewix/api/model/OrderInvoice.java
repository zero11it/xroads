package it.zero11.xroads.modules.rewix.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="order_invoice")
public class OrderInvoice {
	
	@Id
	private OrderInvoicePK pkey;
	
	
	public OrderInvoice(){}

	
	
	public OrderInvoicePK getPkey() {
		return pkey;
	}

	public void setPkey(OrderInvoicePK pkey) {
		this.pkey = pkey;
	}
	

	
	/*	
		 //bidirectional association! Needed to trick hibernate ;P
	    @SuppressWarnings("unused")
	    @Column(name="country_id", nullable=false, updatable=false, insertable=false)
	    private Integer country;

	    @SuppressWarnings("unused")
	    @Column(name="stock_product_id", nullable=false, updatable=false, insertable=false)
	    private Integer stock_product_id;
	    //----
	    public void setProduct(StockProduct product) {
	    	pkey.setProduct(product);
	    }
	 
	    public StockProduct getProduct(){
	      return pkey.getProduct();
	    }
	 
	    public void setCountry(Country country) {
	    	pkey.setCountry(country);
	    }
	 
	    public Country getCountry() {
	      return pkey.getCountry();
	    }
		*/
		
	

}
