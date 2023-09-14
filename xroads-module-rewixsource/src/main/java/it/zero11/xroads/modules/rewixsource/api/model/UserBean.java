package it.zero11.xroads.modules.rewixsource.api.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserBean {
	@XmlAttribute
	private List<String> tags;

	@XmlAttribute
	private String username;
	
	@XmlAttribute
	private String email;
	
	@XmlAttribute
	private boolean ignoreCartRules;
	
	@XmlAttribute
	private Integer autoConfirmPaymentTermId;
	
	@XmlAttribute
	private boolean ignoreRestrinctions;
	
	@XmlAttribute
	private BigDecimal permanentDiscount;
		
	@XmlAttribute
	private String localeCode;

	@XmlAttribute
	private String tradeAgentUsername;
	
	@XmlAttribute
	private Integer status;

	@XmlAttribute
	private Date validationDate;

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getValidationDate() {
		return validationDate;
	}

	public void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}

	public boolean isIgnoreCartRules() {
		return ignoreCartRules;
	}

	public void setIgnoreCartRules(boolean ignoreCartRules) {
		this.ignoreCartRules = ignoreCartRules;
	}

	public Integer getAutoConfirmPaymentTermId() {
		return autoConfirmPaymentTermId;
	}

	public void setAutoConfirmPaymentTermId(Integer autoConfirmPaymentTermId) {
		this.autoConfirmPaymentTermId = autoConfirmPaymentTermId;
	}

	public boolean isIgnoreRestrinctions() {
		return ignoreRestrinctions;
	}

	public void setIgnoreRestrinctions(boolean ignoreRestrinctions) {
		this.ignoreRestrinctions = ignoreRestrinctions;
	}

	public BigDecimal getPermanentDiscount() {
		return permanentDiscount;
	}

	public void setPermanentDiscount(BigDecimal permanentDiscount) {
		this.permanentDiscount = permanentDiscount;
	}

	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}

	public String getTradeAgentUsername() {
		return tradeAgentUsername;
	}

	public void setTradeAgentUsername(String tradeAgentUsername) {
		this.tradeAgentUsername = tradeAgentUsername;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
