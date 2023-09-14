package it.zero11.xroads.ui.view.setting;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.RewixAPI;
import it.zero11.xroads.modules.rewixsource.api.UnauthorizedException;
import it.zero11.xroads.modules.rewixsource.api.model.PageData;
import it.zero11.xroads.modules.rewixsource.api.model.ProductTag;
import it.zero11.xroads.modules.rewixsource.api.model.RewixProduct;
import it.zero11.xroads.modules.rewixsource.cron.RewixSourceProductCron;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.modules.rewixsource.utils.RewixTagUtils;
import it.zero11.xroads.ui.components.TagMappingComp;
import it.zero11.xroads.ui.i18n.RewixUITranslation;
import it.zero11.xroads.ui.utils.NotificationUtils;
import it.zero11.xroads.ui.utils.NotificationUtils.NotificationType;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;

public class TagMappingConfigurationSettings extends VerticalLayout{

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> tagsSupplierMap;
	private  Map<Integer, Set<String>> supplierTagValues;
	private Map<String, String> tagsTargetMap;
	private Map<String, ComboBox<String>> tagsSelect;
	private List<TagMappingComp> tagMappingComponentList;
	private RewixAPI rewixApi = null;
	private XRoadsRewixSourceModule xRoadsModule = null;
	private FormLayout tagsLayout = new FormLayout();
	private JSONObject configurationTagsJson;

	public TagMappingConfigurationSettings buildUI(XRoadsRewixSourceModule xRoadsModule, Consumer<Void> function) {
		this.xRoadsModule = xRoadsModule;
		rewixApi = new RewixAPI(xRoadsModule);
		supplierTagValues = getAllValuesOfSupplierTags();
		tagMappingComponentList = new ArrayList<TagMappingComp>();
		
		removeAll();
		
		setWidth("100%");
		setSpacing(true);
		
		H2 title=new H2(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_TITLE));
		add(title);
		setHorizontalComponentAlignment(Alignment.CENTER, title);
		Paragraph text = new Paragraph(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_INITIAL_NOTES));
		add(text);
		
		add(new H3(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_SUBTITLE)));
		
		
		try {
			tagsSupplierMap = RewixTagUtils.getTagsMap(rewixApi.getSupplierTags());
		} catch (NoSuchAlgorithmException | UnauthorizedException | IOException e) {
			NotificationUtils.show(NotificationType.ERROR, "Failed to retrieve supplier tags : " + e.getMessage());
		}	
		tagsSupplierMap.put("", "");

		tagsSelect = new HashMap<String, ComboBox<String>>();
		try {
			tagsTargetMap = RewixTagUtils.getTagsMap(rewixApi.getTargetTags());
		} catch (NoSuchAlgorithmException | UnauthorizedException | IOException e) {
			NotificationUtils.show(NotificationType.ERROR, "Failed to retrieve target tags : " + e.getMessage());
		}

		tagsTargetMap.forEach(
				(targetTagId, targetTagName) -> {
					String targetTagKey = targetTagName + "-" + targetTagId;
					ComboBox<String> tagsComboBox = new ComboBox<>();
					tagsComboBox.setItemLabelGenerator((lang) -> tagsSupplierMap.get(lang));
					tagsComboBox.setMinWidth("200px");
					tagsComboBox.setItems(tagsSupplierMap.keySet());
					tagsComboBox.setLabel(targetTagName);
					
					tagsComboBox.getListDataView().setSortComparator((k1, k2) -> {
						return tagsSupplierMap.get(k1).compareTo(tagsSupplierMap.get(k2));
					});
					
					// set source tag value if exist
					if(configurationTagsJson.has(targetTagKey) && configurationTagsJson.getJSONObject(targetTagKey).has("sourceTagId")) {
						tagsComboBox.setValue(configurationTagsJson.getJSONObject(targetTagKey).getString("sourceTagId"));
					}

					tagsSelect.put(targetTagKey, tagsComboBox);

					//set custom mapping component
					TagMappingComp tagMappingComp = new TagMappingComp();
					tagMappingComp.setTagetTagKey(targetTagKey);
					tagMappingComponentList.add(tagMappingComp);

					// change management of the source id tag
					tagsComboBox.addValueChangeListener(event -> {
						if(event.getValue() != null && !event.getValue().trim().isEmpty()) {
							JSONObject configurationTagsJsonValue = new JSONObject();
							configurationTagsJsonValue.put("sourceTagId", event.getValue());
							configurationTagsJsonValue.put("valueMapping", new JSONObject());
							configurationTagsJsonValue.put("fixedValue", "");
							configurationTagsJson.put(targetTagKey, configurationTagsJsonValue);
							
							if(tagsSelect.get(targetTagKey).isInvalid()) {
								tagsSelect.get(targetTagKey).setInvalid(false);
							}

							changeOrCreateComboForTagValuesMapping(tagMappingComp, targetTagKey);
						} else {
							configurationTagsJson.remove(targetTagKey);
						}
					});

					if(configurationTagsJson.has(targetTagKey) && configurationTagsJson.getJSONObject(targetTagKey).has("sourceTagId")) {
						changeOrCreateComboForTagValuesMapping(tagMappingComp, targetTagKey);
					}
					// set mapping values if exists
					tagMappingComp.getSourceValuesTag().addValueChangeListener(event -> {
						// enable action only if fixed value is not set
						JSONObject targetTagConfiguration = configurationTagsJson.getJSONObject(targetTagKey);
						JSONObject targetTagConfigurationValueMapping = targetTagConfiguration.optJSONObject("valueMapping");
						if(!targetTagConfiguration.has("fixedValue") || 
								targetTagConfiguration.getString("fixedValue") == null || targetTagConfiguration.getString("fixedValue").isEmpty()) {
							// this mean that user clicked clear button FIXME HOW TO UNDERSTAND WHEN USER CLICK "X" BUTTON ?
							if(event.getOldValue() != null && event.getValue() == null && targetTagConfigurationValueMapping.has(event.getOldValue())) {
								targetTagConfigurationValueMapping.remove(event.getOldValue());
								supplierTagValues.get(targetTagConfiguration.getInt("sourceTagId")).remove(event.getOldValue());
								tagMappingComp.getSourceValueMappingTag().setValue("");
								tagMappingComp.getSourceValuesTag().getDataProvider().refreshAll();
							} else {
								if(targetTagConfigurationValueMapping != null && targetTagConfigurationValueMapping.has(event.getValue())) {
									tagMappingComp.getSourceValueMappingTag().setValue(targetTagConfigurationValueMapping.getString(event.getValue()));
								} else {
									tagMappingComp.getSourceValueMappingTag().setValue("");
								}
							}
						}
					});

					// allow custom values
					tagMappingComp.getSourceValuesTag().setAllowCustomValue(true);
					// manage custom value
					tagMappingComp.getSourceValuesTag().addCustomValueSetListener(event -> {
						supplierTagValues.get(configurationTagsJson.getJSONObject(targetTagKey).getInt("sourceTagId")).add(event.getSource().getValue());
						configurationTagsJson.getJSONObject(targetTagKey).getJSONObject("valueMapping").put(event.getSource().getValue(), "");
						tagMappingComp.getSourceValueMappingTag().setValue("");
						tagMappingComp.getSourceValuesTag().getDataProvider().refreshAll();
					});

					// set fixed value if exist
					if(configurationTagsJson.has(targetTagKey) && configurationTagsJson.getJSONObject(targetTagKey).has("fixedValue") && 
							configurationTagsJson.getJSONObject(targetTagKey).has("fixedValue")) {
						tagMappingComp.getFixedValueMappingTag().setValue(configurationTagsJson.getJSONObject(targetTagKey).getString("fixedValue"));
					}
					// set clear fixed value
					tagMappingComp.getFixedValueMappingTag().setClearButtonVisible(true);
					tagMappingComp.getSourceValuesTag().setClearButtonVisible(true);
					// set clear button event
					tagMappingComp.getFixedValueMappingTag().addValueChangeListener(event -> {
						if(event.getOldValue() != null && event.getValue().equals("") && configurationTagsJson.getJSONObject(targetTagKey).has("fixedValue")) {			
							if(configurationTagsJson.getJSONObject(targetTagKey).getString("fixedValue").equals(event.getOldValue())) {
								configurationTagsJson.getJSONObject(targetTagKey).put("fixedValue", "");
								tagMappingComp.getSourceValuesTag().setReadOnly(false);
								tagMappingComp.getSourceValueMappingTag().setReadOnly(false);
							}
						}
					});
					//SAVE THE TAG VALUE MAP
					tagMappingComp.getSaveButton().addClickListener(event -> {
						onTagMappingComponentSave(tagMappingComp);
						ParamDao.getInstance().updateParam(xRoadsModule, RewixSourceParamType.LAST_SYNCED_PRODUCT_SOURCE_ID, null);
						xRoadsModule.getXRoadsCoreService().addScheduleNowIfNotScheduled(RewixSourceProductCron.class, xRoadsModule);
					});

					Button addButton = new Button(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_MAP_VALUES, xRoadsModule.getConfiguration().getSupplierName()), new Icon(VaadinIcon.PLUS));
					addButton.setIconAfterText(true);
					setAlignSelf(FlexComponent.Alignment.END, addButton);

					addButton.addClickListener(event -> {
						if(configurationTagsJson.has(targetTagKey) && configurationTagsJson.getJSONObject(targetTagKey).has("sourceTagId")) {
							if(!tagMappingComp.isEnabled()) {
								tagMappingComp.populateComponent();
								addButton.setIcon(new Icon(VaadinIcon.MINUS));
							} else {
								tagMappingComp.dePopulateComponent();
								addButton.setIcon(new Icon(VaadinIcon.PLUS));
							}
						} else {
							tagsSelect.get(targetTagKey).setInvalid(true);
							tagsSelect.get(targetTagKey).setErrorMessage(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_NOT_MAPPED, xRoadsModule.getConfiguration().getSupplierName()));
						}
					});
					tagsLayout.add(tagsComboBox, addButton);
					tagsLayout.add(tagMappingComp, 2);
				});
		add(tagsLayout);

		Button saveAllButton = new Button(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_SAVE,
				xRoadsModule.getConfiguration().getSupplierName()), e -> {
					tagMappingComponentList.forEach(this::onTagMappingComponentSave);
					ParamDao.getInstance().updateParam(xRoadsModule, RewixSourceParamType.LAST_SYNCED_PRODUCT_SOURCE_ID, null);
					xRoadsModule.getXRoadsCoreService().addScheduleNowIfNotScheduled(RewixSourceProductCron.class, xRoadsModule);
					NotificationUtils.show(NotificationType.SUCCESS, "Tag mapping configuration saved!");
				});
		add(saveAllButton);
		
		return this;
	}

	private void onTagMappingComponentSave(TagMappingComp tagMappingComp) {
		JSONObject targetTagConfiguration = configurationTagsJson.optJSONObject(tagMappingComp.getTagetTagKey());
		if(targetTagConfiguration == null) {
			targetTagConfiguration = new JSONObject();
		}
		JSONObject targetTagConfigurationValueMapping = targetTagConfiguration.optJSONObject("valueMapping");
		if(tagMappingComp.getSourceValuesTag().getValue() != null && !tagMappingComp.getSourceValuesTag().getValue().isEmpty()) {
			targetTagConfigurationValueMapping
				.put(tagMappingComp.getSourceValuesTag().getValue(), tagMappingComp.getSourceValueMappingTag().getValue());
		}
		targetTagConfiguration.put("fixedValue", tagMappingComp.getFixedValueMappingTag().getValue());
		if(!tagMappingComp.getFixedValueMappingTag().getValue().isEmpty()) {
			tagMappingComp.getSourceValuesTag().setReadOnly(true);
			tagMappingComp.getSourceValueMappingTag().setReadOnly(true);
		}
		tagMappingComp.getSourceValuesTag().getDataProvider().refreshAll();
		// save
		ParamDao.getInstance().updateParam(xRoadsModule, RewixSourceParamType.TAG_MAP, configurationTagsJson.toString());
	}

	private void changeOrCreateComboForTagValuesMapping(TagMappingComp tagMappingComp, String targetTagKey) {
		JSONObject targetTagConfiguration = configurationTagsJson.getJSONObject(targetTagKey);
		JSONObject targetTagConfigurationValueMapping = targetTagConfiguration.optJSONObject("valueMapping");
		
		tagMappingComp.getSourceValuesTag().setLabel(tagsSupplierMap.get(targetTagConfiguration.getString("sourceTagId")) + " values :");
		// case in which there are no values ​​for this tag
		Set<String> tagValues = supplierTagValues.get(targetTagConfiguration.getInt("sourceTagId"));
		// case in which there are no values ​​for this tag
		if(tagValues == null) {
			tagValues = new HashSet<>();
			supplierTagValues.put(targetTagConfiguration.getInt("sourceTagId"), tagValues);
		}
		tagMappingComp.getSourceValuesTag().setItems(tagValues);	

		tagMappingComp.getSourceValuesTag().setRenderer(new ComponentRenderer<>(item -> {
			HorizontalLayout itemLayout = new HorizontalLayout();
			itemLayout.add(new Span(item));
			if(targetTagConfiguration != null && targetTagConfigurationValueMapping.has(item) 
					&& !targetTagConfigurationValueMapping.optString(item).isEmpty()) {
				Icon icon = new Icon(VaadinIcon.CHECK);
				icon.getStyle().set("margin-left", "auto");
				itemLayout.add(icon);
			}
			return itemLayout;
		}));
		
		if(targetTagConfiguration.has("fixedValue") && !targetTagConfiguration.getString("fixedValue").isEmpty()) {
			tagMappingComp.getSourceValuesTag().setReadOnly(true);
			tagMappingComp.getSourceValueMappingTag().setReadOnly(true);
		}
		String tagMappingCompSourceLabel = tagMappingComp.getSourceValueMappingTag().getLabel();
		String tagMappingCompFixedLabel = tagMappingComp.getFixedValueMappingTag().getLabel();
		if (tagMappingCompSourceLabel == null || tagMappingCompSourceLabel.isEmpty()) {
			tagMappingComp.getSourceValueMappingTag()
					.setLabel(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_VALUE, xRoadsModule.getConfiguration().getSupplierName()) + " :");
		}
		if (tagMappingCompFixedLabel == null || tagMappingCompFixedLabel.isEmpty()) {
			tagMappingComp.getFixedValueMappingTag()
					.setLabel(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_FIXED_VALUE, xRoadsModule.getConfiguration().getSupplierName()) + " :");
		}
		if (tagMappingComp.getSaveButton().getText() == null || tagMappingComp.getSaveButton().getText().isEmpty()) {
			tagMappingComp.getSaveButton()
					.setText(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_SAVE, xRoadsModule.getConfiguration().getSupplierName()));
		}

	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Set<String>> getAllValuesOfSupplierTags() {
		Map<Integer, Set<String>> sourceTagValues = null;
		try {
			PageData pageData = rewixApi.getCatalogForPlatformAndParse();
			sourceTagValues = new HashMap<>();
			for (RewixProduct rewixProduct : pageData.getItems()) {
				for (ProductTag tag : rewixProduct.getTags()) {
					sourceTagValues.computeIfAbsent(tag.getId(), k -> new TreeSet<>()).add(tag.getValue().getValue());
				}
			}
		} catch (IOException | UnauthorizedException e) {
			e.printStackTrace(); // FIXME
		}

		// retrieve configuration to combine the tag values ​​from both the catalog and
		// the db
		configurationTagsJson = getConfigurationTags(xRoadsModule);
		if (configurationTagsJson == null) {
			configurationTagsJson = new JSONObject();
		}
		Iterator<String> keys = configurationTagsJson.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (configurationTagsJson.get(key) instanceof JSONObject) {
				JSONObject ob = ((JSONObject) configurationTagsJson.getJSONObject(key));
				if (ob.has("sourceTagId") && ob.has("valueMapping")) {
					Iterator<String> keysValueMapping = ob.getJSONObject("valueMapping").keys();
					while (keysValueMapping.hasNext()) {
						String valueSourceTag = keysValueMapping.next();
						Integer sourceTagId = ob.getInt("sourceTagId");
						sourceTagValues.computeIfAbsent(sourceTagId, k -> new TreeSet<>()).add(valueSourceTag);
					}
				}
			}
		}
		return sourceTagValues;
	}


	public JSONObject getConfigurationTags(XRoadsRewixSourceModule xRoadsModule) {
		String configuration = ParamDao.getInstance().getParameter(xRoadsModule, RewixSourceParamType.TAG_MAP);
		if (configuration == null)
			return null;
		else {
			try {
				return new JSONObject(configuration);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}
