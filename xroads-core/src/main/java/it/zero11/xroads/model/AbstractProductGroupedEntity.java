package it.zero11.xroads.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

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
