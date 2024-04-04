package it.zero11.xroads.servlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.server.VaadinServlet;

@WebServlet(urlPatterns = "/*", asyncSupported = true, initParams = {
		@WebInitParam(name = InitParameters.I18N_PROVIDER, value = "it.zero11.xroads.ui.i18n.TranslationProvider"),
		@WebInitParam(name = InitParameters.SERVLET_PARAMETER_HEARTBEAT_INTERVAL, value = "200") })
public class XRoadsServlet extends VaadinServlet {

	private static final long serialVersionUID = 1L;

}
