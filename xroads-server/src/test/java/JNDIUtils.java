

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class JNDIUtils {
	public static void setupJNDI() throws NamingException {
		setupJNDI(30);
	}
	
	public static void setupJNDI(int max) throws NamingException {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://192.168.77.15/xroads-kidsdistribution2019?user=xroads-kidsdistribution2019&password=xroads-kidsdistribution2019");
		dataSource.setMaxTotal(max);
		
		InitialContext initialContext = new InitialContext();
        initialContext.createSubcontext("java:");
		initialContext.createSubcontext("java:/comp");
		initialContext.createSubcontext("java:/comp/env");
		initialContext.createSubcontext("java:/comp/env/jdbc");

		initialContext.bind("java:/comp/env/jdbc/xroads", dataSource);
	}
}
