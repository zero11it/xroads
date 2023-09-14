package it.zero11.xroads.ui.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

public class TagMappingComp extends Composite<FormLayout> {

	private static final long serialVersionUID = 1L;
	private ComboBox<String> sourceValuesTag;
	private TextField sourceValueMappingTag;
	private TextField fixedValueMappingTag;
	private Button saveButton;
	private String tagetTagKey;
	private boolean enabled;
	
	public TagMappingComp() {
		super();
		this.sourceValuesTag = new ComboBox<String>();
		this.sourceValueMappingTag = new TextField();
		this.fixedValueMappingTag = new TextField();
		this.saveButton = new Button();
		this.enabled = false;
		saveButton.getStyle().set("align-self", "flex-end");
	}

	public TagMappingComp(ComboBox<String> sourceValuesTag, TextField sourceValueMappingTag,
			TextField fixedValueMappingTag, Button saveButton) {
		super();
		this.sourceValuesTag = sourceValuesTag;
		this.sourceValueMappingTag = sourceValueMappingTag;
		this.fixedValueMappingTag = fixedValueMappingTag;
		this.saveButton = saveButton;		
	}

	public ComboBox<String> getSourceValuesTag() {
		return sourceValuesTag;
	}

	public void setSourceValuesTag(ComboBox<String> sourceValuesTag) {
		this.sourceValuesTag = sourceValuesTag;
	}

	public TextField getSourceValueMappingTag() {
		return sourceValueMappingTag;
	}

	public void setSourceValueMappingTag(TextField sourceValueMappingTag) {
		this.sourceValueMappingTag = sourceValueMappingTag;
	}

	public TextField getFixedValueMappingTag() {
		return fixedValueMappingTag;
	}

	public void setFixedValueMappingTag(TextField fixedValueMappingTag) {
		this.fixedValueMappingTag = fixedValueMappingTag;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(Button saveButton) {
		this.saveButton = saveButton;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTagetTagKey() {
		return tagetTagKey;
	}

	public void setTagetTagKey(String tagetTagKey) {
		this.tagetTagKey = tagetTagKey;
	}

	public void populateComponent() {
		getContent().add(sourceValuesTag);
		getContent().add(sourceValueMappingTag);
		getContent().add(fixedValueMappingTag);
		getContent().add(saveButton);
		setEnabled(true);
	}
	
	public void dePopulateComponent() {
		getContent().remove(sourceValuesTag);
		getContent().remove(sourceValueMappingTag);
		getContent().remove(fixedValueMappingTag);
		getContent().remove(saveButton);
		setEnabled(false);
	}
	
}
