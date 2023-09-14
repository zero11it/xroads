package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class OrderStatusUpdate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<OrderStatusInfo> orders;
	
	
	
	public OrderStatusUpdate() {
	}
	public OrderStatusUpdate(List<OrderStatusInfo> orders) {
		this.orders = orders;
	}
	
	@XmlElementWrapper(name="order_list")
	@XmlElement(name="order")
	public List<OrderStatusInfo> getOrders() {
		return orders;
	}


	public void setOrders(List<OrderStatusInfo> orders) {
		this.orders = orders;
	}
	
	
	public static class OrderStatusInfo implements Serializable{

		public static final int ORDER_PENDING=0;
		public static final int ORDER_MONEYWAITING=1;
		public static final int ORDER_TODISPATCH=2;
		public static final int ORDER_DISPATCHED=3;
		public static final int ORDER_BOOKED = 5;
		public static final int ORDER_CANCELED=2000;
		public static final int ORDER_VERIFYFAILED=2002;
		public static final int ORDER_WORKING_ON=3001;
		public static final int ORDER_READY=3002;
		public static final int ORDER_DROPSHIPPING=5003;
		
		private static final long serialVersionUID = 1L;
		
		private Integer order_id;
		private String 	ext_ref;
		private Integer status, substatus;
		private String 	last_update;
		private String 	tracking_code;
		private String 	tracking_url;
		private String 	carrier_name;
		

		private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
		
		
		public Date getParsedDate() throws ParseException {
			if (last_update!=null)
				return df.parse(last_update);
			else
				return null;
		}
		public void setParsedDate(Date date) {
			if (date!=null)
				this.last_update = df.format(date);
		}



		public Integer getOrder_id() {
			return order_id;
		}



		public void setOrder_id(Integer orderId) {
			order_id = orderId;
		}



		public String getExt_ref() {
			return ext_ref;
		}



		public void setExt_ref(String extRef) {
			ext_ref = extRef;
		}



		public Integer getStatus() {
			return status;
		}



		public void setStatus(Integer status) {
			this.status = status;
		}



		public Integer getSubstatus() {
			return substatus;
		}



		public void setSubstatus(Integer substatus) {
			this.substatus = substatus;
		}



		public String getLast_update() {
			return last_update;
		}



		public void setLast_update(String lastUpdate) {
			last_update = lastUpdate;
		}



		public String getTracking_code() {
			return tracking_code;
		}



		public void setTracking_code(String trackingCode) {
			tracking_code = trackingCode;
		}



		public String getTracking_url() {
			return tracking_url;
		}



		public void setTracking_url(String trackingUrl) {
			tracking_url = trackingUrl;
		}
		public String getCarrier_name() {
			return carrier_name;
		}
		public void setCarrier_name(String carrierName) {
			carrier_name = carrierName;
		}
		
	}
	
}
