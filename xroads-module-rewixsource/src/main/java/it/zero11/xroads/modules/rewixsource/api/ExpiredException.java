package it.zero11.xroads.modules.rewixsource.api;

public class ExpiredException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExpiredException() {
		super();
	}

	public ExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpiredException(String message) {
		super(message);
	}

	public ExpiredException(Throwable cause) {
		super(cause);
	}
	
}
