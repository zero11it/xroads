package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productTaxables")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTagsBean {
	@XmlAttribute
	private Integer stockProductId;
	
    @XmlElement(name = "productTag")
	private List<ProductTagBean> productTags;

	public Integer getStockProductId() {
		return stockProductId;
	}

	public void setStockProductId(Integer stockProductId) {
		this.stockProductId = stockProductId;
	}

	public List<ProductTagBean> getProductTags() {
		return productTags;
	}

	public void setProductTags(List<ProductTagBean> productTags) {
		this.productTags = productTags;
	}
}
