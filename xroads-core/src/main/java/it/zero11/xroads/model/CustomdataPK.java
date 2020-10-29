package it.zero11.xroads.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the customdata database table.
 * 
 */
@Embeddable
public class CustomdataPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="source_id")
	private String sourceId;

	@Column(name="data_type")
	private String dataType;

	public CustomdataPK() {
	}
	public String getSourceId() {
		return this.sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getDataType() {
		return this.dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CustomdataPK)) {
			return false;
		}
		CustomdataPK castOther = (CustomdataPK)other;
		return 
			this.sourceId.equals(castOther.sourceId)
			&& this.dataType.equals(castOther.dataType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.sourceId.hashCode();
		hash = hash * prime + this.dataType.hashCode();
		
		return hash;
	}
}