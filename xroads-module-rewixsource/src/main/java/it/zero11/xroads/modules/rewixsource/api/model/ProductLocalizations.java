package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "localizations")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class ProductLocalizations implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<LocaleValue> description;

	private List<LocaleValue> productName;

	private List<LocaleValue> metaDescription;

	private List<LocaleValue> metaKeywords;

	private List<LocaleValue> metaTitle;
	
	private List<LocaleValue> urlKey;

	@XmlElementWrapper(name="description")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getDescription() {
		return description;
	}
	
	public void setDescription(List<LocaleValue> description) {
		this.description = description;
	}

	@XmlElementWrapper(name="name")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getProductName() {
		return productName;
	}

	public void setProductName(List<LocaleValue> productName) {
		this.productName = productName;
	}

	@XmlElementWrapper(name="metaDescription")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(List<LocaleValue> metaDescription) {
		this.metaDescription = metaDescription;
	}

	@XmlElementWrapper(name="metaKeywords")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(List<LocaleValue> metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	@XmlElementWrapper(name="metaTitle")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(List<LocaleValue> metaTitle) {
		this.metaTitle = metaTitle;
	}

	@XmlElementWrapper(name="urlKey")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getUrlKey() {
		return urlKey;
	}

	public void setUrlKey(List<LocaleValue> urlKey) {
		this.urlKey = urlKey;
	}
}
