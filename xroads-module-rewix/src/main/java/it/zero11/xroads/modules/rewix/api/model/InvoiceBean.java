package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import it.zero11.xroads.modules.rewix.utils.JAXBStringURLEncodedToArray;

@XmlRootElement(name = "invoice")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class InvoiceBean implements Serializable {
	public static class InvoiceAttachmentBean {
		private Integer id;
		private String name;
		private String extension;
		private String url;

		public String getExtension() {
			return extension;
		}

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	public static class InvoiceItemBean {
		private Integer id;

		private Integer productId;
		private Integer stockModelId;

		private String SKU;
		private String brand;
		private String name;
		private String color;
		private String size;
		private String barcode;
		private String[] additionalBarcodes;
		private String madein;
		private String intra;

		@Deprecated
		private BigDecimal tax;
		private BigDecimal vat;
		private Integer vatSystemId;
		private BigDecimal vatAmount;
		private BigDecimal totalVatAmount;
		private BigDecimal unitTaxable;
		private BigDecimal taxable;
		private BigDecimal totalTaxable;
		private BigDecimal unitPrice, totalPrice;
		private BigDecimal discount, totalDiscount;

		private Integer quantity;

		private String imageURL;

		public InvoiceItemBean() {
		}

		public String getBarcode() {
			return barcode;
		}

		public String getBrand() {
			return brand;
		}

		public String getColor() {
			return color;
		}

		public BigDecimal getDiscount() {
			return discount;
		}

		public Integer getId() {
			return id;
		}

		public String getImageURL() {
			return imageURL;
		}

		public String getIntra() {
			return intra;
		}

		public String getMadein() {
			return madein;
		}

		public String getName() {
			return name;
		}

		public Integer getProductId() {
			return productId;
		}

		public Integer getStockModelId() {
			return stockModelId;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public String getSize() {
			return size;
		}

		public String getSKU() {
			return SKU;
		}

		@Deprecated
		public BigDecimal getTax() {
			return tax;
		}

		public BigDecimal getTotalDiscount() {
			return totalDiscount;
		}

		public BigDecimal getTotalPrice() {
			return totalPrice;
		}

		public BigDecimal getTotalTaxable() {
			return totalTaxable;
		}

		public BigDecimal getTotalVatAmount() {
			return totalVatAmount;
		}

		public BigDecimal getUnitPrice() {
			return unitPrice;
		}

		public BigDecimal getTaxable() {
			return taxable;
		}

		public BigDecimal getUnitTaxable() {
			return unitTaxable;
		}

		public BigDecimal getVatAmount() {
			return vatAmount;
		}

		public Integer getVatSystemId() {
			return vatSystemId;
		}

		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}

		@XmlJavaTypeAdapter(value=JAXBStringURLEncodedToArray.class)
		public String[] getAdditionalBarcodes() {
			return additionalBarcodes;
		}

		public void setAdditionalBarcodes(String[] additionalBarcodes) {
			this.additionalBarcodes = additionalBarcodes;
		}

		public void setBrand(String brand) {
			this.brand = brand;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public void setDiscount(BigDecimal discount) {
			this.discount = discount;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public void setImageURL(String imageURL) {
			this.imageURL = imageURL;
		}

		public void setIntra(String intra) {
			this.intra = intra;
		}

		public void setMadein(String madein) {
			this.madein = madein;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setProductId(Integer productId) {
			this.productId = productId;
		}

		public void setStockModelId(Integer stockModelId) {
			this.stockModelId = stockModelId;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public void setSize(String size) {
			this.size = size;
		}

		public void setSKU(String sKU) {
			SKU = sKU;
		}

		public void setTotalDiscount(BigDecimal totalDiscount) {
			this.totalDiscount = totalDiscount;
		}

		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}

		public void setTotalTaxable(BigDecimal totalTaxable) {
			this.totalTaxable = totalTaxable;
		}

		public void setTotalVatAmount(BigDecimal totalVatAmount) {
			this.totalVatAmount = totalVatAmount;
		}

		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}

		public void setTaxable(BigDecimal taxable) {
			this.taxable = taxable;
		}

		public void setUnitTaxable(BigDecimal unitTaxable) {
			this.unitTaxable = unitTaxable;
		}

		public void setVatAmount(BigDecimal vatAmount) {
			this.vatAmount = vatAmount;
			this.tax = vatAmount;
		}

		public void setVatSystemId(Integer vatSystemId) {
			this.vatSystemId = vatSystemId;
		}

		public BigDecimal getVat() {
			return vat;
		}

		public void setVat(BigDecimal vat) {
			this.vat = vat;
		}
	}
	public static class InvoiceVat implements Serializable {

		private static final long serialVersionUID = 1L;

		private BigDecimal vat;

		private Integer vatId;

		private String vatName;
		private String vatArt;
		private BigDecimal taxable;
		private BigDecimal amount;
		
		public InvoiceVat() {
		}

		public BigDecimal getTaxable() {
			return taxable;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public BigDecimal getVat() {
			return vat;
		}

		public String getVatArt() {
			return vatArt;
		}

		public Integer getVatId() {
			return vatId;
		}

		public String getVatName() {
			return vatName;
		}

		public void setTaxable(BigDecimal taxable) {
			this.taxable = taxable;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public void setVat(BigDecimal vat) {
			this.vat = vat;
		}

		public void setVatArt(String vatArt) {
			this.vatArt = vatArt;
		}

		public void setVatId(Integer vatId) {
			this.vatId = vatId;
		}

		public void setVatName(String vatName) {
			this.vatName = vatName;
		}
	}
	private static final long serialVersionUID = 1L;
	private String username;
	private Integer billId;
	private String billNo;
	private String billTo;
	private Integer billReferenceId;
	private String vatNumber;
	private String fiscalCode;
	private String address;
	private String zip;
	private String city;
	private String prov;

	private String country;
	private String countryCode;
	private Integer countryId;
	private String paymentInfo;
	private Integer paymentGatewayId;

	private Date date;
	
	private List<InvoiceBean> creditNotes;

	private List<InvoiceAttachmentBean> attachments;
	private List<InvoiceItemBean> items;
	private Integer itemsCount;
	
	private List<Integer> orderIds;
	
	private String currency;

	private BigDecimal taxableTotal;
	private BigDecimal vatAmountTotal;
	private BigDecimal total;

	private BigDecimal ecredit;

	private List<InvoiceVat> vatAmounts;
	
	private String type;

	public InvoiceBean() {
	}

	public String getAddress() {
		return address;
	}

	@XmlElementWrapper(name = "attachments")
	@XmlElement(name = "attachment")
	public List<InvoiceAttachmentBean> getAttachments() {
		return attachments;
	}

	public Integer getBillId() {
		return billId;
	}

	public String getBillNo() {
		return billNo;
	}

	public String getBillTo() {
		return billTo;
	}

	public Integer getBillReferenceId() {
		return billReferenceId;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public String getCurrency() {
		return currency;
	}

	public Date getDate() {
		return date;
	}

	public BigDecimal getEcredit() {
		return ecredit;
	}

	@XmlElementWrapper(name = "creditNotes")
	@XmlElement(name = "creditNote")
	public List<InvoiceBean> getCreditNotes() {
		return creditNotes;
	}

	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	public List<InvoiceItemBean> getItems() {
		return items;
	}

	public Integer getItemsCount() {
		return itemsCount;
	}

	@XmlElementWrapper(name = "orderIds")
	@XmlElement(name="orderId")
	public List<Integer> getOrderIds() {
		return orderIds;
	}

	public Integer getPaymentGatewayId() {
		return paymentGatewayId;
	}
	
	public String getPaymentInfo() {
		return paymentInfo;
	}

	public String getProv() {
		return prov;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public String getUsername() {
		return username;
	}

	public BigDecimal getTaxableTotal() {
		return taxableTotal;
	}

	public BigDecimal getVatAmountTotal() {
		return vatAmountTotal;
	}

	@XmlElementWrapper(name = "vats")
	@XmlElement(name = "vat")
	public List<InvoiceVat> getVatAmounts() {
		return vatAmounts;
	}

	public String getVatNumber() {
		return vatNumber;
	}

	public String getZip() {
		return zip;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	public void setAttachments(List<InvoiceAttachmentBean> attachments) {
		this.attachments = attachments;
	}

	public void setBillId(Integer billId) {
		this.billId = billId;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public void setBillTo(String billTo) {
		this.billTo = billTo;
	}

	public void setBillReferenceId(Integer billReferenceId) {
		this.billReferenceId = billReferenceId;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setEcredit(BigDecimal ecredit) {
		this.ecredit = ecredit;
	}

	public void setCreditNotes(List<InvoiceBean> creditNotes) {
		this.creditNotes = creditNotes;
	}

	public void setItems(List<InvoiceItemBean> items) {
		this.items = items;
	}

	public void setItemsCount(Integer itemsCount) {
		this.itemsCount = itemsCount;
	}

	public void setOrderIds(List<Integer> orderIds) {
		this.orderIds = orderIds;
	}

	public void setPaymentGatewayId(Integer paymentGatewayId) {
		this.paymentGatewayId = paymentGatewayId;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setTaxableTotal(BigDecimal taxableTotal) {
		this.taxableTotal = taxableTotal;
	}

	public void setVatAmountTotal(BigDecimal vatAmountTotal) {
		this.vatAmountTotal = vatAmountTotal;
	}

	public void setVatAmounts(List<InvoiceVat> vatAmounts) {
		this.vatAmounts = vatAmounts;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	public String getFiscalCode() {
		return fiscalCode;
	}

	public void setFiscalCode(String fiscalCode) {
		this.fiscalCode = fiscalCode;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
