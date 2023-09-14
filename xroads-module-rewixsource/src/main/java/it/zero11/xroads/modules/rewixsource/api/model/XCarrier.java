package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="carrier")
public class XCarrier implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private BigDecimal cost;
	
	public XCarrier() {}
	
	public XCarrier(Integer id, String name, BigDecimal cost) {
		this.id = id;
		this.name = name;
		this.cost = cost;
	}

	@XmlAttribute
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	
}
