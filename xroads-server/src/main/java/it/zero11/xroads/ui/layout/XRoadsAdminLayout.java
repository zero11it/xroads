package it.zero11.xroads.ui.layout;

import java.util.Locale;

import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.applayout.AppLayout;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;

import it.zero11.xroads.ui.utils.SessionUtils;
import it.zero11.xroads.ui.view.CronErrorsView;
import it.zero11.xroads.ui.view.CustomerView;
import it.zero11.xroads.ui.view.DashboardView;
import it.zero11.xroads.ui.view.LoginView;
import it.zero11.xroads.ui.view.ModelView;
import it.zero11.xroads.ui.view.OrderView;
import it.zero11.xroads.ui.view.PriceView;
import it.zero11.xroads.ui.view.ProductView;
import it.zero11.xroads.ui.view.SettingsView;
import it.zero11.xroads.ui.view.StockView;

@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@JsModule("./theming/custom.js") 
public class XRoadsAdminLayout extends AppLayoutRouterLayout<AppLayout> implements BeforeEnterObserver, XRoadsPageConfigurator {
	private static final long serialVersionUID = 1L;
    private Class<? extends AppLayout> variant = LeftLayouts.LeftResponsiveHybrid.class;
	private Span title;

	public XRoadsAdminLayout() {
		Object user = SessionUtils.getLoggedUser(VaadinSession.getCurrent());
		if (user != null) {
			
			selectCurrentLocale();

			Component appBar = AppBarBuilder.get().build();
			
			LeftAppMenuBuilder menuBuilder = LeftAppMenuBuilder.get()
					//.addToSection(HEADER, new LeftHeaderItem("XRoads", "XRoads Web Application", null))
					.add(new LeftNavigationItem("DashBoard", VaadinIcon.DASHBOARD.create(), DashboardView.class))
					.add(new LeftNavigationItem("Products", VaadinIcon.PACKAGE.create(), ProductView.class))
					.add(new LeftNavigationItem("Models", VaadinIcon.COPY.create(), ModelView.class))
					.add(new LeftNavigationItem("Customers", VaadinIcon.USERS.create(), CustomerView.class))
					.add(new LeftNavigationItem("Stocks", VaadinIcon.STOCK.create(), StockView.class))
					.add(new LeftNavigationItem("Orders", VaadinIcon.CART_O.create(), OrderView.class))
					.add(new LeftNavigationItem("Prices", VaadinIcon.EURO.create(), PriceView.class))
					.add(new LeftNavigationItem("Cron Errors", VaadinIcon.CLOSE.create(), CronErrorsView.class))
					.add(new LeftNavigationItem("Settings", VaadinIcon.COG.create(), SettingsView.class));
			
	
			Component appMenu = menuBuilder.build();
	
			init(AppLayoutBuilder
					.get(this.variant)
                    .withTitle("XRoads Admin")
                    .withIcon("images/logo-xroads.png")
					.withAppBar(appBar)
					.withAppMenu(appMenu)
					.withUpNavigation()
					.build());
		}else {
			init(AppLayoutBuilder
					.get(this.variant)
					.withTitle(new Span("Brandssync Admin"))
					.withAppBar(AppBarBuilder.get().build())
					.withAppMenu(LeftAppMenuBuilder.get().build())
					.withUpNavigation()
					.build());
		}
        getElement().setAttribute("class", "notranslate");
	}

	private void selectCurrentLocale() {
		Locale locale = (Locale) VaadinRequest.getCurrent().getWrappedSession().getAttribute("locale");
		if (locale == null) {
			locale = UI.getCurrent().getLocale();
			VaadinRequest.getCurrent().getWrappedSession().setAttribute("locale", locale);
		} else {
			UI.getCurrent().setLocale(locale);
		}
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

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (SessionUtils.getLoggedUser(VaadinSession.getCurrent()) == null){
			event.rerouteTo(LoginView.class);
		}
	}
}
