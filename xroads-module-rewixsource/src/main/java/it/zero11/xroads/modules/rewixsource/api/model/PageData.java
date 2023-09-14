package it.zero11.xroads.modules.rewixsource.api.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@SuppressWarnings("rawtypes")
@XmlRootElement(name = "page")
@XmlSeeAlso({ RewixProduct.class, QueryTag.class, XOrder.class, XString.class })
public class PageData implements Serializable {
	public static int defaultPageSize = 10;

	private static final long serialVersionUID = 1L;

	protected int currentPage;
	protected List<RewixProduct> items;
	protected int pageSize;
	protected String query;
	protected List tags;
	protected int totalNumberOfElements;
	protected int totalPages;
	protected String lastUpdate;	
	
	@XmlAttribute
	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@XmlAttribute
	public int getCurrentPage() {
		return currentPage;
	}

	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	public List<RewixProduct> getItems() {
		return items;
	}

	@XmlAttribute
	public int getPageSize() {
		return pageSize;
	}

	public String getQuery() {
		return query;
	}

	@XmlElementWrapper(name = "tags")
	@XmlElement(name = "tag")
	public List getTags() {
		return tags;
	}

	@XmlAttribute
	public int getTotalNumberOfElements() {
		return totalNumberOfElements;
	}

	@XmlAttribute
	public int getTotalPages() {
		return totalPages;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void setItems(List<RewixProduct> items) {
		this.items = items;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setTags(List tags) {
		this.tags = tags;
	}

	public void setTotalNumberOfElements(int totalNumberOfElements) {
		this.totalNumberOfElements = totalNumberOfElements;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	
}