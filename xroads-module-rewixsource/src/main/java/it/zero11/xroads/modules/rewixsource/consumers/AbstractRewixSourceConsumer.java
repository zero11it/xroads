package it.zero11.xroads.modules.rewixsource.consumers;

import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.RewixAPI;

public abstract class AbstractRewixSourceConsumer {
	protected XRoadsRewixSourceModule xRoadsModule;
	protected RewixAPI api;
	
	public AbstractRewixSourceConsumer(XRoadsRewixSourceModule xRoadsModule) {
		this.xRoadsModule = xRoadsModule;
		api = new RewixAPI(xRoadsModule);
	}
}
