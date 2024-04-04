package it.zero11.xroads.ui.component;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;

import com.vaadin.flow.component.combobox.ComboBox;

public class TagTokenField extends AbstractTokenField<String, ComboBox<String>, TagTokenField> {
	private static final long serialVersionUID = 1L;
	
	private final String tagId;
	
	private Map<String, String> tags;
	
	public TagTokenField(String tagId, Map<String, String> tagValues, String addButtonLabel){
		super(false, addButtonLabel);
		this.tagId = tagId;
		this.setValues(tagValues);
	}

	public void setValues(Map<String, String> tagValues){
		tags = tagValues;
		setValue(new String[0]);
		addTokenComboBox.setItems();
		addTokenComboBox.setItems(new TreeSet<>(tags.values()));
	}

	public void setFilter(Map<String, List<String>> filterNode, Function<Map<String, List<String>>, Map<String, String>> filterFunciton){
		tags = filterFunciton.apply(filterNode);
		addTokenComboBox.setItems();
		addTokenComboBox.setItems(new TreeSet<>(tags.values()));
	}

	public String getTagId() {
		return tagId;
	}

	@Override
	protected String getItemCaption(String value) {
		String caption = tags.get(value);
		if (caption == null) {
			return value;
		}else {
			return caption;
		}
	}

	@Override
	protected Object getComboBoxItemCaptionPropertyId() {
		return null;
	}

	@Override
	protected boolean supportNewValue() {
		return false;
	}

	@Override
	protected ComboBox<String> buildAddTokenComboBox() {
		ComboBox<String> combobox = new ComboBox<>();
		return combobox;
	}

	@Override
	protected Class<String> getTokenClass() {
		return String.class;
	}
}