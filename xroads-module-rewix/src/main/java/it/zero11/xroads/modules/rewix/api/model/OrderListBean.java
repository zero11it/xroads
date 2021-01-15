package it.zero11.xroads.modules.rewix.api.model;

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

@XmlRootElement(name = "orderList")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class OrderListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<OrderStatusInfo> orders;
	
	public OrderListBean() {
		
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

		private static final long serialVersionUID = 1L;
		
		private Integer order_id;
		private String customer_id;
		private String 	ext_ref;
		private Integer status, substatus;
		private String 	last_update;
		
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
		
		public String getCustomer_id() {
			return customer_id;
		}
		
		public void setCustomer_id(String customer_id) {
			this.customer_id = customer_id;
		}
	}

}
