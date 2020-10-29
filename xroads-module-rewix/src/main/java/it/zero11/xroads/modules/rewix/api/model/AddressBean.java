package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="address")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressBean {
	@XmlAttribute
	private String type;

	@XmlAttribute
	private String addressType;

	@XmlAttribute
	private String countryCode;

	@XmlAttribute
	private String mobilePhone;

	@XmlAttribute
	private String mobilePhonePrefix;

	@XmlAttribute
	private String number;

	@XmlAttribute
	private String province;

	@XmlAttribute
	private String street;

	@XmlAttribute
	private String careOf;

	@XmlAttribute
	private String addressee;

	@XmlAttribute
	private String city;

	@XmlAttribute
	private String zip;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCareOf() {
		return careOf;
	}

	public void setCareOf(String careOf) {
		this.careOf = careOf;
	}

	public String getAddressee() {
		return addressee;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
}
