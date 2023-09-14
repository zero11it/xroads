package it.zero11.xroads.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
public class Price extends AbstractProductGroupedEntity {
	private static final long serialVersionUID = 1L;

	@Column(name="buy_price")
	private BigDecimal buyPrice;

	private String country;

	@Column(name="customer_source_id")
	private String customerSourceId;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	@Column(name="discounted_price")
	private BigDecimal discountedPrice;

	@Column(name="listing_group")
	private String listingGroup;

	@Column(name="min_quantity")
	private Integer minQuantity;

	@Column(name="retail_price")
	private BigDecimal retailPrice;

	@Column(name="sell_price")
	private BigDecimal sellPrice;

	@Column(name="suggested_price")
	private BigDecimal suggestedPrice;
	
	@Column(name="merchant_code")
	private String merchantCode;

	public BigDecimal getBuyPrice() {
		return this.buyPrice;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCustomerSourceId() {
		return this.customerSourceId;
	}

	public void setCustomerSourceId(String customerSourceId) {
		this.customerSourceId = customerSourceId;
	}

	public JsonNode getData() {
		return this.data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public BigDecimal getDiscountedPrice() {
		return this.discountedPrice;
	}

	public void setDiscountedPrice(BigDecimal discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public String getListingGroup() {
		return this.listingGroup;
	}

	public void setListingGroup(String listingGroup) {
		this.listingGroup = listingGroup;
	}

	public Integer getMinQuantity() {
		return this.minQuantity;
	}

	public void setMinQuantity(Integer minQuantity) {
		this.minQuantity = minQuantity;
	}

	public BigDecimal getRetailPrice() {
		return this.retailPrice;
	}

	public void setRetailPrice(BigDecimal retailPrice) {
		this.retailPrice = retailPrice;
	}

	public BigDecimal getSellPrice() {
		return this.sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public BigDecimal getSuggestedPrice() {
		return this.suggestedPrice;
	}

	public void setSuggestedPrice(BigDecimal suggestedPrice) {
		this.suggestedPrice = suggestedPrice;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

}