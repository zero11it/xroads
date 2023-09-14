package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "querytag")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class QueryTag implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name, value;
	
	
	public QueryTag(){}
	

	
	public QueryTag(int id, String value) {
		super();
		this.id = id;
		this.value = value;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	
	
	
}
