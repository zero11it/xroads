package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "localevalue")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class LocaleValue implements Serializable {
	private static final long serialVersionUID = 1L;

	private String localecode, value, urlKey;

	public LocaleValue(){}
	
	public LocaleValue(String localecode, String value, String urlKey) {
		this.localecode = localecode;
		this.value = value;
		this.urlKey = urlKey;
	}

	@XmlValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlAttribute
	public String getLocalecode() {
		return localecode;
	}

	public void setLocalecode(String localecode) {
		this.localecode = localecode;
	}

	@XmlAttribute
	public String getUrlKey() {
		return urlKey;
	}

	public void setUrlKey(String urlKey) {
		this.urlKey = urlKey;
	}

}
