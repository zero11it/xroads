package it.zero11.xroads.modules.rewixsource.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserCreateBean {
	@XmlAttribute
	private String username;
	
	@XmlAttribute
	private String email;
	
	@XmlAttribute
	private String password;
	
	@XmlAttribute
	private String localeCode;
	
	@XmlAttribute
	private String clausola1;
	
	@XmlAttribute
	private String clausola2;
	
	@XmlAttribute
	private String platformUid;

	@XmlAttribute
	private Integer countryId;
	
	@XmlAttribute
	private Integer channelId;

	@XmlAttribute
	private String countryCode;
	
	@XmlAttribute
	@XmlJavaTypeAdapter(value=JAXBStringURLEncodedToIntegerArray.class)
	private Integer[] consents;
	
	@XmlAttribute
	private String consentsIP;
	
	@XmlAttribute
	private Boolean sendActivationEmail;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}

	public String getClausola1() {
		return clausola1;
	}

	public void setClausola1(String clausola1) {
		this.clausola1 = clausola1;
	}

	public String getClausola2() {
		return clausola2;
	}

	public void setClausola2(String clausola2) {
		this.clausola2 = clausola2;
	}

	public String getPlatformUid() {
		return platformUid;
	}

	public void setPlatformUid(String platformUid) {
		this.platformUid = platformUid;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer[] getConsents() {
		return consents;
	}

	public void setConsents(Integer[] consents) {
		this.consents = consents;
	}

	public String getConsentsIP() {
		return consentsIP;
	}

	public void setConsentsIP(String consentsIP) {
		this.consentsIP = consentsIP;
	}

	public Boolean getSendActivationEmail() {
		return sendActivationEmail;
	}

	public void setSendActivationEmail(Boolean sendActivationEmail) {
		this.sendActivationEmail = sendActivationEmail;
	}
	
}
