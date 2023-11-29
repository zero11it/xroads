package it.zero11.xroads.ui.layout;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import it.zero11.xroads.ui.utils.SessionUtils;
import it.zero11.xroads.ui.view.LoginView;

@JsModule("./theming/custom.js") 
public class XRoadsAdminLayout extends AppLayoutRouterLayout<AppLayout> implements BeforeEnterObserver {
	private static final long serialVersionUID = 1L;
	
	private static final Map<Class<?>, XRoadsShowInMenuBar> viewsInMenu;
	
	static {
		Map<Class<?>, XRoadsShowInMenuBar> views = new HashMap<>();
		try {
			ScanResult scanResult = new ClassGraph()
					.enableClassInfo()
					.enableAnnotationInfo()
					.addClassLoader(XRoadsAdminLayout.class.getClassLoader())
					.scan();
			for (ClassInfo clazzInfo : scanResult.getClassesWithAnnotation(XRoadsShowInMenuBar.class)){
				try{
					Class<?> clazz = Class.forName(clazzInfo.getName());
					XRoadsShowInMenuBar XRoadsShowInMenuBar = clazz.getAnnotation(XRoadsShowInMenuBar.class);
					views.put(clazz, XRoadsShowInMenuBar);
				}catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		viewsInMenu = Collections.unmodifiableMap(views);
	}
	
    private Class<? extends AppLayout> variant = LeftLayouts.LeftResponsiveHybrid.class;

	public XRoadsAdminLayout() {
		Object user = SessionUtils.getLoggedUser(VaadinSession.getCurrent());
		if (user != null) {
			
			selectCurrentLocale();

			Component appBar = AppBarBuilder.get().build();
			
			LeftAppMenuBuilder menuBuilder = LeftAppMenuBuilder.get();
			getMenuItems().forEach(menuBuilder::add);
			
//			LeftAppMenuBuilder menuBuilder = LeftAppMenuBuilder.get()
//					//.addToSection(HEADER, new LeftHeaderItem("XRoads", "XRoads Web Application", null))
//					.add(new LeftNavigationItem("DashBoard", VaadinIcon.DASHBOARD.create(), DashboardView.class))
//					.add(new LeftNavigationItem("Products", VaadinIcon.PACKAGE.create(), ProductView.class))
//					.add(new LeftNavigationItem("Models", VaadinIcon.COPY.create(), ModelView.class))
//					.add(new LeftNavigationItem("Customers", VaadinIcon.USERS.create(), CustomerView.class))
//					.add(new LeftNavigationItem("Stocks", VaadinIcon.STOCK.create(), StockView.class))
//					.add(new LeftNavigationItem("Orders", VaadinIcon.CART_O.create(), OrderView.class))
//					.add(new LeftNavigationItem("Prices", VaadinIcon.EURO.create(), PriceView.class))
//					.add(new LeftNavigationItem("Invoices", VaadinIcon.INVOICE.create(), InvoiceView.class))
//					.add(new LeftNavigationItem("Cron Errors", VaadinIcon.CLOSE.create(), CronErrorsView.class))
//					.add(new LeftNavigationItem("Settings", VaadinIcon.COG.create(), SettingsView.class));
			
	
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
	
	
	@SuppressWarnings("unchecked")
	protected Collection<LeftNavigationItem> getMenuItems() {
		
		TreeSet<LeftNavigationItem> menuItems = new TreeSet<LeftNavigationItem>(); 
//		AccessAnnotationChecker annotationChecker = new AccessAnnotationChecker();
		SortedMap<Integer, LeftNavigationItem> menuItemsMap = new TreeMap<Integer, LeftNavigationItem>(); 
		
		for (Entry<Class<?>, XRoadsShowInMenuBar> viewEntry : viewsInMenu.entrySet()) {
			Class<?> view = viewEntry.getKey();
			
//			if (!annotationChecker.hasAccess(view)) {
//				continue;
//			}
			
			XRoadsShowInMenuBar annotation =  viewEntry.getValue();

			String name = annotation.name();
			if (annotation.i18n()) {
				name = getTranslation(name);
			}
			try{
				LeftNavigationItem leftNavigationItem = new LeftNavigationItem(name, annotation.icon()[0].create(), (Class<? extends Component>) view);
				LeftNavigationItem old = menuItemsMap.put(annotation.order(), leftNavigationItem);
				if (old != null) {
					throw new IllegalArgumentException("Multiple view with the same menu position. Offending classes: " + leftNavigationItem.getHref() + " and " + old.getHref());
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Left App Menu creation Failed. Offending classes: " + view.getCanonicalName() + " " + e.getMessage());
			}
		}
		return menuItemsMap.values();
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
