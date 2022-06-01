package it.zero11.xroads.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name="model_revision")
public class ModelRevision extends AbstractEntityRevision<Model> {
	private static final long serialVersionUID = 1L;

	private Integer availability;

	@Column(name="availability_at")
	private Timestamp availabilityAt;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private Object data;

	private String ean;

	private String name;
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="options")
	private JsonNode options;

	@Column(name="product_source_id")
	private String productSourceId;
	
	@Column(name = "merchant_code")
	private String merchantCode;

	private String sku;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="tags")
	private JsonNode tags;

	public Integer getAvailability() {
		return this.availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public Timestamp getAvailabilityAt() {
		return this.availabilityAt;
	}

	public void setAvailabilityAt(Timestamp availabilityAt) {
		this.availabilityAt = availabilityAt;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getEan() {
		return this.ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JsonNode getOptions() {
		return this.options;
	}

	public void setOptions(JsonNode options) {
		this.options = options;
	}

	public String getProductSourceId() {
		return this.productSourceId;
	}

	public void setProductSourceId(String productSourceId) {
		this.productSourceId = productSourceId;
	}

	public String getSku() {
		return this.sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public JsonNode getTags() {
		return this.tags;
	}

	public void setTags(JsonNode tags) {
		this.tags = tags;
	}
	
	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantId) {
		this.merchantCode = merchantId;
	}
	
}