package it.zero11.xroads.cron;

import it.zero11.xroads.modules.XRoadsModule;

public interface XRoadsCronRunnable<T extends XRoadsModule> extends Runnable {
	T getXRoadsModule();
	void setXRoadsModule(T module);
}
