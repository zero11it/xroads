package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "sale")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class TemporarySale implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String sale, platform_uid, bannerUrl;
	
	private Date start_date, end_date;

	private Integer status;
	
	private Float additional_discount;
	
	private Integer discount_type, discount_visibility, priority;
	
	private boolean autoonline, autooffline, permanent, mixedcart;
	
	private String paycaptureon;
	
	private Integer logistics_id;

	
	public Integer getLogistics_id() {
		return logistics_id;
	}
	public void setLogistics_id(Integer logistics_id) {
		this.logistics_id = logistics_id;
	}
	public static final Integer STATUS_WORKING  = 0;
	public static final Integer STATUS_ENABLED  = 1;
	public static final Integer STATUS_ACTIVE   = 2;
	public static final Integer STATUS_PAUSED   = 3;
	public static final Integer STATUS_EXPIRED  = 4;
	public static final Integer STATUS_ARCHIVED = 5;
	
	
	public static final Integer DISCOUNT_TYPE_PERCENTAGE = 0;
	public static final Integer DISCOUNT_TYPE_FIXED 	 = 1;
	public static final Integer DISCOUNT_TYPE_FINALVALUE = 2;
	
	public static final Integer DISCOUNT_VISIBILITY_LOCAL  = 0;
	public static final Integer DISCOUNT_VISIBILITY_GLOBAL = 1;
	
	
	public TemporarySale(){}
	
	public TemporarySale(String sale, Date start, Date end, String platform_uid, Integer discountType, Float additionalDiscount){
		this.sale = sale;
		this.start_date = start;
		this.end_date = end;
		this.discount_type = discountType;
		this.additional_discount = additionalDiscount;
		this.platform_uid = platform_uid;
		this.status = STATUS_WORKING;
	}
	
	
	/** GETTERs AND SETTERs*/
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSale() {
		return sale;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date startDate) {
		start_date = startDate;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date endDate) {
		end_date = endDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Float getAdditional_discount() {
		return additional_discount;
	}

	public void setAdditional_discount(Float additionalDiscount) {
		additional_discount = additionalDiscount;
	}


	public String getPlatform_uid() {
		return platform_uid;
	}


	public void setPlatform_uid(String platformUid) {
		platform_uid = platformUid;
	}
	public Integer getDiscount_type() {
		return discount_type;
	}
	public void setDiscount_type(Integer discountType) {
		discount_type = discountType;
	}
	public Integer getDiscount_visibility() {
		return discount_visibility;
	}
	public void setDiscount_visibility(Integer discountVisibility) {
		discount_visibility = discountVisibility;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getBannerUrl() {
		return bannerUrl;
	}
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}
	public boolean isAutoonline() {
		return autoonline;
	}
	public void setAutoonline(boolean autoonline) {
		this.autoonline = autoonline;
	}
	public boolean isAutooffline() {
		return autooffline;
	}
	public void setAutooffline(boolean autooffline) {
		this.autooffline = autooffline;
	}
	public boolean isPermanent() {
		return permanent;
	}
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
	public boolean isMixedcart() {
		return mixedcart;
	}
	public void setMixedcart(boolean mixedcart) {
		this.mixedcart = mixedcart;
	}
	public String getPaycaptureon() {
		return paycaptureon;
	}
	public void setPaycaptureon(String paycaptureon) {
		this.paycaptureon = paycaptureon;
	}
	
	
	
}
