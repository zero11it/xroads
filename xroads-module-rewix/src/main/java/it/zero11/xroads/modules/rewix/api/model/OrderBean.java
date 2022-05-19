package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import it.zero11.xroads.modules.rewix.api.model.InvoiceBean.InvoiceAttachmentBean;
import it.zero11.xroads.modules.rewix.utils.JAXBStringURLEncodedToArray;

@XmlRootElement(name = "order")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class OrderBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer origin;
	private Integer paymentTermIds;
	private String externalRef;
	private String username;
	private String email;
	private AnagraficaData anagrafica; 
	private Integer status, substatus;
	private Date submitDate;
	private Date dispatchDate;
	private DispatchData dispatchData;
	private InvoiceData billingData;
	private List<InvoiceData> creditNotes;
	private List<OrderPackageBean> packages;
	private List<OrderPaymentBean> payments;
	private TrackingData trackingData;
	private List<OrderItemBean> items;
	private Integer itemsCount;
	private String currency;
	private String paymentGateway;
	private String paymentReference;
	
	private BigDecimal total;
	private BigDecimal totaltaxable, totprediscount,totprediscountTaxable, totprediscountVatAmount, discountused, ecredit;
	private BigDecimal vat, vat_amount;
	private BigDecimal taxableItems;
	private BigDecimal totalItems;
	
	private List<OrderVat> vatAmounts;
	
	private Integer vatReliefId;

	private BigDecimal dispatchFixed, dispatchWeight;
	private BigDecimal dispatchFixedTaxable, dispatchWeightTaxable;
	private BigDecimal dispatchFixedVatAmount, dispatchWeightVatAmount;

	private BigDecimal paymentFee;
	private BigDecimal paymentFeeTaxable;
	private BigDecimal paymentFeeVatAmount;
	
	private List<PropertyData> properties;	
	private List<OrderAttachmentBean> attachments;
	
	private String notes, adminNotes;
	
	private Date lastUpdate;
	
	private Date expireDate;
	
	public OrderBean(){}
	
	public static class TrackingData implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String trackingCode, trackingURL, carrierName;
		
		public TrackingData(){}
		
		public String getTrackingCode() {
			return trackingCode;
		}

		public void setTrackingCode(String trackingCode) {
			this.trackingCode = trackingCode;
		}

		public String getTrackingURL() {
			return trackingURL;
		}

		public void setTrackingURL(String trackingURL) {
			this.trackingURL = trackingURL;
		}

		public String getCarrierName() {
			return carrierName;
		}

		public void setCarrierName(String carrierName) {
			this.carrierName = carrierName;
		}
	}
	
	public static class PropertyData implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private Integer id;
		private String context, key, value;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
				
	}
	
	public static class DispatchData implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String recipient;
		private String careOf;
		private String address;
		private String zip;
		private String addressType;
		private String street;
		private String number;
		private String city;
		private String prov;
		private String country;
		private String countryCode;
		private Integer countryId;
		private String cel;
		private String celPrefix;
		private String notes;
		
		public DispatchData(){}
		
		public String getRecipient() {
			return recipient;
		}
		public void setRecipient(String recipient) {
			this.recipient = recipient;
		}
		public String getCareOf() {
			return careOf;
		}
		public void setCareOf(String careOf) {
			this.careOf = careOf;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getZip() {
			return zip;
		}
		public void setZip(String zip) {
			this.zip = zip;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}

		public String getCountryCode() {
			return countryCode;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public String getAddressType() {
			return addressType;
		}

		public void setAddressType(String addressType) {
			this.addressType = addressType;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getProv() {
			return prov;
		}

		public void setProv(String prov) {
			this.prov = prov;
		}

		public Integer getCountryId() {
			return countryId;
		}

		public void setCountryId(Integer countryId) {
			this.countryId = countryId;
		}

		public String getCel() {
			return cel;
		}

		public void setCel(String cel) {
			this.cel = cel;
		}

		public String getCelPrefix() {
			return celPrefix;
		}

		public void setCelPrefix(String celPrefix) {
			this.celPrefix = celPrefix;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}
	}
	
	public static class AnagraficaData implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String cfpiva;
		private String validatedVat;
		private String fiscalCode;
		private String firstName;
		private String lastName;
		private String businessName;
		private String title;
		private String tel_prefix;
		private String tel;
		private String cel_prefix;
		private String cel;
		private String fax;
		private String skype;
		private String website;
		private String pec;
		private String sdi;
		private String address;
		private String zip;
		private String city;
		private String prov;
		private String country;
		private String countryCode;
		private Integer countryId;
		private String privateNotes;
		
		public AnagraficaData(){}

		public String getCfpiva() {
			return cfpiva;
		}

		public void setCfpiva(String cfpiva) {
			this.cfpiva = cfpiva;
		}

		public String getValidatedVat() {
			return validatedVat;
		}

		public void setValidatedVat(String validatedVat) {
			this.validatedVat = validatedVat;
		}

		public String getFiscalCode() {
			return fiscalCode;
		}

		public void setFiscalCode(String fiscalCode) {
			this.fiscalCode = fiscalCode;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getBusinessName() {
			return businessName;
		}

		public void setBusinessName(String businessName) {
			this.businessName = businessName;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTel_prefix() {
			return tel_prefix;
		}

		public void setTel_prefix(String tel_prefix) {
			this.tel_prefix = tel_prefix;
		}

		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getCel_prefix() {
			return cel_prefix;
		}

		public void setCel_prefix(String cel_prefix) {
			this.cel_prefix = cel_prefix;
		}

		public String getCel() {
			return cel;
		}

		public void setCel(String cel) {
			this.cel = cel;
		}

		public String getFax() {
			return fax;
		}

		public void setFax(String fax) {
			this.fax = fax;
		}

		public String getSkype() {
			return skype;
		}

		public void setSkype(String skype) {
			this.skype = skype;
		}

		public String getWebsite() {
			return website;
		}

		public void setWebsite(String website) {
			this.website = website;
		}

		public String getPec() {
			return pec;
		}

		public void setPec(String pec) {
			this.pec = pec;
		}

		public String getSdi() {
			return sdi;
		}

		public void setSdi(String sdi) {
			this.sdi = sdi;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getProv() {
			return prov;
		}

		public void setProv(String prov) {
			this.prov = prov;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
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

		public void setCountryId(Integer countryId) {
			this.countryId = countryId;
		}

		public String getPrivateNotes() {
			return privateNotes;
		}

		public void setPrivateNotes(String privateNotes) {
			this.privateNotes = privateNotes;
		}
	}
	
	public static class InvoiceData implements Serializable{
		private static final long serialVersionUID = 1L;

		private boolean billed;
		
		private Integer billId;
		private String billNo;
		private String billTo;
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
		
		private List<InvoiceAttachmentBean> attachments;

		public InvoiceData(){}
		
		public Integer getBillId() {
			return billId;
		}
		public void setBillId(Integer billId) {
			this.billId = billId;
		}
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getBillTo() {
			return billTo;
		}
		public void setBillTo(String billTo) {
			this.billTo = billTo;
		}
		public String getVatNumber() {
			return vatNumber;
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
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getZip() {
			return zip;
		}
		public void setZip(String zip) {
			this.zip = zip;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
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

		public void setCountryId(Integer countryId) {
			this.countryId = countryId;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getProv() {
			return prov;
		}

		public void setProv(String prov) {
			this.prov = prov;
		}

		public String getPaymentInfo() {
			return paymentInfo;
		}

		public void setPaymentInfo(String paymentInfo) {
			this.paymentInfo = paymentInfo;
		}

		@XmlAttribute
		public boolean isBilled() {
			return billed;
		}

		public void setBilled(boolean billed) {
			this.billed = billed;
		}
		
		@XmlElementWrapper(name="attachments")
		@XmlElement(name="attachment")
		public List<InvoiceAttachmentBean> getAttachments() {
			return attachments;
		}

		public void setAttachments(List<InvoiceAttachmentBean> attachments) {
			this.attachments = attachments;
		}
	}

	public static class OrderAttachmentBean implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private Integer id;
		private String name;
		private String extension;
		private String url;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getExtension() {
			return extension;
		}
		public void setExtension(String extension) {
			this.extension = extension;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	public static class OrderItemBean implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private Integer id;
		private Integer stockModelId;
		
		private Integer productId;
		
		private String SKU;
		private String supplierCode;
		private String brand;
		private String name;
		private String description;
		private String option1;
		private String option2;
		private String option3;
		private String barcode;
		private String[] additionalBarcodes;
		private String madein;
		private String intra;

		@Deprecated
		private BigDecimal tax;
		private BigDecimal vatAmount;
		private BigDecimal totalVatAmount;
		private BigDecimal unitTaxable;
		private BigDecimal taxable;
		private BigDecimal totalTaxable;
		private BigDecimal unitPrice, totalPrice;
		private BigDecimal discount, totalDiscount;
		
		private Integer catalogRuleId;
		private Integer vat_system_id;
		private BigDecimal vat;
		
		private Integer quantity;
		private Integer refundedQuantity;
		
		private String imageURL;
		
		private List<ProductTag> tags;

		private ProductLocalizations productLocalizations;
		
		private ModelLocalizations modelLocalizations;
		
		public OrderItemBean(){}

		public String getSKU() {
			return SKU;
		}

		public void setSKU(String sKU) {
			SKU = sKU;
		}

		public String getSupplierCode() {
			return supplierCode;
		}

		public void setSupplierCode(String supplierCode) {
			this.supplierCode = supplierCode;
		}

		public String getBrand() {
			return brand;
		}

		public BigDecimal getTaxable() {
			return taxable;
		}

		public void setTaxable(BigDecimal taxable) {
			this.taxable = taxable;
		}

		public void setBrand(String brand) {
			this.brand = brand;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Deprecated
		public BigDecimal getTax() {
			return tax;
		}

		public BigDecimal getVatAmount() {
			return vatAmount;
		}

		public void setVatAmount(BigDecimal vatAmount) {
			this.vatAmount = vatAmount;
			this.tax = vatAmount;
		}

		public BigDecimal getTotalVatAmount() {
			return totalVatAmount;
		}

		public void setTotalVatAmount(BigDecimal totalVatAmount) {
			this.totalVatAmount = totalVatAmount;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Integer getRefundedQuantity() {
			return refundedQuantity;
		}

		public void setRefundedQuantity(Integer refundedQuantity) {
			this.refundedQuantity = refundedQuantity;
		}

		public String getImageURL() {
			return imageURL;
		}

		public void setImageURL(String imageURL) {
			this.imageURL = imageURL;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getStockModelId() {
			return stockModelId;
		}

		public void setStockModelId(Integer stockModelId) {
			this.stockModelId = stockModelId;
		}

		public Integer getProductId() {
			return productId;
		}

		public void setProductId(Integer productId) {
			this.productId = productId;
		}

		public BigDecimal getUnitTaxable() {
			return unitTaxable;
		}

		public void setUnitTaxable(BigDecimal unitTaxable) {
			this.unitTaxable = unitTaxable;
		}

		public BigDecimal getTotalTaxable() {
			return totalTaxable;
		}

		public void setTotalTaxable(BigDecimal totalTaxable) {
			this.totalTaxable = totalTaxable;
		}

		public BigDecimal getUnitPrice() {
			return unitPrice;
		}

		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}

		public BigDecimal getTotalPrice() {
			return totalPrice;
		}

		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}

		public BigDecimal getDiscount() {
			return discount;
		}

		public void setDiscount(BigDecimal discount) {
			this.discount = discount;
		}

		public Integer getCatalogRuleId() {
			return catalogRuleId;
		}

		public void setCatalogRuleId(Integer catalogRuleId) {
			this.catalogRuleId = catalogRuleId;
		}

		public Integer getVat_system_id() {
			return vat_system_id;
		}

		public void setVat_system_id(Integer vat_system_id) {
			this.vat_system_id = vat_system_id;
		}

		public BigDecimal getVat() {
			return vat;
		}

		public void setVat(BigDecimal vat) {
			this.vat = vat;
		}
		
		@Deprecated
		@XmlElement(name="color")
		public String getColor() {
			return option2;
		}
		
		@Deprecated
		@XmlElement(name="size")
		public String getSize() {
			return option1;
		}

		public String getOption1() {
			return option1;
		}

		public void setOption1(String option1) {
			this.option1 = option1;
		}

		public String getOption2() {
			return option2;
		}

		public void setOption2(String option2) {
			this.option2 = option2;
		}

		public String getOption3() {
			return option3;
		}

		public void setOption3(String option3) {
			this.option3 = option3;
		}

		public String getBarcode() {
			return barcode;
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

		public BigDecimal getTotalDiscount() {
			return totalDiscount;
		}

		public void setTotalDiscount(BigDecimal totalDiscount) {
			this.totalDiscount = totalDiscount;
		}

		public String getMadein() {
			return madein;
		}

		public void setMadein(String madein) {
			this.madein = madein;
		}

		public String getIntra() {
			return intra;
		}

		public void setIntra(String intra) {
			this.intra = intra;
		}

		@XmlElementWrapper(name="tags")
		@XmlElement(name="tag")
		public List<ProductTag> getTags() {
			return tags;
		}

		public void setTags(List<ProductTag> tags) {
			this.tags = tags;
		}

		public ProductLocalizations getProductLocalizations() {
			return productLocalizations;
		}

		public void setProductLocalizations(ProductLocalizations productLocalizations) {
			this.productLocalizations = productLocalizations;
		}

		public ModelLocalizations getModelLocalizations() {
			return modelLocalizations;
		}

		public void setModelLocalizations(ModelLocalizations modelLocalizations) {
			this.modelLocalizations = modelLocalizations;
		}
	}
	
	public static class OrderPackageBean implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private BigDecimal width;
		private BigDecimal height;
		private BigDecimal length;
		private BigDecimal weight;
		
		public BigDecimal getWidth() {
			return width;
		}
		
		public void setWidth(BigDecimal width) {
			this.width = width;
		}
		
		public BigDecimal getHeight() {
			return height;
		}
		
		public void setHeight(BigDecimal height) {
			this.height = height;
		}
		
		public BigDecimal getLength() {
			return length;
		}
		
		public void setLength(BigDecimal length) {
			this.length = length;
		}
		
		public BigDecimal getWeight() {
			return weight;
		}
		
		public void setWeight(BigDecimal weight) {
			this.weight = weight;
		}
	}
	
	public static class OrderPaymentBean implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Integer paymentGwId;

		private Integer ecreditId;
		
		private String transactionAccount;
		
		private String transactionReference;
		
		private BigDecimal amount;

		private Date date;

		public Integer getPaymentGwId() {
			return paymentGwId;
		}

		public void setPaymentGwId(Integer paymentGwId) {
			this.paymentGwId = paymentGwId;
		}

		public Integer getEcreditId() {
			return ecreditId;
		}

		public void setEcreditId(Integer ecreditId) {
			this.ecreditId = ecreditId;
		}

		public String getTransactionAccount() {
			return transactionAccount;
		}

		public void setTransactionAccount(String transactionAccount) {
			this.transactionAccount = transactionAccount;
		}

		public String getTransactionReference() {
			return transactionReference;
		}

		public void setTransactionReference(String transactionReference) {
			this.transactionReference = transactionReference;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}
	}
	
	public static class OrderVat implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public OrderVat(){}
	
		private BigDecimal vat;
		private Integer vatId;
		private String vatName;
		private String vatArt;
		private BigDecimal amount;

		public BigDecimal getVat() {
			return vat;
		}

		public void setVat(BigDecimal vat) {
			this.vat = vat;
		}

		public Integer getVatId() {
			return vatId;
		}

		public void setVatId(Integer vatId) {
			this.vatId = vatId;
		}

		public String getVatName() {
			return vatName;
		}

		public void setVatName(String vatName) {
			this.vatName = vatName;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public String getVatArt() {
			return vatArt;
		}

		public void setVatArt(String vatArt) {
			this.vatArt = vatArt;
		}
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public DispatchData getDispatchData() {
		return dispatchData;
	}

	public void setDispatchData(DispatchData dispatchData) {
		this.dispatchData = dispatchData;
	}

	public InvoiceData getBillingData() {
		return billingData;
	}

	public void setBillingData(InvoiceData billingData) {
		this.billingData = billingData;
	}

	@XmlElementWrapper(name="items")
	@XmlElement(name="item")
	public List<OrderItemBean> getItems() {
		return items;
	}

	public void setItems(List<OrderItemBean> items) {
		this.items = items;
	}
	
	@XmlElementWrapper(name="vats")
	@XmlElement(name="vat")
	public List<OrderVat> getVatAmounts() {
		return vatAmounts;
	}

	public void setVatAmounts(List<OrderVat> vatAmounts) {
		this.vatAmounts = vatAmounts;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public TrackingData getTrackingData() {
		return trackingData;
	}

	public void setTrackingData(TrackingData trackingData) {
		this.trackingData = trackingData;
	}

	public Integer getSubstatus() {
		return substatus;
	}

	public void setSubstatus(Integer substatus) {
		this.substatus = substatus;
	}

	public Integer getOrigin() {
		return origin;
	}

	public void setOrigin(Integer origin) {
		this.origin = origin;
	}

	public Integer getPaymentTermsId() {
		return paymentTermIds;
	}

	public void setPaymentTermsId(Integer paymentTermsId) {
		this.paymentTermIds = paymentTermsId;
	}

	public String getExternalRef() {
		return externalRef;
	}

	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}
	
	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getTotaltaxable() {
		return totaltaxable;
	}

	public void setTotaltaxable(BigDecimal totaltaxable) {
		this.totaltaxable = totaltaxable;
	}

	public BigDecimal getTotprediscount() {
		return totprediscount;
	}

	public void setTotprediscount(BigDecimal totprediscount) {
		this.totprediscount = totprediscount;
	}

	public BigDecimal getTotprediscountTaxable() {
		return totprediscountTaxable;
	}

	public void setTotprediscountTaxable(BigDecimal totprediscountTaxable) {
		this.totprediscountTaxable = totprediscountTaxable;
	}

	public BigDecimal getTotprediscountVatAmount() {
		return totprediscountVatAmount;
	}

	public void setTotprediscountVatAmount(BigDecimal totprediscountVatAmount) {
		this.totprediscountVatAmount = totprediscountVatAmount;
	}

	public BigDecimal getDiscountused() {
		return discountused;
	}

	public void setDiscountused(BigDecimal discountused) {
		this.discountused = discountused;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public BigDecimal getVat_amount() {
		return vat_amount;
	}

	public void setVat_amount(BigDecimal vat_amount) {
		this.vat_amount = vat_amount;
	}
	
	public Integer getVatReliefId() {
		return vatReliefId;
	}

	public void setVatReliefId(Integer vatReliefId) {
		this.vatReliefId = vatReliefId;
	}

	public Integer getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Integer itemsCount) {
		this.itemsCount = itemsCount;
	}

	public BigDecimal getTotalItems() {
		return totalItems;
	}
	
	public void setTotalItems(BigDecimal totalItems) {
		this.totalItems = totalItems;
	}
	
	public BigDecimal getTaxableItems() {
		return taxableItems;
	}

	public void setTaxableItems(BigDecimal taxableItems) {
		this.taxableItems = taxableItems;
	}

	public BigDecimal getDispatchFixed() {
		return dispatchFixed;
	}

	public void setDispatchFixed(BigDecimal dispatchFixed) {
		this.dispatchFixed = dispatchFixed;
	}

	public BigDecimal getDispatchWeight() {
		return dispatchWeight;
	}

	public void setDispatchWeight(BigDecimal dispatchWeight) {
		this.dispatchWeight = dispatchWeight;
	}

	public BigDecimal getDispatchFixedTaxable() {
		return dispatchFixedTaxable;
	}

	public void setDispatchFixedTaxable(BigDecimal dispatchFixedTaxable) {
		this.dispatchFixedTaxable = dispatchFixedTaxable;
	}

	public BigDecimal getDispatchWeightTaxable() {
		return dispatchWeightTaxable;
	}

	public void setDispatchWeightTaxable(BigDecimal dispatchWeightTaxable) {
		this.dispatchWeightTaxable = dispatchWeightTaxable;
	}

	public BigDecimal getDispatchFixedVatAmount() {
		return dispatchFixedVatAmount;
	}

	public void setDispatchFixedVatAmount(BigDecimal dispatchFixedVatAmount) {
		this.dispatchFixedVatAmount = dispatchFixedVatAmount;
	}

	public BigDecimal getDispatchWeightVatAmount() {
		return dispatchWeightVatAmount;
	}

	public void setDispatchWeightVatAmount(BigDecimal dispatchWeightVatAmount) {
		this.dispatchWeightVatAmount = dispatchWeightVatAmount;
	}
	
	public BigDecimal getPaymentFee() {
		return paymentFee;
	}

	public void setPaymentFee(BigDecimal paymentFee) {
		this.paymentFee = paymentFee;
	}

	public BigDecimal getPaymentFeeTaxable() {
		return paymentFeeTaxable;
	}

	public void setPaymentFeeTaxable(BigDecimal paymentFeeTaxable) {
		this.paymentFeeTaxable = paymentFeeTaxable;
	}

	public BigDecimal getPaymentFeeVatAmount() {
		return paymentFeeVatAmount;
	}

	public void setPaymentFeeVatAmount(BigDecimal paymentFeeVatAmount) {
		this.paymentFeeVatAmount = paymentFeeVatAmount;
	}

	public BigDecimal getEcredit() {
		return ecredit;
	}
	public void setEcredit(BigDecimal ecredit) {
		this.ecredit = ecredit;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public AnagraficaData getAnagrafica() {
		return anagrafica;
	}

	public void setAnagrafica(AnagraficaData anagrafica) {
		this.anagrafica = anagrafica;
	}

	public String getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	@XmlElementWrapper(name="creditNotes")
	@XmlElement(name="creditNote")
	public List<InvoiceData> getCreditNotes() {
		return creditNotes;
	}

	public void setCreditNotes(List<InvoiceData> creditNotes) {
		this.creditNotes = creditNotes;
	}

	@XmlElementWrapper(name="properties")
	@XmlElement(name="property")
	public List<PropertyData> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyData> properties) {
		this.properties = properties;
	}
	
	@XmlElementWrapper(name="attachments")
	@XmlElement(name="attachment")
	public List<OrderAttachmentBean> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<OrderAttachmentBean> attachments) {
		this.attachments = attachments;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAdminNotes() {
		return adminNotes;
	}

	public void setAdminNotes(String adminNotes) {
		this.adminNotes = adminNotes;
	}
	
	@XmlElementWrapper(name="packages")
	@XmlElement(name="package")
	public List<OrderPackageBean> getPackages() {
		return packages;
	}

	public void setPackages(List<OrderPackageBean> packages) {
		this.packages = packages;
	}

	public List<OrderPaymentBean> getPayments() {
		return payments;
	}

	public void setPayments(List<OrderPaymentBean> payments) {
		this.payments = payments;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public Date getExpireDate() {
		return expireDate;
	}
	
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
}
