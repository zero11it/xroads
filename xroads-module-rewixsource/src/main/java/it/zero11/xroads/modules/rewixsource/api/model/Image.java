package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Image implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String url;
	private String name;
	private Integer[] modelIds;
	
	public Image(Integer id, String url, String name, Integer[] modelIds) {
		super();
		this.id = id;
		this.url = url;
		this.name = name;
		this.modelIds = modelIds;
	}

	public Image(){}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlJavaTypeAdapter(value=JAXBStringURLEncodedToIntegerArray.class)
	public Integer[] getModelIds() {
		return modelIds;
	}

	public void setModelIds(Integer[] modelIds) {
		this.modelIds = modelIds;
	}
	
	@Override
	public String toString() {
		return url;
	}
}
