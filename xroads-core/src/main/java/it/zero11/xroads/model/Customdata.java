package it.zero11.xroads.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
public class Customdata extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CustomdataPK id;


	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="content")
	private JsonNode content;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	@Column(name="product_source_id")
	private String productSourceId;

	public Customdata() {
	}

	public CustomdataPK getId() {
		return this.id;
	}

	public void setId(CustomdataPK id) {
		this.id = id;
	}

	public JsonNode getContent() {
		return this.content;
	}

	public void setContent(JsonNode content) {
		this.content = content;
	}

	public JsonNode getData() {
		return this.data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public String getProductSourceId() {
		return this.productSourceId;
	}

	public void setProductSourceId(String productSourceId) {
		this.productSourceId = productSourceId;
	}
}