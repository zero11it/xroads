package it.zero11.xroads.modules;

public abstract class AbstractXRoadsModule implements XRoadsModule {
	private String name;
	private XRoadsCoreService xRoadsCoreService;
	
	@Override
	public final void configure(String name, XRoadsCoreService xRoadsCoreService) {
		this.name = name;
		this.xRoadsCoreService = xRoadsCoreService;
	}
	
	public final String getName() {
		return name;
	}
	
	public final XRoadsCoreService getXRoadsCoreService() {
		return xRoadsCoreService;
	}
}
