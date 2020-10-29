package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "orderAttachments")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderAttachmentsBean {
	@XmlAttribute
	private Integer orderId;
		
    @XmlElement(name = "attachment")
	private List<OrderAttachmentBean> orderAttachments;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public List<OrderAttachmentBean> getOrderAttachments() {
		return orderAttachments;
	}

	public void setOrderAttachments(List<OrderAttachmentBean> orderAttachments) {
		this.orderAttachments = orderAttachments;
	}

}
