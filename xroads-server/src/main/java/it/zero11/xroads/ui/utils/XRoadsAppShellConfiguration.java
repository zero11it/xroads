package it.zero11.xroads.ui.utils;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.VaadinServlet;

@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class XRoadsAppShellConfiguration implements AppShellConfigurator {
	private static final long serialVersionUID = 1L;

	public void configurePage(AppShellSettings settings) {
		String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
		settings.addFavIcon("apple-touch-icon", contextPath + "/images/apple-touch-icon.png", "120x120");
		settings.addFavIcon("icon", contextPath + "/images/favicon-32x32.png", "32x32");
		settings.addFavIcon("icon", contextPath + "/images/favicon-16x16.png", "16x16");
	}
}
