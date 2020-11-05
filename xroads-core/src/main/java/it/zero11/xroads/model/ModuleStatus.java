package it.zero11.xroads.model;

public enum ModuleStatus {
	SYNCED("Synced"),
	NOT_PROCESSED("Not processed"),
	DIFFERENT_VERSIONS("Different versions"),
	TO_SYNC("To sync"), //this = NOT_IMPORTED + REWIX_VERSION_DIFFER
	SYNC_ERRORS("Sync errors");
	
    private String module; 
  
    public String getName() 
    { 
        return this.module; 
    } 
  
    private ModuleStatus(String module) 
    { 
        this.module = module; 
    } 
}
