package it.zero11.xroads.webservices;

import javax.servlet.annotation.WebServlet;

import org.glassfish.jersey.servlet.ServletContainer;

@WebServlet(urlPatterns = "/rest/*", name = "xroads-rest-servlet")
public class XRoadsRestServlet extends ServletContainer {
	private static final long serialVersionUID = 1L;
	private static XRoadsRestServlet instance;
	
	public XRoadsRestServlet() {
		super(new XRoadsRestApplication());
		instance = this;
	}

	public static void shutdown() {
		instance = null;
	}

	public static void reloadConfiguration() {
		if (instance != null) {
			instance.reload(new XRoadsRestApplication());
		}
	}

}
