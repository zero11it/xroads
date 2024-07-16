package it.zero11.xroads.modules.rewix.api.model;

import java.io.Serializable;

public class Address  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final static String TYPE_INVOICE 	= "invoice";
	public final static String TYPE_DISPATCH 	= "dispatch";
	
	private Integer id;
	
	private String username;
	
	private String type;
	
	private Integer country_id;
	
	private String address_type;
	private String street, number;

	private String city;

	private String prov;

	private String cap;

	private String addressee;
	
	private String careof;

	private String cel_prefix, cel;
	
	private String cfpiva;
	

	public String getAddress() {
		String _tmp = String.format("%s %s", (address_type!=null ? address_type :""),
				(street!=null ? street :""));
		return  _tmp + (number!=null ? " ,"+number :"");
	}
	
	public String getFullAddress() {
		String myprov = (prov == null) ? "" : " (" + prov + ")";
		String mycity = (city == null) ? "" : " " +city;
		return getAddress() + mycity + myprov;
	}



	

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProv() {
		return prov;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public String getFullname() {
		return String.format("%s", addressee);
	}
	
	public String getFullMobile() {
		return String.format("%s%s", cel_prefix !=null ? cel_prefix : "", 
				 cel !=null ? cel : "");
	}
	


	public String getCel() {
		return cel;
	}

	public void setCel(String cel) {
		this.cel = cel;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAddress_type() {
		return address_type;
	}

	public void setAddress_type(String address_type) {
		this.address_type = address_type;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCel_prefix() {
		return cel_prefix;
	}

	public void setCel_prefix(String cel_prefix) {
		this.cel_prefix = cel_prefix;
	}

		
	public void fillIn(Order a){
		if (address_type==null || address_type.trim().length()==0)
			address_type = a.getAddress_type();
		
		if (street==null || street.trim().length()==0)
			street = a.getStreet();
		
		if (number==null || number.trim().length()==0)
			number = a.getNumber();
		
		if (cap==null || cap.trim().length()==0)
			cap = a.getCap();
		
		if (city==null || city.trim().length()==0)
			city = a.getCity();
		
		if (prov==null || prov.trim().length()==0)
			prov = a.getProv();
		
		if (cel_prefix==null || cel_prefix.trim().length()==0)
			cel_prefix = a.getCel_prefix();
		
		if (cel==null || cel.trim().length()==0)
			cel = a.getCel();
		
		if (country_id==null || country_id<=0)
			country_id = a.getCountry_id();
		
	}

	public Integer getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Integer country_id) {
		this.country_id = country_id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddressee() {
		return addressee;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public String getCareof() {
		return careof;
	}

	public void setCareof(String careof) {
		this.careof = careof;
	}

	public String getCfpiva() {
		return cfpiva;
	}

	public void setCfpiva(String cfpiva) {
		this.cfpiva = cfpiva;
	}

	
}
