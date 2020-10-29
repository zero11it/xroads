package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupBean {
	@XmlAttribute
	private Integer id;

	@XmlAttribute
	private String name;

	@XmlAttribute
	@XmlJavaTypeAdapter(value=JAXBStringURLEncodedToIntegerArray.class)
	private Integer[] incompatibleWith;

	@XmlAttribute
	private String platformUid;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer[] getIncompatibleWith() {
		return incompatibleWith;
	}

	public void setIncompatibleWith(Integer[] incompatibleWith) {
		this.incompatibleWith = incompatibleWith;
	}

	public String getPlatformUid() {
		return platformUid;
	}

	public void setPlatformUid(String platformUid) {
		this.platformUid = platformUid;
	}
}
