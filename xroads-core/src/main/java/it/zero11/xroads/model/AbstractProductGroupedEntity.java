package it.zero11.xroads.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractProductGroupedEntity extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Column(name="product_source_id")
	private String productSourceId;

	public String getProductSourceId() {
		return productSourceId;
	}

	public void setProductSourceId(String productSourceId) {
		this.productSourceId = productSourceId;
	}
}
