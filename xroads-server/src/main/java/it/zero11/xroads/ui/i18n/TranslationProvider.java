package it.zero11.xroads.ui.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.vaadin.flow.i18n.I18NProvider;

import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.utils.modules.core.utils.LocalCache;
import it.zero11.xroads.utils.modules.core.utils.LocalCache.TTL;
import it.zero11.xroads.utils.modules.core.utils.LocalCache.UpdateMode;

public class TranslationProvider implements I18NProvider {
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(TranslationProvider.class);
	
	public final String bundlePrefix;

    private final List<Locale> locales;
    
	public TranslationProvider() {
		bundlePrefix = ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.LOCALE_BUNDLE_PREFIX);
		
		ClassLoader classLoader = TranslationProvider.class.getClassLoader(); 
		
		List<Locale> locales = new ArrayList<>();
		try {
			if (classLoader.getResources("/i18n/" + bundlePrefix + "_en.properties").hasMoreElements()) {
				locales.add(new Locale("en"));
			}
		}catch (Exception e) {
		}
		try {
			if (classLoader.getResources("/i18n/" + bundlePrefix + "_it.properties").hasMoreElements()) {
				locales.add(new Locale("it"));
			}
		}catch (Exception e) {
		}
		
		this.locales = List.copyOf(locales);
	}

	@Override
	public List<Locale> getProvidedLocales() {
		return locales;
	}

	@Override
	public String getTranslation(String key, Locale locale, Object... params) {
		if (key == null) {
            return "";
        }

        String value;
        try {
            value = getResourceBundle(locale).getString(key);
        } catch (final MissingResourceException e) {
        	log.warn("Missing resource", e);
            return key;
        }
        
        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        
        return value;
	}

	private ResourceBundle getResourceBundle(Locale locale) {
		return LocalCache.getInstance().getOrGenerate(locale.toString(), UpdateMode.CALLER_THREAD, TTL.LONG, () -> {
			return new CombinedResourceBundle(bundlePrefix, locale, TranslationProvider.class.getClassLoader());
		});
	}
	
	public static class CombinedResourceBundle extends ResourceBundle {
		private final String bundlePrefix;
		private final Locale locale;
		private final ClassLoader classLoader;
		
		private Map<String, String> combinedResources = new HashMap<>();
		
	    public CombinedResourceBundle(String bundlePrefix, Locale locale, ClassLoader classLoader) {
			this.bundlePrefix = bundlePrefix;
			this.locale = locale;
			this.classLoader = classLoader;
			load();
		}

		public void load(){
			combinedResources = new HashMap<>();
			try {
				for (URL url : Collections.list(classLoader.getResources("/i18n/" + bundlePrefix + "_" + locale.getLanguage() + ".properties"))) {
					try(InputStream is = url.openStream()){
						PropertyResourceBundle bundle = new PropertyResourceBundle(is);
			            for (String key : Collections.list(bundle.getKeys())){
			            	combinedResources.put(key, bundle.getString(key));
			            }
					} catch (IOException e) {
					}
				}
			} catch (IOException e) {
			}
	    }
	    
		@Override
		protected Object handleGetObject(String key) {
			return combinedResources.get(key);
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.enumeration(combinedResources.keySet());
		}
		
	}
}
