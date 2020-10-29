package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productImages")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductImagesBean {
	@XmlAttribute
	private Integer stockProductId;
	
	@XmlAttribute()
	private boolean deleteExisting = false;
	
    @XmlElement(name = "productImage")
	private List<ProductImageBean> productImages;

	public Integer getStockProductId() {
		return stockProductId;
	}

	public void setStockProductId(Integer stockProductId) {
		this.stockProductId = stockProductId;
	}

	public boolean isDeleteExisting() {
		return deleteExisting;
	}

	public void setDeleteExisting(boolean deleteExisting) {
		this.deleteExisting = deleteExisting;
	}

	public List<ProductImageBean> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<ProductImageBean> productImages) {
		this.productImages = productImages;
	}
}
