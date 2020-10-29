package it.zero11.xroads.sync;

import java.util.List;

import it.zero11.xroads.model.AbstractProductGroupedEntity;

public interface EntityProductGroupedConsumer<T extends AbstractProductGroupedEntity> {
	void consume(List<T> entity) throws SyncException;
}
