package it.zero11.xroads.cron;

import it.zero11.xroads.modules.XRoadsModule;

public abstract class AbstractXRoadsCronRunnable<T extends XRoadsModule> implements XRoadsCronRunnable<T> {
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
