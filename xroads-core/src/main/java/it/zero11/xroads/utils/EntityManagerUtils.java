package it.zero11.xroads.utils;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

public class EntityManagerUtils {

	private static final String DATASOURCE = "java:/comp/env/jdbc/xroads";
	private static EntityManagerFactory emf;

	public static EntityManager createEntityManager() {
		if (emf == null || !emf.isOpen()){
			synchronized (EntityManagerUtils.class) {
				if (emf == null || !emf.isOpen()){
					Map override = new HashMap();
					override.put("hibernate.connection.datasource", DATASOURCE);
					emf = Persistence.createEntityManagerFactory("it.zero11.xroads", override);
				}
			}
		}

		return emf.createEntityManager();
	}

	public static void close(){
		emf.close();
	}

	public static void baseline(){
		try {
			Flyway flyway = Flyway.configure()
					.baselineVersion("2019.01.01")
					.dataSource((DataSource)InitialContext.doLookup(DATASOURCE)).load();
			flyway.baseline();
		}catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void migrate(){
		try {
			Flyway flyway = Flyway.configure()
					.dataSource((DataSource)InitialContext.doLookup(DATASOURCE)).load();
			flyway.migrate();
		}catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}



