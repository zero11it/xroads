package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import com.sun.xml.txw2.annotation.XmlAttribute;

@XmlRootElement(name="paymentTermGenerator")
public class PaymentTermGenerator implements Serializable{
	private static final long serialVersionUID = 1L;

	private BigDecimal percentage;

	private Integer amount;
	
	private boolean amountIsDays;

	private boolean endOfMonth;

	private Integer decimalPlaces;

	@XmlAttribute
	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	@XmlAttribute
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	@XmlAttribute
	public boolean isAmountIsDays() {
		return amountIsDays;
	}

	public void setAmountIsDays(boolean amountIsDays) {
		this.amountIsDays = amountIsDays;
	}

	@XmlAttribute
	public boolean isEndOfMonth() {
		return endOfMonth;
	}

	public void setEndOfMonth(boolean endOfMonth) {
		this.endOfMonth = endOfMonth;
	}
	
	@XmlAttribute
	public Integer getDecimalPlaces() {
		return decimalPlaces;
	}

	public void setDecimalPlaces(Integer decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}
}