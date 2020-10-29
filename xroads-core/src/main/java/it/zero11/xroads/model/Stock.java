package it.zero11.xroads.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
public class Stock extends AbstractModelGroupedEntity {
	private static final long serialVersionUID = 1L;

	private Integer availability;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	private String supplier;

	private String warehouse;

	public Integer getAvailability() {
		return this.availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public JsonNode getData() {
		return this.data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public String getSupplier() {
		return this.supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getWarehouse() {
		return this.warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

}