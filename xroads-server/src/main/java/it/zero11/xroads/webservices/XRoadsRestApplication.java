package it.zero11.xroads.webservices;

import java.util.Set;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;
import it.zero11.xroads.webservice.XRoadsWebservice;

public class XRoadsRestApplication extends ResourceConfig{
	public XRoadsRestApplication() {
		packages("it.zero11.xroads.webservices");
		register(JacksonFeature.class);
		register(MultiPartFeature.class);
		
		for (XRoadsModule module : XRoadsCoreServiceBean.getInstance().getEnabledModules(true).values()) {
			Set<Class<? extends XRoadsWebservice<?>>> webservices = module.getWebservices();
			for (Class<? extends XRoadsWebservice<?>> webservice : webservices) {
				try {
					@SuppressWarnings("unchecked")
					XRoadsWebservice<XRoadsModule> webserviceInstance = (XRoadsWebservice<XRoadsModule>) webservice.getConstructor().newInstance();
					webserviceInstance.setXRoadsModule(module);
					register(webserviceInstance);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
