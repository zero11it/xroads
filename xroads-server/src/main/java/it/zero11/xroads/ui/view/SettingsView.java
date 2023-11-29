package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.ui.layout.XRoadsShowInMenuBar;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;
import it.zero11.xroads.utils.modules.core.utils.LocalCache;
import it.zero11.xroads.webservices.XRoadsRestServlet;

@Route(value = "settings", layout = XRoadsAdminLayout.class)
@XRoadsShowInMenuBar(name = "Settings", icon = {VaadinIcon.COG}, order = 100)
public class SettingsView extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Button clearSettings;
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		clearSettings = new Button("Clear settings and reload webservices", VaadinIcon.REFRESH.create(),(event) -> {
			LocalCache.getInstance().clear();
			XRoadsRestServlet.reloadConfiguration();
		});		
		add(clearSettings);		
	}
	
	
}
