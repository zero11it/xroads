package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productTranslations")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTranslationsBean {
	public static final int TYPE_DESCRIPTION = 0;
	public static final int TYPE_SIZE = 1;
	public static final int TYPE_COLOR = 2;
	public static final int TYPE_PRODUCT_NAME = 3;
	public static final int TYPE_META_DESCRIPTION = 4;
	public static final int TYPE_META_TITLE = 5;
	public static final int TYPE_META_KEYWORDS = 6;
	@XmlAttribute
	private Integer stockProductId;

	@XmlAttribute
	private Integer type;
	
	@XmlAttribute
	private String value;
	
    @XmlElement(name = "productDescription")
	private List<ProductTranslationBean> productTranslations;

	public Integer getStockProductId() {
		return stockProductId;
	}

	public void setStockProductId(Integer stockProductId) {
		this.stockProductId = stockProductId;
	}

	public List<ProductTranslationBean> getProductTranslations() {
		return productTranslations;
	}

	public void setProductTranslations(List<ProductTranslationBean> productTranslations) {
		this.productTranslations = productTranslations;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
