package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "invoiceFilter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class InvoiceFilterBean {
	private Integer lastFatturaId;
	
	public Integer getLastFatturaId() {
		return lastFatturaId;
	}

	public void setLastFatturaId(Integer lastFatturaId) {
		this.lastFatturaId = lastFatturaId;
	}
}
