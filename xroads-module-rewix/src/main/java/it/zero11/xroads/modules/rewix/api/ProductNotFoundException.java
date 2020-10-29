package it.zero11.xroads.modules.rewix.api;

import it.zero11.xroads.sync.SyncException;

public class ProductNotFoundException extends SyncException {
	private static final long serialVersionUID = 1L;

	Integer id;
	
	public ProductNotFoundException(Integer productId) {
		super("Product " + productId + " not found.");
		this.id = productId;
	}


	public Integer getId() {
		return id;
	}
}
