package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class XOrder implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer status, substatus;
	private String externalRef;
	private Date submitDate;
	private DispatchData dispatchData;
	private BillingData billingData;
	private TrackingData trackingData;
	private List<XOrderItem> items;	
	private String currency;
	private Double total;
	
	public XOrder(){}
	
	public static class TrackingData{
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
	
	public static class DispatchData{
		private String recipient;
		private String careOf;
		private String address;
		private String zip;
		private String country;
		
		
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
	}
	
	public static class BillingData{
		
		private boolean billed;
		
		private Integer billId;
		private String billNo;
		private List<Integer> creditNoteIds;
		
		private String billTo;
		private String vatNumber;
		private String address;
		private String zip;
		private String country;
		
		public BillingData(){}
		
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
		@XmlAttribute
		public boolean isBilled() {
			return billed;
		}

		public void setBilled(boolean billed) {
			this.billed = billed;
		}

		@XmlElementWrapper(name="creditNotes")
		@XmlElement(name="creditNote")
		public List<Integer> getCreditNoteIds() {
			return creditNoteIds;
		}

		public void setCreditNoteIds(List<Integer> creditNoteIds) {
			this.creditNoteIds = creditNoteIds;
		}
	}
	
	@XmlRootElement(name = "item")
	@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
	public static class XOrderItem{
		private Integer id;
		
		private String SKU;
		private String brand;
		private String name;
		private String description;
		
		private Float tax;
		private Float unitTaxable;
		private Float totalTaxable;
		private Float unitPrice, totalPrice;
		private Float discount;
		
		private Integer quantity;
		
		private String imageURL;
		
		public XOrderItem(){}

		public String getSKU() {
			return SKU;
		}

		public void setSKU(String sKU) {
			SKU = sKU;
		}

		public String getBrand() {
			return brand;
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

		public Float getTax() {
			return tax;
		}

		public void setTax(Float tax) {
			this.tax = tax;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
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

		public Float getUnitTaxable() {
			return unitTaxable;
		}

		public void setUnitTaxable(Float unitTaxable) {
			this.unitTaxable = unitTaxable;
		}

		public Float getTotalTaxable() {
			return totalTaxable;
		}

		public void setTotalTaxable(Float totalTaxable) {
			this.totalTaxable = totalTaxable;
		}

		public Float getUnitPrice() {
			return unitPrice;
		}

		public void setUnitPrice(Float unitPrice) {
			this.unitPrice = unitPrice;
		}

		public Float getTotalPrice() {
			return totalPrice;
		}

		public void setTotalPrice(Float totalPrice) {
			this.totalPrice = totalPrice;
		}

		public Float getDiscount() {
			return discount;
		}

		public void setDiscount(Float discount) {
			this.discount = discount;
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

	public String getExternalRef() {
		return externalRef;
	}

	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
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

	public BillingData getBillingData() {
		return billingData;
	}

	public void setBillingData(BillingData billingData) {
		this.billingData = billingData;
	}

	@XmlElementWrapper(name="items")
	@XmlElement(name="item")
	public List<XOrderItem> getItems() {
		return items;
	}

	public void setItems(List<XOrderItem> items) {
		this.items = items;
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

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
	
}
