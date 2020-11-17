package it.zero11.xroads.modules.rewix.api.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="anagrafica")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnagraficaBean {
	@XmlAttribute
	private Date birth;
	
	@XmlAttribute
	private String businessName;
		
	@XmlAttribute
	private String fax;
	
	@XmlAttribute
	private String firstName;
	
	@XmlAttribute
	private String lastName;
	
	@XmlAttribute
	private String mobilePhone;
	
	@XmlAttribute
	private String mobilePhonePrefix;
	
	@XmlAttribute
	private String note;
	
	@XmlAttribute
	private String note2;
	
	@XmlAttribute
	private String phone;
	
	@XmlAttribute
	private String phonePrefix;
	
	@XmlAttribute
	private String privateNotes;
	
	@XmlAttribute
	private String skype;
	
	@XmlAttribute
	private String validatedVatNumber;
	
	@XmlAttribute
	private String vatNumber;

	@XmlAttribute
	private String website;

	@XmlAttribute
	private String pec;

	@XmlAttribute
	private String sdi;

	@XmlAttribute
	private String loyalityCard;

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
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

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getMobilePhonePrefix() {
		return mobilePhonePrefix;
	}

	public void setMobilePhonePrefix(String mobilePhonePrefix) {
		this.mobilePhonePrefix = mobilePhonePrefix;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote2() {
		return note2;
	}

	public void setNote2(String note2) {
		this.note2 = note2;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhonePrefix() {
		return phonePrefix;
	}

	public void setPhonePrefix(String phonePrefix) {
		this.phonePrefix = phonePrefix;
	}

	public String getPrivateNotes() {
		return privateNotes;
	}

	public void setPrivateNotes(String privateNotes) {
		this.privateNotes = privateNotes;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public String getValidatedVatNumber() {
		return validatedVatNumber;
	}

	public void setValidatedVatNumber(String validatedVatNumber) {
		this.validatedVatNumber = validatedVatNumber;
	}

	public String getVatNumber() {
		return vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
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

	public String getLoyalityCard() {
		return loyalityCard;
	}

	public void setLoyalityCard(String loyalityCard) {
		this.loyalityCard = loyalityCard;
	}
}
