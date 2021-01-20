package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="orders_properties")
public class OrderProperties implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private OrderPropertyPK pkey;
	
	
	public OrderProperties(){}


	public OrderProperties(OrderPropertyPK pkey) {
		this.pkey = pkey;
	}


	public OrderPropertyPK getPkey() {
		return pkey;
	}


	public void setPkey(OrderPropertyPK pkey) {
		this.pkey = pkey;
	}

	
	
	
	

		
	

}
