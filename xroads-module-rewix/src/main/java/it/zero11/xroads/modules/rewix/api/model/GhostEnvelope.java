package it.zero11.xroads.modules.rewix.api.model;

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
public class GhostEnvelope {

	private Integer order_id;

	private List<GhostOrder> order_list;
	
	
	public GhostEnvelope() {}

	
	
	
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer orderId) {
		order_id = orderId;
	}



	@XmlElementWrapper(name="order_list")
	@XmlElement(name="order")
	public List<GhostOrder> getOrder_list() {
		return order_list;
	}
	public void setOrder_list(List<GhostOrder> orderList) {
		order_list = orderList;
	}

	
	public static class GhostOrder{
		private String key;
		private Integer carrierId;
		private String date;
		private static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
		private RecipientDetails recipient_details;
		private List<Item> item_list;
		
		public GhostOrder(String key, String date) {
			this.key = key;
			this.date = date;
		}
		
		public GhostOrder(String key, Date date) {
			this.key = key;
			this.date = df.format(date);
		}

		public GhostOrder(){}

		public String getKey() {
			return key;
		}
		
		public void setKey(String key) {
			this.key = key;
		}

		
		public Integer getCarrierId() {
			return carrierId;
		}

		public void setCarrierId(Integer carrierId) {
			this.carrierId = carrierId;
		}

		public RecipientDetails getRecipient_details() {
			return recipient_details;
		}
		
		public void setRecipient_details(RecipientDetails recipientDetails) {
			recipient_details = recipientDetails;
		}
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}


		public Date getParsedDate() throws ParseException {
			if (date!=null)
				return df.parse(date);
			else
				return null;
		}
		public void setParsedDate(Date date) {
			this.date = df.format(date);
		}

		
		

		@XmlElementWrapper(name="item_list")
		@XmlElement(name="item")
		public List<Item> getItem_list() {
			return item_list;
		}
		public void setItem_list(List<Item> itemList) {
			item_list = itemList;
		}
		
		
		
		public static class RecipientDetails{
			private String recipient, careof, cfpiva, customer_key, notes;
			private Address address;
			private Phone phone;
			
			public RecipientDetails(){}
			
			public String getRecipient() {
				return recipient;
			}

			public void setRecipient(String recipient) {
				this.recipient = recipient;
			}

			public String getCareof() {
				return careof;
			}

			public void setCareof(String careof) {
				this.careof = careof;
			}

			public String getCustomer_key() {
				return customer_key;
			}

			public void setCustomer_key(String customerKey) {
				customer_key = customerKey;
			}

			public Address getAddress() {
				return address;
			}

			public void setAddress(Address address) {
				this.address = address;
			}

			public Phone getPhone() {
				return phone;
			}

			public void setPhone(Phone phone) {
				this.phone = phone;
			}

			public String getCfpiva() {
				return cfpiva;
			}

			public void setCfpiva(String cfpiva) {
				this.cfpiva = cfpiva;
			}

			public static class Address{
				private String street_type, street_name, address_number, zip;
				private String city, province, countrycode;
				
				public Address(){}
				
				
				public String getStreet_type() {
					return street_type;
				}
				public void setStreet_type(String streetType) {
					street_type = streetType;
				}
				public String getStreet_name() {
					return street_name;
				}
				public void setStreet_name(String streetName) {
					street_name = streetName;
				}
				public String getAddress_number() {
					return address_number;
				}
				public void setAddress_number(String addressNumber) {
					address_number = addressNumber;
				}
				public String getZip() {
					return zip;
				}
				public void setZip(String zip) {
					this.zip = zip;
				}
				public String getCity() {
					return city;
				}
				public void setCity(String city) {
					this.city = city;
				}
				public String getProvince() {
					return province;
				}
				public void setProvince(String province) {
					this.province = province;
				}
				public String getCountrycode() {
					return countrycode;
				}
				public void setCountrycode(String countrycode) {
					this.countrycode = countrycode;
				}
				
				

			}//end of address clas
			
			public static class Phone{
				private String prefix, number;

				public Phone(){}
				
				public Phone(String prefix, String number){
					this.prefix = prefix;
					this.number = number;
				}
				
				public String getPrefix() {
					return prefix;
				}

				public void setPrefix(String prefix) {
					this.prefix = prefix;
				}

				public String getNumber() {
					return number;
				}

				public void setNumber(String number) {
					this.number = number;
				}
			}
			
			public void fillFromOrder(Order order, String countrycode){
				this.customer_key 	= order.getCustomer(); 
				this.careof 		= order.getCareof();
				this.phone			= new Phone(order.getCel_prefix(), order.getCel());
				this.recipient 		= order.getDest();
				this.notes 			= order.getNotes();
				Address address = new Address();
				address.setStreet_type		(order.getAddress_type());
				address.setStreet_name		(order.getStreet());
				address.setAddress_number	(order.getNumber());
				address.setZip				(order.getCap());
				address.setCity				(order.getCity());
				address.setProvince			(order.getProv());
				address.setCountrycode		(countrycode);
				this.address 		= address; 
				
			}

			public String getNotes() {
				return notes;
			}

			public void setNotes(String notes) {
				this.notes = notes;
			}
			
			
		}//End of class Recipient Deatails


		public static class Item{
			private Integer stock_id, quantity;

			public Item(){}
			
			public Item(Integer stock_id, Integer quantity){
				this.stock_id = stock_id;
				this.quantity = quantity;
			}
			
			public Integer getStock_id() {
				return stock_id;
			}

			public void setStock_id(Integer stockId) {
				stock_id = stockId;
			}

			public Integer getQuantity() {
				return quantity;
			}

			public void setQuantity(Integer quantity) {
				this.quantity = quantity;
			}
		}

	}// end of class Order

	
}
