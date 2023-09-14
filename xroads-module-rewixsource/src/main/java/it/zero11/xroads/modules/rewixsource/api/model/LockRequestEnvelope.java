package it.zero11.xroads.modules.rewixsource.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class LockRequestEnvelope {


	private List<LockRequest> operations;
	
	
	public LockRequestEnvelope() {}
	public LockRequestEnvelope( List<LockRequest> operations ) {
		this.operations = operations;
	}


	//@XmlElementWrapper(name="order_list")
	@XmlElement(name="operation")
	public List<LockRequest> getOperations() {
		return operations;
	}

	public void setOperations(List<LockRequest> operations) {
		this.operations = operations;
	}
		
	
	
	public static class LockRequest{
		public static final String LOCK	= "lock";
		public static final String UNLOCK = "unlock";
		public static final String SET	= "set";
		public static final String ENSURE	= "ensure";
		
		
		private String type;
		private List<LRModel> models_list;
		
		public LockRequest(){}
		public LockRequest(String type, List<LRModel> models_list){
			this.type = type;
			this.models_list = models_list;
		}
		
		
		public static class LRModel{
			private String stock_id;
			private Integer quantity;
			
			public LRModel(){}
			public LRModel(String stock_id, Integer quantity){
				this.stock_id = stock_id;
				this.quantity = quantity;
			}
			@XmlAttribute(name="stock_id")
			public String getStock_id() {
				return stock_id;
			}
			public void setStock_id(String stockId) {
				stock_id = stockId;
			}
			@XmlAttribute(name="quantity")
			public Integer getQuantity() {
				return quantity;
			}
			public void setQuantity(Integer quantity) {
				this.quantity = quantity;
			}
			
		}
		 
		@XmlAttribute(name="type")
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
		@XmlElement(name="model")
		public List<LRModel> getModels_list() {
			return models_list;
		}
		public void setModels_list(List<LRModel> modelsList) {
			models_list = modelsList;
		}




		
	}// end of class LockRequest
	

}
