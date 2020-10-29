package it.zero11.xroads.webservice;

import it.zero11.xroads.modules.XRoadsModule;

public abstract class AbstractXRoadsWebservice<T extends XRoadsModule> implements XRoadsWebservice<T> {
	protected T xRoadsModule;
	
	@Override
	public final void setXRoadsModule(T xRoadsModule) {
		this.xRoadsModule = xRoadsModule;
	}
	
	@Override
	public final T getXRoadsModule() {
		return xRoadsModule;
	}

}
