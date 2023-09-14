package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="temporarysales")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class TemporarySales implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<TemporarySale>enabled;
	private List<TemporarySale>active;

	
	@XmlElementWrapper(name="enabled")
	@XmlElement(name="sale")
	public List<TemporarySale> getEnabled() {
		return enabled;
	}

	public void setEnabled(List<TemporarySale> enabled) {
		this.enabled = enabled;
	}

	@XmlElementWrapper(name="active")
	@XmlElement(name="sale")
	public List<TemporarySale> getActive() {
		return active;
	}

	public void setActive(List<TemporarySale> active) {
		this.active = active;
	}

	
	/*
	private List<Integer> sales;

	@XmlElementWrapper(name="active")
	@XmlElement(name="active")
	public List<Integer> getSales() {
		return sales;
	}

	public void setSales(List<Integer> sales) {
		this.sales = sales;
	}
	*/
}
