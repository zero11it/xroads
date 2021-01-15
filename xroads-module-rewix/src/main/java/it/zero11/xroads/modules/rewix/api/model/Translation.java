package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "translation")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Translation implements Serializable {
	private static final long serialVersionUID = 1L;

	private LocaleValue localeValue;

	public Translation(){
		localeValue = new LocaleValue();
	}
	
	public Translation(LocaleValue localeValue){
		this.localeValue = localeValue;
	}
	
	@XmlTransient
	public LocaleValue getLocaleValue() {
		return localeValue;
	}

	public String getDescription() {
		return localeValue.getValue();
	}

	public void setDescription(String description) {
		localeValue.setValue(description);
	}

	public String getLocalecode() {
		return localeValue.getLocalecode();
	}

	public void setLocalecode(String localecode) {
		localeValue.setLocalecode(localecode);
	}

	public String getUrlKey() {
		return localeValue.getUrlKey();
	}

	public void setUrlKey(String urlKey) {
		localeValue.setUrlKey(urlKey);
	}
}
