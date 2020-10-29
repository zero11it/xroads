package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productTaxables")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTaxablesBean {
	@XmlAttribute
	private Integer stockProductId;
	
    @XmlElement(name = "productTaxable")
	private List<ProductTaxableBean> productTaxables;

	public Integer getStockProductId() {
		return stockProductId;
	}

	public void setStockProductId(Integer stockProductId) {
		this.stockProductId = stockProductId;
	}

	public List<ProductTaxableBean> getProductTaxables() {
		return productTaxables;
	}

	public void setProductTaxables(List<ProductTaxableBean> productTaxables) {
		this.productTaxables = productTaxables;
	}
}
