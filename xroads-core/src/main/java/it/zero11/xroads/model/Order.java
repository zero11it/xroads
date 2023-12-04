package it.zero11.xroads.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name="orders")
public class Order extends AbstractCustomerGroupedEntity {
	private static final long serialVersionUID = 1L;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="anagrafica")
	private JsonNode anagrafica;

	private String currency;

	@Column(name="customer_email")
	private String customerEmail;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	@Column(name="dispatch_taxable")
	private BigDecimal dispatchTaxable;

	@Column(name="dispatch_total")
	private BigDecimal dispatchTotal;

	@Column(name="dispatch_vat")
	private BigDecimal dispatchVat;


	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="invoice_address")
	private JsonNode invoiceAddress;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="line_items")
	private JsonNode lineItems;

	@Column(name="order_date")
	private Timestamp orderDate;

	@Column(name="payment_gateway")
	private String paymentGateway;
	
	@Type( type = "jsonb-node" )
	@Column(name="payments")
	private JsonNode payments;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="shipping_address")
	private JsonNode shippingAddress;

	private String source;

	private Integer status;

	private BigDecimal total;

	@Column(name="total_vat")
	private BigDecimal totalVat;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="totals")
	private JsonNode totals;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="properties")
	private JsonNode properties;
	
	@Column(name="rewix_merchant_id")
	private Integer rewixMerchantId;

	public JsonNode getAnagrafica() {
		return this.anagrafica;
	}

	public void setAnagrafica(JsonNode anagrafica) {
		this.anagrafica = anagrafica;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCustomerEmail() {
		return this.customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public JsonNode getData() {
		return this.data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public BigDecimal getDispatchTaxable() {
		return this.dispatchTaxable;
	}

	public void setDispatchTaxable(BigDecimal dispatchTaxable) {
		this.dispatchTaxable = dispatchTaxable;
	}

	public BigDecimal getDispatchTotal() {
		return this.dispatchTotal;
	}

	public void setDispatchTotal(BigDecimal dispatchTotal) {
		this.dispatchTotal = dispatchTotal;
	}

	public BigDecimal getDispatchVat() {
		return this.dispatchVat;
	}

	public void setDispatchVat(BigDecimal dispatchVat) {
		this.dispatchVat = dispatchVat;
	}

	public JsonNode getInvoiceAddress() {
		return this.invoiceAddress;
	}

	public void setInvoiceAddress(JsonNode invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}

	public JsonNode getLineItems() {
		return this.lineItems;
	}

	public void setLineItems(JsonNode lineItems) {
		this.lineItems = lineItems;
	}

	public Timestamp getOrderDate() {
		return this.orderDate;
	}

	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

	public String getPaymentGateway() {
		return this.paymentGateway;
	}

	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	public JsonNode getShippingAddress() {
		return this.shippingAddress;
	}

	public void setShippingAddress(JsonNode shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getTotal() {
		return this.total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getTotalVat() {
		return this.totalVat;
	}

	public void setTotalVat(BigDecimal totalVat) {
		this.totalVat = totalVat;
	}

	public JsonNode getTotals() {
		return this.totals;
	}

	public void setTotals(JsonNode totals) {
		this.totals = totals;
	}
	
	public JsonNode getPayments() {
		return payments;
	}

	public void setPayments(JsonNode payments) {
		this.payments = payments;
	}

	public JsonNode getProperties() {
		return properties;
	}

	public void setProperties(JsonNode properties) {
		this.properties = properties;
	}

	public Integer getRewixMerchantId() {
		return rewixMerchantId;
	}

	public void setRewixMerchantId(Integer rewixMerchantId) {
		this.rewixMerchantId = rewixMerchantId;
	}
	
}