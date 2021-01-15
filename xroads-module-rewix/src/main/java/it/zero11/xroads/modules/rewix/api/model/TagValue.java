package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


public class TagValue implements Serializable{
	private static final long serialVersionUID = 1L;
	private String value;
	private String valueI18N;
	private String urlKey;
	private Integer totalCount;
	
	@Deprecated
	private List<Translation> translations;
	private List<LocaleValue> localeValues;
	
	
	public TagValue(String value) {
		this.value = value;
	}
	
	public TagValue(String value, String i18n) {
		this.value = value;
		this.valueI18N = i18n;
	}
	
	public TagValue(String value, String i18n, String urlKey, Integer totalCount) {
		this.value = value;
		this.valueI18N = i18n;
		this.urlKey = urlKey;
		this.totalCount = totalCount;
	}
	
	public TagValue(){}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValueI18N() {
		return valueI18N;
	}
	public void setValueI18N(String valueI18N) {
		this.valueI18N = valueI18N;
	}
	public String getUrlKey() {
		return urlKey;
	}
	public void setUrlKey(String urlKey) {
		this.urlKey = urlKey;
	}

	@Deprecated
	@XmlElementWrapper(name="translations")
	@XmlElement(name="translation")
	public List<Translation> getTranslations() {
		return translations;
	}

	@Deprecated
	public void setTranslations(List<Translation> translations) {
		this.translations = translations;
	}

	@XmlElement(name="localevalue")
	public List<LocaleValue> getLocaleValues() {
		return localeValues;
	}

	public void setLocaleValues(List<LocaleValue> localeValues) {
		this.localeValues = localeValues;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
}
