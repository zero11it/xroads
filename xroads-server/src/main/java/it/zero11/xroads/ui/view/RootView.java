package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.ui.layout.EmptyScrollableLayout;
@Route(value = "", layout = EmptyScrollableLayout.class)
public class RootView extends VerticalLayout implements BeforeEnterObserver{
	private static final long serialVersionUID = 1L;

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		event.rerouteTo(DashboardView.class);		
	}

}
