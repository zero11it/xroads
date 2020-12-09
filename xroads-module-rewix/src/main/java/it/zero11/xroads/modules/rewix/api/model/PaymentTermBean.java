package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "paymentTerm")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentTermBean {
	@XmlAttribute
	private Integer id;

	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private boolean paid;
	
	@XmlAttribute
	private boolean generateInvoice;
	
	private PaymentTermsGenerator paymentTermsGenerator;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public boolean isGenerateInvoice() {
		return generateInvoice;
	}

	public void setGenerateInvoice(boolean generateInvoice) {
		this.generateInvoice = generateInvoice;
	}

	public PaymentTermsGenerator getPaymentTermsGenerator() {
		return paymentTermsGenerator;
	}

	public void setPaymentTermsGenerator(PaymentTermsGenerator paymentTermsGenerator) {
		this.paymentTermsGenerator = paymentTermsGenerator;
	}
}
