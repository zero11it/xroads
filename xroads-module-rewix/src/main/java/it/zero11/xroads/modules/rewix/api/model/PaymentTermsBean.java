package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "paymentTerms")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentTermsBean {
    @XmlElement(name = "paymentTerm")
	private List<PaymentTermBean> paymentTerms;

	public List<PaymentTermBean> getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(List<PaymentTermBean> paymentTerms) {
		this.paymentTerms = paymentTerms;
	}
}
