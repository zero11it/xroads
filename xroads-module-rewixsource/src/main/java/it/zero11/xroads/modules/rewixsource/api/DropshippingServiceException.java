package it.zero11.xroads.modules.rewixsource.api;


public class DropshippingServiceException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DropshippingServiceException(Exception ex) {
		super(ex);
	}

	public DropshippingServiceException(String string) {
		super(string);
	}
}
