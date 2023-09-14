package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="value")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValueWithKey implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlAttribute
	protected String key;
	
	@XmlValue
	protected String value;
	
	public ValueWithKey() {
	}

	public ValueWithKey(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public ValueWithKey(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
