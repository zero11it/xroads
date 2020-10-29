package it.zero11.xroads.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * The persistent class for the customdata_revision database table.
 * 
 */
@Entity
@Table(name="customdata_revision")
@NamedQuery(name="CustomdataRevision.findAll", query="SELECT c FROM CustomdataRevision c")
public class CustomdataRevision implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CustomdataRevisionPK id;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="content")
	private JsonNode content;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="external_references")
	private JsonNode externalReferences;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public CustomdataRevision() {
	}

	public CustomdataRevisionPK getId() {
		return this.id;
	}

	public void setId(CustomdataRevisionPK id) {
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

	public JsonNode getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(JsonNode externalReferences) {
		this.externalReferences = externalReferences;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}