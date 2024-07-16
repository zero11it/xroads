package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import it.zero11.xroads.modules.rewix.api.model.GhostEnvelope.GhostOrder;
import it.zero11.xroads.modules.rewix.api.model.GhostEnvelope.GhostOrder.RecipientDetails;
import it.zero11.xroads.modules.rewix.api.model.GhostEnvelope.GhostOrder.RecipientDetails.Phone;

public class Order implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	public static final String GW_CREDIT_TRANSFER = "CREDIT_TRANSFER";
	public static final String GW_CASHONDELIVERY = "COD";
	
	public static final int ORDER_PENDING=0;
	public static final int ORDER_PENDING_NO_LOCK=10;
	
	public static final int ORDER_MONEYWAITING=1;

	public static final int ORDER_TODISPATCH=2;
	public static final int ORDER_DISPATCHED=3;
		
	public static final int ORDER_BOOKED = 5;
	public static final int ORDER_NOT_AUTHORIZED = 5001;
	public static final int ORDER_AUTHORIZED = 5002;
	public static final int ORDER_DS_GROWING = 5003;
	public static final int ORDER_AUTHORIZED_PHOTO = 5004;
	
	public static final int ORDER_DROPSHIPPING = 6;

	public static final int ORDER_WISHLIST = 11;

	
	public static final int ORDER_MONEYERROR=1000;
	
	public static final int ORDER_CANCELED=2000;
	
	public static final int ORDER_EXCEPTION=2001;
	public static final int ORDER_VERIFY_FAILED=2002;
	
	public static final int ORDER_REFUND=2003;
	public static final int ORDER_REJECTED=2005;
	
	public static final int ORDER_WALLET_PAYMENT_SCHEDULED=4000;
	
	//Logistics
	public static final int ORDER_WORKING_ON=3001;
	public static final int ORDER_READY=3002;
	public static final int ORDER_NOT_DISPATCHABLE=3003;
	public static final int ORDER_NOT_DISPATCHABLE_READY=3006;
	public static final int ORDER_MISSINGITEMS=3004;
	public static final int ORDER_DISPATCHNOTALLOWED=3005;
	public static final int ORDER_USER_NOT_VALIDATED=3007;

	public static final int ORIGIN_FRONTEND = 0;
	public static final int ORIGIN_API = 1;
	public static final int ORIGIN_ADMIN = 2;
	public static final int ORIGIN_SUBSCRIPTION = 3;
	public static final int ORIGIN_FRONTEND_ADMIN = 4;
	public static final int ORIGIN_EXTERNAL = 5;

	public static final Map<Integer, String> ORIGINS;
	static{
		Map<Integer, String> origins = new HashMap<Integer, String>();
		origins.put(ORIGIN_FRONTEND, "Frontend");
		origins.put(ORIGIN_API, "API");
		origins.put(ORIGIN_ADMIN, "Admin");
		origins.put(ORIGIN_SUBSCRIPTION, "Subscription");
		origins.put(ORIGIN_FRONTEND_ADMIN, "Frontend (Admin)");
		origins.put(ORIGIN_EXTERNAL, "External");
		ORIGINS = Collections.unmodifiableMap(origins);
	}
		
	public static String readableStatus(int s){
		if (s==ORDER_PENDING)
			return "Pending";
		if (s==ORDER_PENDING_NO_LOCK)
			return "Pending (no lock)";
		else if (s==ORDER_MONEYWAITING)
			return "Money Waiting";
		else if (s==ORDER_MONEYERROR)
			return "Payment cancelled";
		else if (s==ORDER_WALLET_PAYMENT_SCHEDULED)
			return "Payment scheduled";
		else if (s==ORDER_TODISPATCH)
			return"Confirmed" ;
		else if (s==ORDER_DISPATCHED)
			return "Dispatched" ;
		else if (s==ORDER_BOOKED)
			return "Booked" ;
		else if (s==ORDER_CANCELED)
			return "Cancelled";
		else if (s==ORDER_REJECTED)
			return "Rejected";
		else if (s==ORDER_VERIFY_FAILED)
			return "Payment Verification Failed";
		else if (s==ORDER_WORKING_ON)
			return "Working on";
		else if (s==ORDER_READY)
			return "Ready";
		else if (s==ORDER_NOT_DISPATCHABLE)
			return "Not dispatchable";
		else if (s==ORDER_MISSINGITEMS)
			return "Missing Items";
		else if (s==ORDER_DISPATCHNOTALLOWED)
			return "Not allowed to dispatch";
		else if (s==ORDER_USER_NOT_VALIDATED)
			return "Not Validated";
		else if (s==ORDER_NOT_DISPATCHABLE_READY)
			return "Not dispatchable (Ready)";
		else if (s==ORDER_REFUND)
			return "Refunded";
		else if (s==ORDER_NOT_AUTHORIZED)
			return "Not authorized";
		else if (s==ORDER_AUTHORIZED)
			return "Authorized";
		else if (s==ORDER_DS_GROWING)
			return "DropShipping - Growing";
		else if (s==ORDER_DROPSHIPPING)
			return "Dropshipping";
		else if (s==ORDER_WISHLIST)
			return "Wishlist";
		else
			return "STATO NON RICONOSCIUTO";
		
	}
	
	public static String htmlStatus(int s, int substatus){
		if (s==ORDER_PENDING)
			return String.format("<span style=\"color:%s;\">%s</span>", "gray", readableStatus(s) );
		else if (s==ORDER_PENDING_NO_LOCK)
			return String.format("<span style=\"color:%s;\">%s</span>", "gray", readableStatus(s) );
		else if (s==ORDER_MONEYWAITING)
			return String.format("<span style=\"color:%s;\">%s</span>", "yellow", readableStatus(s) );
		else if (s==ORDER_MONEYERROR)
			return String.format("<span style=\"color:%s;\">%s</span>", "red", readableStatus(s) );
		else if (s==ORDER_TODISPATCH)
			return String.format("<span style=\"color:%s;\">%s</span>", "orange", readableStatus(s));
		else if (s==ORDER_CANCELED)
			return String.format("<span style=\"color:%s;\">%s [%s]</span>", "red",readableStatus(s), readableStatus(substatus) );
		else if (s==ORDER_WORKING_ON)
			return String.format("<span style=\"color:%s;\">%s</span>", "#B82446",readableStatus(s) );
		else if (s==ORDER_READY)
			return String.format("<span style=\"color:%s;\">%s</span>", "#60BF94",readableStatus(s) );
		else if (s==ORDER_DISPATCHED)
			return String.format("<span style=\"color:%s;\">%s</span>", "green", readableStatus(s) );
		else if (s==ORDER_BOOKED)
			return String.format("<span style=\"color:%s;\">%s%s</span>", "green", readableStatus(s), 
					(substatus>0) ? " ["+readableStatus(substatus)+"]": "" );	
		else if (s==ORDER_DROPSHIPPING)
			return String.format("<span style=\"color:%s;\">%s%s</span>", "green", (substatus>0) ? readableStatus(substatus): "", 
				" ["+readableStatus(s)+"]" );	
		else if (s==ORDER_NOT_DISPATCHABLE)
			return String.format("<span style=\"color:%s;\">%s</span>", "#B82446",readableStatus(s) );
		else if (s==ORDER_WISHLIST)
			return String.format("<span style=\"color:%s;\">%s</span>", "indigo", readableStatus(s) );
		else
			return "STATO NON RICONOSCIUTO";
		
	}
	

	public boolean isPayed(){
		if (status.equals(Order.ORDER_TODISPATCH) || 
			status.equals(Order.ORDER_BOOKED) ||
			status.equals(Order.ORDER_CANCELED) ||
			status.equals(Order.ORDER_DROPSHIPPING) ||
			status.equals(Order.ORDER_WORKING_ON) ||
			status.equals(Order.ORDER_READY) ||
			(payment_callback !=null &&
			   (payment_callback.toUpperCase().equals("OK") ||
			    payment_callback.toUpperCase().equals("EX"))
			)){
			return true;
		}else{
			return false;
		}
	} 
	
	/*
	@Transient
	public boolean isBooked(){
		return status==ORDER_BOOKED;
	} 
	*/
	

	
	
	private Integer order_id;
	
	private String customer, dest, careof;
	
	private Integer country_id;	
	
	private Integer carrier_id;
	
	private BigDecimal vat;
	private Integer vat_system_id;
	
	private String city, prov, address_type, street, number, notes, cel_prefix, cel, cfpiva, recipient_cfpiva;
	
	private String cap, recipient_email;
	
	private Date submit_date, dispatch_date, last_update;
	
	private Integer status, substatus;
	
	private String payment_callback, payment_gw;
	
	private BigDecimal paymentAmount; // Excluding gw fee
	
	private Integer group_id;
	
	private Integer supplier_id;

	private BigDecimal dispatch_fixed=BigDecimal.ZERO, dispatch_weight=BigDecimal.ZERO;
	private BigDecimal vat_amount_dispatch_fixed=BigDecimal.ZERO, vat_amount_dispatch_weight=BigDecimal.ZERO;
	
	private BigDecimal paymentFee=BigDecimal.ZERO;
	
	private BigDecimal vatAmountPaymentFee=BigDecimal.ZERO;
	
	@Deprecated
	private BigDecimal discount = BigDecimal.ZERO;
	
	private BigDecimal ecredit = BigDecimal.ZERO;
	
	private String admin_notes;
	
	private Integer paymentTermsId;
	
	private String  validatedVat;

	private Integer origin;
	
	//External order_id for ghost orders
	private String ext_ref;
	
	//Internal order_id for ghost orders
	@Deprecated
	private Integer order_ref;

	private String trackingcode, trackingurl, carriername;

	
	private String currency;
	private BigDecimal exchange_rate;
	
	private Integer logistics_id;
	
	public Integer getLogistics_id() {
		return logistics_id;
	}

	public void setLogistics_id(Integer logistics_id) {
		this.logistics_id = logistics_id;
	}

	@Column(name="payment_gw_id")
	private Integer paymentGwId;
	
	public Integer getSubstatus() {
		return substatus;
	}

	public void setSubstatus(Integer substatus) {
		this.substatus = substatus;
	}

	public Order(){}

	public Integer getOrder_id() {
		return order_id;
	}

	
	private List<OrderInvoice> invoice_list;
	
	private Map<Integer, OrderInvoice> invoice_map;
	
	private List<OrderProperties> properties;
	
	private Map<Integer, OrderProperties> propertiesMap;
	
	public List<OrderInvoice> getInvoice_list() {
		return invoice_list;
	}

	public void setInvoice_list(List<OrderInvoice> invoice_list) {
		this.invoice_list = invoice_list;
	}

	public Map<Integer, OrderInvoice> getInvoice_map() {
		return invoice_map;
	}

	public void setInvoice_map(Map<Integer, OrderInvoice> invoice_map) {
		this.invoice_map = invoice_map;
	}
	
	public String getCustomer() {
		return customer;
	}

	public Date getSubmit_date() {
		return submit_date;
	}

	public Integer getStatus() {
		return status;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setSubmit_date(Date submit_date) {
		this.submit_date = submit_date;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getCity() {
		return city;
	}

	public String getProv() {
		return prov;
	}


	public void setCity(String city) {
		this.city = city;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}



	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap!=null ? cap.trim() : cap;
	}

	public String getAddress() {
		return String.format("%s%s%s", address_type!=null ? address_type+" " : "", street+" ", number);
	}

	public String getNumber() {
		return number;
	}



	public void setNumber(String number) {
		this.number = number;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public void fillDispatch(Address a){		
		dest = a.getAddressee();
		careof= a.getCareof();
		
		address_type = a.getAddress_type();
		street = a.getStreet();
		number = a.getNumber();
		cap = a.getCap();
		
		city = a.getCity();
		prov = a.getProv();
		
		cel_prefix = a.getCel_prefix();
		cel = a.getCel();

		country_id = a.getCountry_id();
	}

	
	public void fillFromGhostOrder(GhostOrder ge_order, int countryId){

		// Recipient
		RecipientDetails recipient = ge_order.getRecipient_details();
		this.setDest(recipient.getRecipient());
		this.setCareof(recipient.getCareof());
		this.setNotes(recipient.getNotes());
		this.setRecipient_cfpiva(recipient.getCfpiva());
		//Recipient -> address
		it.zero11.xroads.modules.rewix.api.model.GhostEnvelope.GhostOrder.RecipientDetails.Address address = recipient.getAddress();
		this.setAddress_type(address.getStreet_type());
		this.setStreet(address.getStreet_name());
		this.setNumber(address.getAddress_number());
		this.setCap(address.getZip());
		this.setCity(address.getCity());
		this.setProv(address.getProvince());
		this.setCountry_id(countryId);
		//Recipient -> phone
		Phone phone = recipient.getPhone();
		this.setCel_prefix(phone.getPrefix());
		this.setCel(phone.getNumber());
		
	}
	
	
	public Order clone() throws CloneNotSupportedException {
		Order o = (Order)super.clone();
		o.setOrder_id(null);
		o.setInvoice_list(null);
		o.setInvoice_map(null);
		o.setProperties(null);
		o.setPropertiesMap(null);

		return o;
	}
	
	public String getPayment_callback() {
		return payment_callback;
	}



	public void setPayment_callback(String payment_callback) {
		this.payment_callback = payment_callback;
	}

	public Date getDispatch_date() {
		return dispatch_date;
	}

	public void setDispatch_date(Date dispatch_date) {
		this.dispatch_date = dispatch_date;
	}

	public Date getLast_update() {
		return last_update;
	}

	public void setLast_update(Date last_update) {
		this.last_update = last_update;
	}



	public Integer getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Integer group_id) {
		this.group_id = group_id;
	}

	public String getAddress_type() {
		return address_type;
	}

	public void setAddress_type(String address_type) {
		this.address_type = address_type;
	}

	public String getCel() {
		return cel;
	}

	public void setCel(String cel) {
		this.cel = cel;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCel_prefix() {
		return cel_prefix;
	}

	public void setCel_prefix(String cel_prefix) {
		this.cel_prefix = cel_prefix;
	}

	public String getCfpiva() {
		return cfpiva;
	}

	public void setCfpiva(String cfpiva) {
		this.cfpiva = cfpiva;
	}
	
	public String getRecipient_cfpiva() {
		return recipient_cfpiva;
	}

	public void setRecipient_cfpiva(String recipient_cfpiva) {
		this.recipient_cfpiva = recipient_cfpiva;
	}

	public String getRecipient_email() {
		return recipient_email;
	}

	public void setRecipient_email(String recipient_email) {
		this.recipient_email = recipient_email;
	}

	public String getPayment_gw() {
		return payment_gw;
	}

	public void setPayment_gw(String payment_gw) {
		this.payment_gw = payment_gw;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Integer getPaymentTermsId() {
		return paymentTermsId;
	}

	public void setPaymentTermsId(Integer paymentTermsId) {
		this.paymentTermsId = paymentTermsId;
	}

	public Integer getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Integer country_id) {
		this.country_id = country_id;
	}

	public Integer getCarrier_id() {
		return carrier_id;
	}

	public void setCarrier_id(Integer carrier_id) {
		this.carrier_id = carrier_id;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public BigDecimal getDispatch_fixed() {
		return dispatch_fixed;
	}

	public void setDispatch_fixed(BigDecimal dispatch_fixed) {
		this.dispatch_fixed = dispatch_fixed;
	}

	public BigDecimal getDispatch_weight() {
		return dispatch_weight;
	}

	public void setDispatch_weight(BigDecimal dispatch_weight) {
		this.dispatch_weight = dispatch_weight;
	}

	public BigDecimal getVat_amount_dispatch_fixed() {
		return vat_amount_dispatch_fixed;
	}

	public void setVat_amount_dispatch_fixed(BigDecimal vat_amount_dispatch_fixed) {
		this.vat_amount_dispatch_fixed = vat_amount_dispatch_fixed;
	}

	public BigDecimal getVat_amount_dispatch_weight() {
		return vat_amount_dispatch_weight;
	}

	public void setVat_amount_dispatch_weight(BigDecimal vat_amount_dispatch_weight) {
		this.vat_amount_dispatch_weight = vat_amount_dispatch_weight;
	}

	public BigDecimal getPaymentFee() {
		return paymentFee;
	}

	public void setPaymentFee(BigDecimal paymentFee) {
		this.paymentFee = paymentFee;
	}

	public BigDecimal getVatAmountPaymentFee() {
		return vatAmountPaymentFee;
	}

	public void setVatAmountPaymentFee(BigDecimal vatAmountPaymentFee) {
		this.vatAmountPaymentFee = vatAmountPaymentFee;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getExchange_rate() {
		return exchange_rate;
	}

	public void setExchange_rate(BigDecimal exchange_rate) {
		this.exchange_rate = exchange_rate;
	}

	@Deprecated
	public BigDecimal getDiscount() {
		return discount;
	}

	@Deprecated
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public String getAdmin_notes() {
		return admin_notes;
	}

	public void setAdmin_notes(String admin_notes) {
		this.admin_notes = admin_notes;
	}

	public String getValidatedVat() {
		return validatedVat;
	}

	public void setValidatedVat(String validatedVat) {
		this.validatedVat = validatedVat;
	}

	public Integer getOrigin() {
		return origin;
	}

	public void setOrigin(Integer origin) {
		this.origin = origin;
	}

	public String getExt_ref() {
		return ext_ref;
	}

	public void setExt_ref(String ext_ref) {
		this.ext_ref = ext_ref;
	}

	@Deprecated
	public Integer getOrder_ref() {
		return order_ref;
	}

	@Deprecated
	public void setOrder_ref(Integer order_ref) {
		this.order_ref = order_ref;
	}

	public Integer getVat_system_id() {
		return vat_system_id;
	}

	public void setVat_system_id(Integer vat_system_id) {
		this.vat_system_id = vat_system_id;
	}
	
	public boolean isVatValidated(){
		return cfpiva!=null && validatedVat!=null && cfpiva.equals(validatedVat);
	}

	public String getCareof() {
		return careof;
	}

	public void setCareof(String careof) {
		this.careof = careof;
	}

	public String getTrackingcode() {
		return trackingcode;
	}

	public void setTrackingcode(String trackingcode) {
		this.trackingcode = trackingcode;
	}

	public String getTrackingurl() {
		return trackingurl;
	}

	public void setTrackingurl(String trackingurl) {
		this.trackingurl = trackingurl;
	}

	public String getCarriername() {
		return carriername;
	}

	public void setCarriername(String carriername) {
		this.carriername = carriername;
	}

	public List<OrderProperties> getProperties() {
		return properties;
	}

	public void setProperties(List<OrderProperties> properties) {
		this.properties = properties;
	}

	public Map<Integer, OrderProperties> getPropertiesMap() {
		return propertiesMap;
	}

	public void setPropertiesMap(Map<Integer, OrderProperties> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}

	public Integer getPaymentGwId() {
		return paymentGwId;
	}

	public void setPaymentGwId(Integer paymentGwId) {
		this.paymentGwId = paymentGwId;
	}

	public Integer getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(Integer supplier_id) {
		this.supplier_id = supplier_id;
	}

	public BigDecimal getEcredit() {
		return ecredit;
	}

	public void setEcredit(BigDecimal ecredit) {
		this.ecredit = ecredit;
	}
}
