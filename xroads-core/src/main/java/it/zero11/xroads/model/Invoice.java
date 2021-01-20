package it.zero11.xroads.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;
@Entity
@Table(name="invoices")
public class Invoice extends AbstractCustomerGroupedEntity{
	private static final long serialVersionUID = 1L;
	
	@Column(name="invoice_number")
	private String invoiceNumber;
	
	@Column(name="vat_number")
	private String vatNumber;
	
	@Column(name="year")
	private Integer year;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;
	
    @Column(name="document_type")
	private String documentType;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="line_items")
	private JsonNode lineItems;
	
	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="totals")
	private JsonNode totals;

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getVatNumber() {
		return vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public JsonNode getLineItems() {
		return lineItems;
	}

	public void setLineItems(JsonNode lineItems) {
		this.lineItems = lineItems;
	}

	public JsonNode getTotals() {
		return totals;
	}

	public void setTotals(JsonNode totals) {
		this.totals = totals;
	}

	public JsonNode getData() {
		return data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
}
