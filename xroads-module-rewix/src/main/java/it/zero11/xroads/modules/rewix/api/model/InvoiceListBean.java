package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "invoiceList")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class InvoiceListBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<Integer> invoices;
	
	public InvoiceListBean() {
		
	}
	public InvoiceListBean(List<Fattura> invoices) {
		this.invoices = new ArrayList<>();
		if (invoices != null) {
			for (Fattura invoice : invoices) {
				this.invoices.add(invoice.getFattura_id());
			}
		}
	}
	
	@XmlElement(name="invoice")
	public List<Integer> getInvoices() {
		return invoices;
	}


	public void setInvoices(List<Integer> invoices) {
		this.invoices = invoices;
	}

}
