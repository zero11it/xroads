package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productTagMeta")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTagMetaBean {
	@XmlAttribute
	private String platformUid;
	
	@XmlAttribute
	private String localeCode;

	@XmlAttribute
	private String urlKey;
	
	@XmlAttribute
	private String tagTranslation;
	
	public String getPlatformUid() {
		return platformUid;
	}

	public void setPlatformUid(String platformUid) {
		this.platformUid = platformUid;
	}

	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}

	public String getUrlKey() {
		return urlKey;
	}

	public void setUrlKey(String urlKey) {
		this.urlKey = urlKey;
	}

	public String getTagTranslation() {
		return tagTranslation;
	}

	public void setTagTranslation(String tagTranslation) {
		this.tagTranslation = tagTranslation;
	}
}
