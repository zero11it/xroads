package it.zero11.xroads.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name="product_revision")
public class ProductRevision extends AbstractEntityRevision<Product> {
	private static final long serialVersionUID = 1L;

	private String brand;

	private BigDecimal cost;


	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="descriptions")
	private JsonNode descriptions;

	private String ean;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="images")
	private JsonNode images;

	private String name;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="names")
	private JsonNode names;

	private String sku;

	private String supplier;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="tags")
	private JsonNode tags;

	private Boolean virtual;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="restrictions")
	private JsonNode restrictions;

	public String getBrand() {
		return this.brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public JsonNode getData() {
		return this.data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public JsonNode getDescriptions() {
		return this.descriptions;
	}

	public void setDescriptions(JsonNode descriptions) {
		this.descriptions = descriptions;
	}

	public String getEan() {
		return this.ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public JsonNode getImages() {
		return this.images;
	}

	public void setImages(JsonNode images) {
		this.images = images;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JsonNode getNames() {
		return this.names;
	}

	public void setNames(JsonNode names) {
		this.names = names;
	}

	public String getSku() {
		return this.sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getSupplier() {
		return this.supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public JsonNode getTags() {
		return this.tags;
	}

	public void setTags(JsonNode tags) {
		this.tags = tags;
	}

	public Boolean getVirtual() {
		return this.virtual;
	}

	public void setVirtual(Boolean virtual) {
		this.virtual = virtual;
	}

	public JsonNode getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(JsonNode restrictions) {
		this.restrictions = restrictions;
	}

}