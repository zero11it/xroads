package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class ProductTag implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String nameI18N;
	private boolean hidden;
	private int priority;
	
	private TagValue value; //used for tag of a specific product, with a single value
	private Collection<TagValue> values;//used surfing tags, with all the possible values
	
	@Deprecated
	private List<Translation> translations;

	private List<LocaleValue> localeValues;
	
	public ProductTag(){}
	

	
	public ProductTag(int id, String name, String nameI18N) {
		super();
		this.id = id;
		this.name = name;
		this.nameI18N = nameI18N;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameI18N() {
		return nameI18N;
	}

	public void setNameI18N(String nameI18N) {
		this.nameI18N = nameI18N;
	}

	@XmlElementWrapper(name="values")
	@XmlElement(name="value")
	public Collection<TagValue> getValues() {
		return values;
	}

	public void setValues(Collection<TagValue> values) {
		this.values = values;
	}



	public boolean isHidden() {
		return hidden;
	}



	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}



	public int getPriority() {
		return priority;
	}



	public void setPriority(int priority) {
		this.priority = priority;
	}



	public TagValue getValue() {
		return value;
	}



	public void setValue(TagValue value) {
		this.value = value;
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
	
}
