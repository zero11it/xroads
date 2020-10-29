package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class UpdateDropshippingOrderStatusBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer orderId;
	private String extRef;
	private Integer substatus;
	private String trackingCode;
	private String trackingUrl;
	private String carrierName;
	
	public Integer getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	public String getExtRef() {
		return extRef;
	}
	
	public void setExtRef(String extRef) {
		this.extRef = extRef;
	}
	
	public Integer getSubstatus() {
		return substatus;
	}
	
	public void setSubstatus(Integer substatus) {
		this.substatus = substatus;
	}
	
	public String getTrackingCode() {
		return trackingCode;
	}
	
	public void setTrackingCode(String trackingCode) {
		this.trackingCode = trackingCode;
	}
	
	public String getTrackingUrl() {
		return trackingUrl;
	}
	
	public void setTrackingUrl(String trackingUrl) {
		this.trackingUrl = trackingUrl;
	}
	
	public String getCarrierName() {
		return carrierName;
	}
	
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
}
