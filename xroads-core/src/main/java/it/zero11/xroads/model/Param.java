package it.zero11.xroads.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "param", indexes={@Index(unique=true, columnList="name")})
public class Param implements Serializable {

	private static final long serialVersionUID = 1L;
	 	 
	@Id
	@Column(columnDefinition = "bigserial")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String name;
	
	@Column(name="value", columnDefinition="text")
	private String value;
	
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
