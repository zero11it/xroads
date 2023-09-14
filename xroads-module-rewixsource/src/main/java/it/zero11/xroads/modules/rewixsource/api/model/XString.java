package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="value")
@XmlAccessorType(XmlAccessType.FIELD)
public class XString implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlAttribute
	protected String key;
	
	@XmlElement
	protected String value;
	
	public XString() {
	}

	public XString(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public XString(String value) {
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
