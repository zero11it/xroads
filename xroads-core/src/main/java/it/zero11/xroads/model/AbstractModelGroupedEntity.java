package it.zero11.xroads.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractModelGroupedEntity extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Column(name="model_source_id")
	private String modelSourceId;

	public String getModelSourceId() {
		return modelSourceId;
	}

	public void setModelSourceId(String modelSourceId) {
		this.modelSourceId = modelSourceId;
	}
}
