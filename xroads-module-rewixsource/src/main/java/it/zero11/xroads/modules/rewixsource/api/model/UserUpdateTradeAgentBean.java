package it.zero11.xroads.modules.rewixsource.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserUpdateTradeAgentBean {

	@XmlAttribute
	private String tradeAgentUsername;
	
	public UserUpdateTradeAgentBean() {
		
	}

	public String getTradeAgentUsername() {
		return tradeAgentUsername;
	}

	public void setTradeAgentUsername(String tradeAgentUsername) {
		this.tradeAgentUsername = tradeAgentUsername;
	}	
}
