package it.zero11.xroads.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractEntityRevision<T extends AbstractEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AbstractEntityRevisionPK id;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public AbstractEntityRevisionPK getId() {
		return this.id;
	}

	public void setId(AbstractEntityRevisionPK id) {
		this.id = id;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
}
