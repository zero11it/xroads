package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement(name = "orderAttachment")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderAttachmentBean {

	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String url;
	
	@XmlAttribute
	@XmlSchemaType(name = "base64Binary")
	@XmlJavaTypeAdapter(JAXBStringBase64ToByteArray.class)
	private byte[] data;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
