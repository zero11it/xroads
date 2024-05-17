package it.zero11.xroads.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
public class Product extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	private String brand;

	private BigDecimal cost;


	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="descriptions")
	private JsonNode descriptions;

	@Deprecated
	private String ean;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="images")
	private JsonNode images;

	private String name;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="names")
	private JsonNode names;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="option1")
	private JsonNode option1;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="option2")
	private JsonNode option2;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="option3")
	private JsonNode option3;

	private Boolean online;

	private String sku;

	private String supplier;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="tags")
	private JsonNode tags;

	private Boolean virtual;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="restrictions")
	private JsonNode restrictions;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="urlkeys")
	private JsonNode urlkeys;
	
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

	public JsonNode getOption1() {
		return option1;
	}

	public void setOption1(JsonNode option1) {
		this.option1 = option1;
	}

	public JsonNode getOption2() {
		return option2;
	}

	public void setOption2(JsonNode option2) {
		this.option2 = option2;
	}

	public JsonNode getOption3() {
		return option3;
	}

	public void setOption3(JsonNode option3) {
		this.option3 = option3;
	}

	public Boolean getOnline() {
		return this.online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
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

	public JsonNode getUrlkeys() {
		return urlkeys;
	}

	public void setUrlkeys(JsonNode urlkeys) {
		this.urlkeys = urlkeys;
	}

}