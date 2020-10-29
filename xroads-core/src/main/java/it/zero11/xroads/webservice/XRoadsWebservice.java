package it.zero11.xroads.webservice;

import it.zero11.xroads.modules.XRoadsModule;

public interface XRoadsWebservice<T extends XRoadsModule> {
	T getXRoadsModule();
	void setXRoadsModule(T module);
}
