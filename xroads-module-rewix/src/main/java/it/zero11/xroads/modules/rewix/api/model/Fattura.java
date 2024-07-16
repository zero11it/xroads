package it.zero11.xroads.modules.rewix.api.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.MapKey;
import javax.persistence.OneToMany;

public class Fattura {
	
	public static final String TYPE_INVOICE 	= "invoice";
	public static final String TYPE_CREDITNOTE	= "creditnote";
	
	private Integer fattura_id;
	
	private Integer invoice_no;
	private String suffix;
	
	private Integer country_id;
	private String dest, address, city, prov, cfpiva, cap;
	private BigDecimal ecredit;
	private Date data;
	private BigDecimal vat;
	private Integer vat_system_id;
	
	//Nota di credito
	private Integer credit_id;
	private Date credit_date;
	private String username;
	private String type=TYPE_INVOICE;
	
	private String letteraIntento;
	
	private String payment_info;
	
	private Integer refInvoice;
	
	private String currency;
	private BigDecimal exchange_rate;
	
	private Integer paymentTermsId;
	
	private Date paymentDate;
	
	public Fattura(){
		data = new Date();		
	}

	public Integer getFattura_id() {
		return fattura_id;
	}

	public void setFattura_id(Integer fattura_id) {
		this.fattura_id = fattura_id;
	}
	/*
	private Integer order_id;
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	*/
	
	@OneToMany(mappedBy="pkey.fattura_id")
	private List<OrderInvoice> orders_list;
	
	@OneToMany(mappedBy="pkey.fattura_id")
	@MapKey(name="pkey.order_id")
	private Map<Integer, OrderInvoice> orders_map;
	
	public List<OrderInvoice> getOrders_list() {
		return orders_list;
	}

	public void setOrders_list(List<OrderInvoice> orders_list) {
		this.orders_list = orders_list;
	}

	public Map<Integer, OrderInvoice> getOrders_map() {
		return orders_map;
	}

	public void setOrders_map(Map<Integer, OrderInvoice> orders_map) {
		this.orders_map = orders_map;
	}

	

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getCfpiva() {
		return cfpiva;
	}

	public void setCfpiva(String cfpiva) {
		this.cfpiva = cfpiva;
	}

	public Integer getCredit_id() {
		return credit_id;
	}

	public void setCredit_id(Integer credit_id) {
		this.credit_id = credit_id;
	}

	public Date getCredit_date() {
		return credit_date;
	}

	public void setCredit_date(Date credit_date) {
		this.credit_date = credit_date;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	
	public void fillFromOrder(Order ord){
		this.dest = ord.getDest();
		try{
			this.address = ord.getAddress();
		}catch (Exception e) {}
		
		this.cap = ord.getCap();
		this.city = ord.getCity();
		this.prov = ord.getProv();

		this.country_id = ord.getCountry_id();	
		
		this.cfpiva = ord.getCfpiva();
		this.username = ord.getCustomer();
	}

	public void fillFromInvoice(Fattura invoice) {
		this.dest = invoice.getDest();
		try{
			this.address = invoice.getAddress();
		}catch (Exception e) {}
		
		this.cap = invoice.getCap();
		this.city = invoice.getCity();
		this.prov = invoice.getProv();

		this.country_id = invoice.getCountry_id();	
		
		this.cfpiva = invoice.getCfpiva();
		this.username = invoice.getUsername();
		this.currency = invoice.getCurrency();
		this.exchange_rate = invoice.getExchange_rate();
	}
	
	public void fillFromInvoiceAddress(Address a){
		this.dest = a.getAddressee();
		this.address = a.getAddress();
		
		this.cap = a.getCap();
		this.city = a.getCity();
		this.prov = a.getProv();

		this.country_id = a.getCountry_id();
		this.username = a.getUsername();
	}

	public Integer getInvoice_no() {
		return invoice_no;
	}

	public void setInvoice_no(Integer invoice_no) {
		this.invoice_no = invoice_no;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Integer getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Integer country_id) {
		this.country_id = country_id;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public Integer getVat_system_id() {
		return vat_system_id;
	}

	public void setVat_system_id(Integer vat_system_id) {
		this.vat_system_id = vat_system_id;
	}

	public String getLetteraIntento() {
		return letteraIntento;
	}

	public void setLetteraIntento(String letteraIntento) {
		this.letteraIntento = letteraIntento;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getRefInvoice() {
		return refInvoice;
	}

	public void setRefInvoice(Integer refInvoice) {
		this.refInvoice = refInvoice;
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

	public String getPayment_info() {
		return payment_info;
	}

	public void setPayment_info(String payment_info) {
		this.payment_info = payment_info;
	}

	public BigDecimal getEcredit() {
		return ecredit;
	}

	public void setEcredit(BigDecimal ecredit) {
		this.ecredit = ecredit;
	}

	public Integer getPaymentTermsId() {
		return paymentTermsId;
	}

	public void setPaymentTermsId(Integer paymentTermsId) {
		this.paymentTermsId = paymentTermsId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
}
