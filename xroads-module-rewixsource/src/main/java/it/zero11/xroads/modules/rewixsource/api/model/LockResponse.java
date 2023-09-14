package it.zero11.xroads.modules.rewixsource.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class LockResponse {

	private Integer order_id;
	private List<LockModel> models;
	
	
	public LockResponse() {}
	public LockResponse(Integer order_id, List<LockModel> models ) {
		this.order_id = order_id;
		this.models = models;
	}


	//@XmlElementWrapper(name="order_list")
	@XmlElement(name="model")
	public List<LockModel> getModels() {
		return models;
	}

	public void setModels(List<LockModel> operations) {
		this.models = operations;
	}	
	
	@XmlAttribute(name="order_id")
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer orderId) {
		order_id = orderId;
	}

}
