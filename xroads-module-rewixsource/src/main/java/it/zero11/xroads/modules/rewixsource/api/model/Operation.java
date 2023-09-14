package it.zero11.xroads.modules.rewixsource.api.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="operation")
public class Operation {
	
	public static final String SET = "set"; 
	public static final String LOCK = "lock"; 
	public static final String UNLOCK = "unlock"; 
	
	@XmlAttribute
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
