package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(PaymentTermGenerator.class)
@XmlRootElement(name="paymentTermsGenerator")
public class PaymentTermsGenerator implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected List<PaymentTermGenerator> paymentTermGenerators;

	@XmlAnyElement(lax = true)
	public List<PaymentTermGenerator> getPaymentTermGenerators() {
		return paymentTermGenerators;
	}

	public void setPaymentTermGenerators(List<PaymentTermGenerator> paymentTermGenerators) {
		this.paymentTermGenerators = paymentTermGenerators;
	}
}