package it.zero11.xroads.ui.layout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLayout;

/**
 * The main layout contains the header with the navigation buttons, and the
 * child views below that.
 */
@JsModule("./theming/custom.js") 
public class EmptyScrollableLayout extends Div implements RouterLayout{

	private static final long serialVersionUID = 1L;

	public EmptyScrollableLayout() {
        setSizeFull();
        getElement().getStyle().set("overflow", "auto");
        getElement().setAttribute("class", "notranslate");
    }
	
	@Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        /**
         * Using the @Theme Annotation to set the Dark Theme will cause some issues with shadows which will appear in
         * the wrong color making them seemingly invisible instead do it the following way as long as the issue is not
         * solved see here -> https://github.com/vaadin/flow/issues/4765
         */
        //getUI().get().getPage().executeJavaScript("document.documentElement.setAttribute(\"theme\",\"dark\")");
    }

	
}