package it.zero11.xroads.utils.modules.core.model;

import java.io.Serializable;
import java.time.Instant;

import it.zero11.xroads.model.AbstractEntity;

public class EntityStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Class<? extends AbstractEntity> entityClass;
	private Instant referenceTime;
	private Long newQueued;
	private Long updateQueued;
	private Long syncronized;
	private Long syncError;
	private String module;
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Class<? extends AbstractEntity> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<? extends AbstractEntity> entityClass) {
		this.entityClass = entityClass;
	}

	public Instant getReferenceTime() {
		return referenceTime;
	}

	public void setReferenceTime(Instant referenceTime) {
		this.referenceTime = referenceTime;
	}

	public Long getNewQueued() {
		return newQueued;
	}
	
	public void setNewQueued(Long newQueued) {
		this.newQueued = newQueued;
	}
	
	public Long getUpdateQueued() {
		return updateQueued;
	}
	
	public void setUpdateQueued(Long updateQueued) {
		this.updateQueued = updateQueued;
	}
	
	public Long getSyncronized() {
		return syncronized;
	}
	
	public void setSyncronized(Long syncronized) {
		this.syncronized = syncronized;
	}
	
	public Long getSyncError() {
		return syncError;
	}
	
	public void setSyncError(Long syncError) {
		this.syncError = syncError;
	}
	
	public double getNewQueuedPercentage() {
		if (newQueued + updateQueued + syncronized + syncError > 0) {
			return ((double)newQueued) / (newQueued + updateQueued + syncronized + syncError);
		}else {
			return 0.0;
		}
	}
	
	public double getUpdateQueuedPercentage() {
		if (newQueued + updateQueued + syncronized + syncError > 0) {
			return ((double)updateQueued) / (newQueued + updateQueued + syncronized + syncError);
		}else {
			return 0.0;
		}
	}
	
	public double getSyncronizedPercentage() {
		if (newQueued + updateQueued + syncronized + syncError > 0) {
			return ((double)syncronized) / (newQueued + updateQueued + syncronized + syncError);
		}else {
			return 0.0;
		}
	}
	
	public double getSyncErrorPercentage() {
		if (newQueued + updateQueued + syncronized + syncError > 0) {
			return ((double)syncError) / (newQueued + updateQueued + syncronized + syncError);
		}else {
			return 0.0;
		}
	}
}
