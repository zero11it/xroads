package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productRestrictions")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductRestrictionsBean {
	@XmlAttribute
	private Integer stockProductId;
	
    @XmlElement(name = "productRestrictionHidden")
	private List<ProductRestrictionBean> productRestrictionsHidden;
    
    @XmlElement(name = "productRestrictionVisible")
	private List<ProductRestrictionBean> productRestrictionsVisible;

	public Integer getStockProductId() {
		return stockProductId;
	}

	public void setStockProductId(Integer stockProductId) {
		this.stockProductId = stockProductId;
	}

	public List<ProductRestrictionBean> getProductRestrictionsHidden() {
		return productRestrictionsHidden;
	}

	public void setProductRestrictionsHidden(List<ProductRestrictionBean> productRestrictionsHidden) {
		this.productRestrictionsHidden = productRestrictionsHidden;
	}

	public List<ProductRestrictionBean> getProductRestrictionsVisible() {
		return productRestrictionsVisible;
	}

	public void setProductRestrictionsVisible(List<ProductRestrictionBean> productRestrictionsVisible) {
		this.productRestrictionsVisible = productRestrictionsVisible;
	}
}
