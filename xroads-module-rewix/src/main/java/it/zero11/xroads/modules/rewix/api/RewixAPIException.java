package it.zero11.xroads.modules.rewix.api;

import it.zero11.xroads.sync.SyncException;

public class RewixAPIException extends SyncException {
	private static final long serialVersionUID = -1L;

	private int httpStatus;

	public RewixAPIException(int httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public int getHttpStatus() {
		return httpStatus;
	}
}
