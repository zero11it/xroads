package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="userTradeAgent")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserTradeAgentBean {
	@XmlAttribute
	private String username;
	
	@XmlAttribute
	private String tradeAgentUsername;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getTradeAgentUsername() {
		return tradeAgentUsername;
	}
	
	public void setTradeAgentUsername(String tradeAgentUsername) {
		this.tradeAgentUsername = tradeAgentUsername;
	}
}
