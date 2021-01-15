package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "localizations")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class ModelLocalizations implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<LocaleValue> size;
	
	private List<LocaleValue> color;

	@XmlElementWrapper(name="size")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getSize() {
		return size;
	}
	
	public void setSize(List<LocaleValue> size) {
		this.size = size;
	}

	@XmlElementWrapper(name="color")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getColor() {
		return color;
	}

	public void setColor(List<LocaleValue> color) {
		this.color = color;
	}
}
