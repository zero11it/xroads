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

	private List<LocaleValue> option1;

	private List<LocaleValue> option2;

	private List<LocaleValue> option3;

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
	
	@XmlElementWrapper(name="option1")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getOption1() {
		return option1;
	}

	public void setOption1(List<LocaleValue> option1) {
		this.option1 = option1;
	}
	
	@XmlElementWrapper(name="option2")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getOption2() {
		return option2;
	}

	public void setOption2(List<LocaleValue> option2) {
		this.option2 = option2;
	}

	@XmlElementWrapper(name="option3")
	@XmlElement(name="localevalue")
	public List<LocaleValue> getOption3() {
		return option3;
	}

	public void setOption3(List<LocaleValue> option3) {
		this.option3 = option3;
	}
}
