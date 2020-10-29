package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productTagMetas")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTagMetasBean {
	@XmlAttribute
	private Integer tagId;
	
	@XmlAttribute
	private String tagValue;
		
    @XmlElement(name = "productTagMeta")
	private List<ProductTagMetaBean> productTagMetas;

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	public List<ProductTagMetaBean> getProductTagMetas() {
		return productTagMetas;
	}

	public void setProductTagMetas(List<ProductTagMetaBean> productTagMetas) {
		this.productTagMetas = productTagMetas;
	}
}
