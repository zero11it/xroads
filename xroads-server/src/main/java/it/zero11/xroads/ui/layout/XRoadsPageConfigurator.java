package it.zero11.xroads.ui.layout;

import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinServlet;

public interface XRoadsPageConfigurator extends PageConfigurator {
	default public void configurePage(InitialPageSettings settings) {
		String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
		settings.addFavIcon("apple-touch-icon", contextPath + "/images/apple-touch-icon.png", "120x120");
		settings.addFavIcon("icon", contextPath + "/images/favicon-32x32.png", "32x32");
		settings.addFavIcon("icon", contextPath + "/images/favicon-16x16.png", "16x16");
	}
}
