package it.zero11.xroads.modules.rewix.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userConsents")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserConsentsBean {
	@XmlElement(name = "userConsents")
	private List<UserConsentBean> userConsents;

	public List<UserConsentBean> getUserConsents() {
		return userConsents;
	}

	public void setUserConsents(List<UserConsentBean> userConsents) {
		this.userConsents = userConsents;
	}
}
