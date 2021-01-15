package it.zero11.xroads.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.databind.JsonNode;

import org.hibernate.annotations.Parameter;

import it.zero11.xroads.utils.ArrayUserType;

@Entity
@TypeDefs(value={
		@TypeDef(name = "array", typeClass = ArrayUserType.class)
})
public class Model extends AbstractProductGroupedEntity {
	private static final long serialVersionUID = 1L;

	@Deprecated
	private Integer availability;

	@Deprecated
	@Column(name="availability_at")
	private Timestamp availabilityAt;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	private String ean;

	@Type(type = "array",parameters={@Parameter(name="type", value="java.lang.String")})
	@Column(name="additional_barcode", columnDefinition="_text")
	private String[] additionalBarcode;
	
	@Column(name = "weight")
	private BigDecimal weight;
	
	private String name;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="options")
	private JsonNode options;

	private String sku;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="tags")
	private JsonNode tags;

	public Model() {
	}

	@Deprecated
	public Integer getAvailability() {
		return this.availability;
	}
	@Deprecated
	public void setAvailability(Integer availability) {
		this.availability = availability;
	}
	@Deprecated
	public Timestamp getAvailabilityAt() {
		return this.availabilityAt;
	}
	@Deprecated
	public void setAvailabilityAt(Timestamp availabilityAt) {
		this.availabilityAt = availabilityAt;
	}

	public JsonNode getData() {
		return this.data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public String getEan() {
		return this.ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}
	
	public String[] getAdditionalBarcode() {
		return additionalBarcode;
	}

	public void setAdditionalBarcode(String[] addiionalBarcode) {
		this.additionalBarcode = addiionalBarcode;
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

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

}