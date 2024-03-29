package it.zero11.xroads.utils.modules.core.model;

import java.io.Serializable;

import it.zero11.xroads.model.ModuleStatus;

public class WrapFilter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	ModuleStatus moduleStatus;
	String searchKey;
	
	public WrapFilter() {
		super();
	}
	public WrapFilter(ModuleStatus moduleStatus) {
		this.moduleStatus = moduleStatus;
	}
	public ModuleStatus getModuleStatus() {
		return moduleStatus;
	}
	public void setModuleStatus(ModuleStatus moduleStatus) {
		this.moduleStatus = moduleStatus;
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	
}
