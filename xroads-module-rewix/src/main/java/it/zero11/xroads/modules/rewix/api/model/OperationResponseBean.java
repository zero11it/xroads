package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="response")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationResponseBean {

	@XmlAttribute(required=true)
	private Boolean status;
	
	@XmlAttribute
	private Integer code;
	
	@XmlAttribute
	private String message;
	
	@XmlElement(name = "id")
	private List<Integer> ids;

	public OperationResponseBean() {
		
	}
	
	public OperationResponseBean(Boolean status, Integer code, String message, List<Integer> ids) {
		super();
		this.status = status;
		this.code = code;
		this.message = message;
		this.ids = ids;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
}
