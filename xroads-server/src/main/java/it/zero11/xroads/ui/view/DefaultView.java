package it.zero11.xroads.ui.view;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

public class DefaultView extends VerticalLayout implements HasErrorParameter<NotFoundException>, BeforeEnterObserver {
	private static final long serialVersionUID = 1L;

	@Override
	public void beforeEnter(BeforeEnterEvent event) {}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
		setSizeFull();
		Button notFound = new Button("Return to Dashboard", (e) -> UI.getCurrent().navigate(
				DashboardView.class));
        add(notFound);
        setAlignItems(Alignment.CENTER);
        
		return HttpServletResponse.SC_NOT_FOUND;
	}

}
