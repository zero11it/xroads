package it.zero11.xroads.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractCustomerGroupedEntity extends AbstractEntity{

	@Column(name="customer_source_id")
	private String customerSourceId;

	public String getCustomerSourceId() {
		return customerSourceId;
	}

	public void setCustomerSourceId(String customerSourceId) {
		this.customerSourceId = customerSourceId;
	}
}
