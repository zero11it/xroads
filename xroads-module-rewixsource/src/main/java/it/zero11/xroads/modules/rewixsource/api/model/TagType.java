package it.zero11.xroads.modules.rewixsource.api.model;

public enum TagType{
	TAG_BRAND(1, "Brand"),	
	TAG_CATEGORY(4, "Category"),
	TAG_SUBCATEGORY(5, "Subcategory"),	
	TAG_GENDER(26, "Gender"),	
	TAG_SEASON(11, "Season"),	
	TAG_COLOR(13, "Color"),
	TAG_PRODUCTNAME(30, "Product Name");

	public final int tagId;
	public final String tagIdStr;
	public final String tagName;
	
	private TagType(int tagId, String tagName){
		this.tagId = tagId;
		this.tagIdStr = Integer.toString(tagId);
		this.tagName = tagName;
	}
}