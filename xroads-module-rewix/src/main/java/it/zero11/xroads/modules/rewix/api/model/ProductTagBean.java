package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productTag")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTagBean {

	@XmlAttribute
	private Integer tagId;

	@XmlElement(name = "value")
	private List<String> tagValues;

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public List<String> getTagValues() {
		return tagValues;
	}

	public void setTagValues(List<String> tagValues) {
		this.tagValues = tagValues;
	}

}
