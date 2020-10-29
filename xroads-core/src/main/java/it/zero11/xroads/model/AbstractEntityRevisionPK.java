package it.zero11.xroads.model;

import java.io.Serializable;
import javax.persistence.*;

@Embeddable
public class AbstractEntityRevisionPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="source_id")
	private String sourceId;

	private Integer version;

	public AbstractEntityRevisionPK() {
		
	}
	
	public AbstractEntityRevisionPK(String sourceId, Integer version) {
		this.sourceId = sourceId;
		this.version = version;
	}
	
	public String getSourceId() {
		return this.sourceId;
	}
	
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
	public Integer getVersion() {
		return this.version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractEntityRevisionPK)) {
			return false;
		}
		AbstractEntityRevisionPK castOther = (AbstractEntityRevisionPK)other;
		return 
			this.sourceId.equals(castOther.sourceId)
			&& this.version.equals(castOther.version);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.sourceId.hashCode();
		hash = hash * prime + this.version.hashCode();
		
		return hash;
	}
}