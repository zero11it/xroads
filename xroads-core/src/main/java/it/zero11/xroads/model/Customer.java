package it.zero11.xroads.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
public class Customer extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="addresses")
	private JsonNode addresses;

	private String company;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="data")
	private JsonNode data;

	@Temporal(TemporalType.DATE)
	@Column(name="date_of_birth")
	private Date dateOfBirth;

	private String email;

	private String firstname;

	@Column(name="fiscal_code")
	private String fiscalCode;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="groups")
	private JsonNode groups;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="payment_terms")
	private JsonNode paymentTerms;
	
	@Column(name="language_code")
	private String languageCode;

	private String lastname;

	@Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb", name="phone")
	private JsonNode phone;

	private String username;

	@Column(name="vat_number")
	private String vatNumber;

	public Customer() {
	}

	public JsonNode getAddresses() {
		return this.addresses;
	}

	public void setAddresses(JsonNode addresses) {
		this.addresses = addresses;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public JsonNode getData() {
		return this.data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getFiscalCode() {
		return this.fiscalCode;
	}

	public void setFiscalCode(String fiscalCode) {
		this.fiscalCode = fiscalCode;
	}

	public JsonNode getGroups() {
		return this.groups;
	}

	public void setGroups(JsonNode groups) {
		this.groups = groups;
	}

	public String getLanguageCode() {
		return this.languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public JsonNode getPhone() {
		return this.phone;
	}

	public void setPhone(JsonNode phone) {
		this.phone = phone;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVatNumber() {
		return this.vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	public JsonNode getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(JsonNode paymentTerms) {
		this.paymentTerms = paymentTerms;
	}
	
}