package it.zero11.xroads.modules.rewix.api.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "orderFilter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class OrderFilterBean {
	private String platformUid;
	private List<Integer> orderStatuses;
	private List<Integer> orderStatusesExcluded;
	private List<Integer> orderSubstatuses;
	private Boolean orderSubstatusNull;
	private List<Integer> shippingCountry = null;
	private List<Integer> shippingCountryExcluded = null;
	private List<Integer> stockModel = null;
	private List<Integer> stockModelExcluded = null;
	private Date lastUpdateFrom;
	private Date lastUpdateTo;
	private Date dispatchDateFrom;
	private Date dispatchDateTo;
	private BigDecimal orderItemTaxableFrom;
	private BigDecimal orderItemTaxableTo;
	private String username;
	
	private String sort;
	private String sortDir;
	
	private int firstResult;
	private int maxResult;

	public String getPlatformUid() {
		return platformUid;
	}

	public void setPlatformUid(String platformUid) {
		this.platformUid = platformUid;
	}

	public List<Integer> getOrderStatuses() {
		return orderStatuses;
	}

	public void setOrderStatuses(List<Integer> orderStatuses) {
		this.orderStatuses = orderStatuses;
	}

	public List<Integer> getOrderStatusesExcluded() {
		return orderStatusesExcluded;
	}

	public void setOrderStatusesExcluded(List<Integer> orderStatusesExcluded) {
		this.orderStatusesExcluded = orderStatusesExcluded;
	}

	public List<Integer> getShippingCountry() {
		return shippingCountry;
	}

	public void setShippingCountry(List<Integer> shippingCountry) {
		this.shippingCountry = shippingCountry;
	}

	public List<Integer> getShippingCountryExcluded() {
		return shippingCountryExcluded;
	}

	public void setShippingCountryExcluded(List<Integer> shippingCountryExcluded) {
		this.shippingCountryExcluded = shippingCountryExcluded;
	}

	public List<Integer> getStockModel() {
		return stockModel;
	}

	public void setStockModel(List<Integer> stockModel) {
		this.stockModel = stockModel;
	}

	public List<Integer> getStockModelExcluded() {
		return stockModelExcluded;
	}

	public void setStockModelExcluded(List<Integer> stockModelExcluded) {
		this.stockModelExcluded = stockModelExcluded;
	}

	public Date getLastUpdateFrom() {
		return lastUpdateFrom;
	}

	public void setLastUpdateFrom(Date lastUpdateFrom) {
		this.lastUpdateFrom = lastUpdateFrom;
	}

	public Date getLastUpdateTo() {
		return lastUpdateTo;
	}

	public void setLastUpdateTo(Date lastUpdateTo) {
		this.lastUpdateTo = lastUpdateTo;
	}

	public Date getDispatchDateFrom() {
		return dispatchDateFrom;
	}

	public void setDispatchDateFrom(Date dispatchDateFrom) {
		this.dispatchDateFrom = dispatchDateFrom;
	}

	public Date getDispatchDateTo() {
		return dispatchDateTo;
	}

	public void setDispatchDateTo(Date dispatchDateTo) {
		this.dispatchDateTo = dispatchDateTo;
	}

	public BigDecimal getOrderItemTaxableFrom() {
		return orderItemTaxableFrom;
	}

	public void setOrderItemTaxableFrom(BigDecimal orderItemTaxableFrom) {
		this.orderItemTaxableFrom = orderItemTaxableFrom;
	}

	public BigDecimal getOrderItemTaxableTo() {
		return orderItemTaxableTo;
	}

	public void setOrderItemTaxableTo(BigDecimal orderItemTaxableTo) {
		this.orderItemTaxableTo = orderItemTaxableTo;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public List<Integer> getOrderSubstatuses() {
		return orderSubstatuses;
	}

	public void setOrderSubstatuses(List<Integer> orderSubstatuses) {
		this.orderSubstatuses = orderSubstatuses;
	}

	public Boolean getOrderSubstatusNull() {
		return orderSubstatusNull;
	}

	public void setOrderSubstatusNull(Boolean orderSubstatusNull) {
		this.orderSubstatusNull = orderSubstatusNull;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}
}
