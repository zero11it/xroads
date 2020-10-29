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

@TypeDef(
		name = "jsonb-node", 
		typeClass = JsonNodeBinaryType.class
		)
@MappedSuperclass
public class AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="source_id")
	private String sourceId;

	@Type( type = "jsonb-node" )
	@Column(columnDefinition = "jsonb", name="external_references")
	private JsonNode externalReferences;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	private Integer version;

	public String getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
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

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractEntity other = (AbstractEntity) obj;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		return true;
	}
	
}
