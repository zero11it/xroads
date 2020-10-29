package it.zero11.xroads.sync;

import it.zero11.xroads.model.AbstractEntity;

public interface EntityConsumer<T extends AbstractEntity> {
	void consume(T entity) throws SyncException;
}
