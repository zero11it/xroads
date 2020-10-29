package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import it.zero11.xroads.ui.layout.EmptyScrollableLayout;
import it.zero11.xroads.ui.utils.SessionUtils;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.webservices.OAuthWebservice;

@PageTitle("Admin login")
@Route(value = "login", layout = EmptyScrollableLayout.class)
public class LoginView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	private HorizontalLayout logoLayout;
	private Image imgLogo;
	
	public LoginView() {
		setSizeFull();
//		H2 title = new H2("Admin login");
//		add(title);
		logoLayout = new HorizontalLayout();
		logoLayout.setWidthFull();		
		imgLogo = new Image("images/logo-xroads-esteso.png  ","logo");
		imgLogo.setWidth("270px");
		imgLogo.setHeight("70px");
		logoLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		logoLayout.add(imgLogo);
		
		Component loginForm = buildLoginForm();
		setAlignSelf(Alignment.CENTER, loginForm);
		setJustifyContentMode(JustifyContentMode.CENTER);
		add(logoLayout, loginForm);
	}

	private Component buildLoginForm() {
        final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setMargin(false);
        loginPanel.setAlignItems(Alignment.CENTER);

        final Button signin = new Button("Login to " + ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.NAME), 
        		VaadinIcon.ANGLE_RIGHT.create());
        signin.getStyle().set("max-width", "95vw");
        signin.addThemeVariants(ButtonVariant.LUMO_LARGE);
        signin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        signin.addClickListener(event -> {
        	doLogin();        	
        });

        loginPanel.add(signin);
        
        return loginPanel;
    }

    
    private void doLogin() {
    	if (SessionUtils.getLoggedUser(VaadinSession.getCurrent()) != null){
    		UI.getCurrent().navigate(DashboardView.class);
		} else {
			UI.getCurrent().getPage().executeJs("window.location.href = '" + OAuthWebservice.getOAuthRedirectURL() + "';");
		}
	}

}
