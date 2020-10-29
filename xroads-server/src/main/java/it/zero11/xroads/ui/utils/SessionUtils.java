package it.zero11.xroads.ui.utils;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;

public class SessionUtils {
	public final static String SESSION_USER_ID = "userID";
	public final static String SESSION_LOCALE = "userLocale";
	public final static String AUTH_COOKIE_NAME = "auth";
	
	//FIXME: eventually to be replaced with a real user
	private final static Object LOGGED_USER = new Object();
	
	public static Object getLoggedUser(VaadinSession session){
		return getLoggedUser(session.getSession());
	}
	
	public static Object getLoggedUser(WrappedSession session){
		Long supplierID = getLoggedUserId(session);
		if (supplierID != null){
			Object supplier = LOGGED_USER;
			if (supplier == null){
				logout();
			}
			return supplier;
		}

		return null;
	}
	
	public static void putUserInSession(WrappedSession session, Object supplier) {
		session.setAttribute(SESSION_USER_ID, 1L);
		session.setAttribute(SESSION_LOCALE, "en_US");
	}

	public static Long getLoggedUserId(VaadinSession session) {
		return getLoggedUserId(session.getSession());
	}
	
	public static Long getLoggedUserId(WrappedSession session) {
		return (Long) session.getAttribute(SESSION_USER_ID);
	}

	public static String getLoggedUserLocale(VaadinSession session) {
		return getLoggedUserLocale(session.getSession());
	}

	public static String getLoggedUserLocale(WrappedSession session) {
		return (String) session.getAttribute(SESSION_LOCALE);
	}	

	public static void logout() {	
		VaadinSession.getCurrent().getSession().setAttribute(SESSION_USER_ID, null);		
	}

}
