package it.zero11.xroads.ui.abstractview;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.HasValidator;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;

import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.modules.AbstractXRoadsModule;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.ui.component.TagTokenField;
import it.zero11.xroads.ui.i18n.UITranslation;
import it.zero11.xroads.ui.utils.NotificationUtils;
import it.zero11.xroads.ui.utils.NotificationUtils.NotificationType;
import it.zero11.xroads.ui.utils.XRoadsUIUtils;
import it.zero11.xroads.utils.XRoadsUtils;
import it.zero11.xroads.utils.modules.core.model.MarkupRoundType;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleBean;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleType;
import it.zero11.xroads.utils.modules.core.service.MarkupRuleService;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;
import it.zero11.xroads.utils.modules.core.utils.MarkupRuleUtils;

public abstract class AbstractMarkupRuleView<T extends AbstractXRoadsModule> extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	protected T xRoadsModule;
	private List<T> xRoadsModules;
	private Select<T> xRoadsModulesSelect;
	private MarkupRuleService<T> markupService;
	
	private MarkupRuleType markupRuleType;
	private Select<MarkupRuleType> xRoadsMarkupTypeSelect;
	
	private Grid<MarkupRuleBean> grid;

	public AbstractMarkupRuleView(Class<T> typeClass) {
		this.xRoadsModules =  XRoadsCoreServiceBean.getInstance()
				.getEnabledModules(false)
				.values()
				.stream()
				.filter(typeClass::isInstance)
				.map(typeClass::cast)
				.toList();
		this.xRoadsModule = xRoadsModules.get(0);
		this.markupService = new MarkupRuleService<T>();
		setSizeFull();
	}

	public abstract Map<String, String> getTagNames(T xRoadsModule);

	public abstract Map<String, String> getTagValues(T xRoadsModule, String tagId);

	public abstract Map<String, String> getFilteredTagValues(T xRoadsModule, String tagId, Map<String, List<String>> filterNode);

	protected abstract List<TagTokenField> getTagTokensFields(ObjectNode value);

	protected abstract IParamType getMarkupRulesParameter();

	// return markup price type - markup price type label
	protected abstract Map<String, String> getMarkupPriceType();

	protected abstract void onDeleteMarkupRule(MarkupRuleBean markupRuleBean);

	protected abstract void onSaveMarkupRule(MarkupRuleBean markupRuleBean);

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);

		Map<String, String> markupPriceType = getMarkupPriceType();

		HorizontalLayout topBar = new HorizontalLayout();
		topBar.setWidth("100%");
		
		xRoadsModulesSelect = new Select<>();
		xRoadsModulesSelect.setItems(xRoadsModules);
		xRoadsModulesSelect.setItemLabelGenerator(XRoadsModule::getName);
		xRoadsModulesSelect.setTextRenderer(XRoadsModule::getName);
		xRoadsModulesSelect.addValueChangeListener((event) -> {
			this.xRoadsModule = event.getValue();
			updateContainerDataSource();
		});
		
		xRoadsMarkupTypeSelect = new Select<>();
		xRoadsMarkupTypeSelect.setItems(MarkupRuleType.values());
		xRoadsMarkupTypeSelect.setItemLabelGenerator(MarkupRuleType::toString);
		xRoadsMarkupTypeSelect.setTextRenderer(MarkupRuleType::toString);
		xRoadsMarkupTypeSelect.setValue(MarkupRuleType.TAXABLE);
		this.markupRuleType = MarkupRuleType.TAXABLE;
		xRoadsMarkupTypeSelect.addValueChangeListener((event) -> {
			this.markupRuleType = event.getValue();
			updateContainerDataSource();
		});
		
		topBar.add(xRoadsModulesSelect, xRoadsMarkupTypeSelect);
		
		Button addButton = new Button(getTranslation(UITranslation.MARKUP_RULES_ADD_NEW), VaadinIcon.PLUS.create());
		topBar.add(addButton);
		
		add(topBar);
		
		grid = new Grid<>();
		grid.setSizeFull();
		grid.getElement().getStyle().set("min-height", "400px");
	
		Set<Button> editButtons = new HashSet<>();
		
		grid.addComponentColumn(rule -> new MarkupRuleComponent(rule.getProperties(), xRoadsModule, getTagNames(xRoadsModule),
				tagId -> getTagValues(xRoadsModule, tagId)))
				.setHeader(getTranslation(UITranslation.MARKUP_RULES_RULE))
				.setWidth("280px")
				.setFlexGrow(1);
		grid.addColumn(MarkupRuleBean::getMarkupPercentage).setHeader(getTranslation(UITranslation.MARKUP_RULES_MARKUP))
				.setWidth("130px")
				.setFlexGrow(0);
		grid.addColumn(MarkupRuleBean::getMarkupFixed).setHeader(getTranslation(UITranslation.MARKUP_RULES_FIXED_AMOUNT))
				.setWidth("130px")
				.setFlexGrow(0);
		grid.addColumn(markupRule -> getMarkupPriceType().get(markupRule.getBasePriceType()))
				.setHeader(getTranslation(UITranslation.MARKUP_RULES_BASE_PRICE))
				.setWidth("150px")
				.setFlexGrow(0);
		grid.addColumn(MarkupRuleBean::getMinCostMarkupPercentage)
				.setHeader(getTranslation(UITranslation.MARKUP_RULES_MIN_COST_MARKUP))
				.setWidth("130px")
				.setFlexGrow(0);
		grid.addColumn(markupRule -> XRoadsUIUtils.getMarkupRoundTypeLabel(markupRule.getRoundType()))
				.setHeader(getTranslation(UITranslation.MARKUP_RULES_ROUNDING))
				.setWidth("250px")
				.setFlexGrow(0);
		grid.addComponentColumn(rule -> {
			HorizontalLayout layout = new HorizontalLayout();
	
			Button edit = new Button(getTranslation(UITranslation.MARKUP_RULES_EDIT), VaadinIcon.EDIT.create());
			edit.addClickListener(e -> {
				MarkupRuleEditorWindow ruleEditor = new MarkupRuleEditorWindow(rule, xRoadsModule, value -> getTagTokensFields(value),
						markupPriceType, getTranslation(UITranslation.MARKUP_RULES_EDIT_WINDOW_TITLE));
				ruleEditor.open();
			});
			editButtons.add(edit);
			edit.addDetachListener(e -> editButtons.remove(e.getSource()));
			layout.add(edit);
	
			Button delete = new Button(getTranslation(UITranslation.MARKUP_RULES_DELETE), VaadinIcon.TRASH.create());
			delete.addClickListener(e -> {
				if (rule.getId() != null){
					try{
						markupService.deleteMarkupRule(rule, xRoadsModule, markupRuleType, getMarkupRulesParameter());
						onDeleteMarkupRule(rule);
					}catch (Exception e2) {
						NotificationUtils.show(NotificationType.ERROR, e2.getMessage());
					}
				}
				updateContainerDataSource();
			});
			editButtons.add(delete);
			delete.addDetachListener(e -> editButtons.remove(e.getSource()));
			layout.add(delete);
	
			return layout;
		}).setHeader(getTranslation(UITranslation.MARKUP_RULES_ACTIONS))
			.setWidth("300px")
			.setFlexGrow(0);

		grid.addItemDoubleClickListener(event -> {
			MarkupRuleEditorWindow ruleEditor = new MarkupRuleEditorWindow(event.getItem(), xRoadsModule, value -> getTagTokensFields(value),
					markupPriceType, getTranslation(UITranslation.MARKUP_RULES_EDIT_WINDOW_TITLE));
			ruleEditor.open();
		});
	
		addButton.addClickListener((event)->{
			MarkupRuleBean newRule = new MarkupRuleBean();
			newRule.setBasePriceType(markupPriceType.keySet().stream().findFirst().get());
			newRule.setMarkupPercentage(BigDecimal.ZERO);
			newRule.setMarkupFixed(BigDecimal.ZERO);
			newRule.setMinCostMarkupPercentage(BigDecimal.ZERO);
			newRule.setRoundType(MarkupRoundType.None);
			try {
				MarkupRuleEditorWindow ruleEditor = new MarkupRuleEditorWindow(newRule, xRoadsModule, value -> getTagTokensFields(value),
						markupPriceType, getTranslation(UITranslation.MARKUP_RULES_CREATE_WINDOW_TITLE));
				ruleEditor.open();
			} catch(Throwable e) {
				e.printStackTrace();
			}
		});
		
		add(grid);
		xRoadsModulesSelect.setValue(this.xRoadsModule);
		xRoadsModulesSelect.setValue(this.xRoadsModule);
	}

	private void updateContainerDataSource() {
		try{			
			List<MarkupRuleBean> rules = new ArrayList<>(markupService.getMarkupByModuleAndRuleType(xRoadsModule, markupRuleType, getMarkupRulesParameter()));
			grid.setItems(rules);
		}catch (Exception e) {
			NotificationUtils.show(NotificationType.ERROR, e.getMessage());
		}
	}

	public static class MarkupRuleComponent extends FormLayout {

		private static final long serialVersionUID = 1L;

		public MarkupRuleComponent(JsonNode ruleProperties, XRoadsModule xRoadsModule, Map<String, String> tagNames,
				Function<String, Map<String, String>> getTagValues) {

			setResponsiveSteps(
					new ResponsiveStep("0", 1, LabelsPosition.ASIDE)
			);
			getStyle().set("pointer-events", "none");
			getStyle().set("--vaadin-form-item-label-width", "10em");
			
				if (ruleProperties != null && ruleProperties.size() > 0) {
					Iterator<Entry<String,JsonNode>> it = ruleProperties.fields();
					while(it.hasNext()){
						
						String xRoadsMarkerText;
						boolean tagEntry = true;
						Entry<String,JsonNode> entry = it.next();
						if (entry.getKey().equals(MarkupRuleUtils.MIN_BASE_PRICE_KEY)){
							tagEntry = false;
							xRoadsMarkerText = getTranslation(UITranslation.MARKUP_RULES_MIN_BASE_PRICE);
						} else if(entry.getKey().equals(MarkupRuleUtils.MAX_BASE_PRICE_KEY)){
							tagEntry = false;
							xRoadsMarkerText = getTranslation(UITranslation.MARKUP_RULES_MAX_BASE_PRICE);
						} else {
							try {
								String tagId = entry.getKey();
								xRoadsMarkerText = tagNames.get(tagId);
							} catch(NumberFormatException e) {
								continue;
							}
						}
						
						VerticalLayout valuesLayout = new VerticalLayout();
						valuesLayout.setPadding(false);
						valuesLayout.setSpacing(false);
						
						if(tagEntry) { // TAG VALUES
							Map<String, String> tagValues = getTagValues.apply(entry.getKey());
							Iterator<JsonNode> itElements = entry.getValue().elements();
							while(itElements.hasNext()){
								JsonNode element = itElements.next();
								Div xRoadsItem = new Div();
								xRoadsItem.addClassName("xroads-item");
								
								String translatedValue = tagValues.get(element.asText());
								xRoadsItem.setText(translatedValue != null ? translatedValue : element.asText());
								valuesLayout.add(xRoadsItem);
							}
						} else { // MIN PRICE VALUES
							Div sopifyItem = new Div();
							sopifyItem.addClassName("xroads-item");
							sopifyItem.setText(entry.getValue().asText());
							valuesLayout.add(sopifyItem);
						}
						
						addFormItem(valuesLayout, xRoadsMarkerText);
					}
				}else {					
					addFormItem(new Span(), getTranslation(UITranslation.MARKUP_RULES_DEFAULT_RULE));
				}
		}
	}
	
	private class MarkupRuleEditorWindow extends Dialog{
		
		private static final long serialVersionUID = 1L;
		
		private FormLayout formLayout;
		private Binder<MarkupRuleBean> binder;
		
		public MarkupRuleEditorWindow(MarkupRuleBean markupRule, T xRoadsModule,
				Function<ObjectNode, List<TagTokenField>> getTagTokensFields,
				Map<String, String> markupPriceType, String titleText) {
			this.binder = new Binder<MarkupRuleBean>();
			binder.setBean(markupRule);
			this.setModal(true);
			
			setWidth("min(800px, 95%)");
			
			formLayout = new FormLayout();
			formLayout.setResponsiveSteps(
					new ResponsiveStep("0", 1, LabelsPosition.TOP),
					new ResponsiveStep("550px", 2, LabelsPosition.TOP)
			);
			
			Select<String> basePriceType = new Select<>();
			basePriceType.setLabel(getTranslation(UITranslation.MARKUP_RULES_BASE_PRICE));
			basePriceType.setWidthFull();
			basePriceType.setItems(markupPriceType.keySet());
			basePriceType.setItemLabelGenerator(markupPriceType::get);
			basePriceType.setTextRenderer(markupPriceType::get);
			binder.forField(basePriceType)
			.asRequired()
			.bind(MarkupRuleBean::getBasePriceType, MarkupRuleBean::setBasePriceType);
			formLayout.add(basePriceType);
			
			BigDecimalField markupFixed = new BigDecimalField(getTranslation(UITranslation.MARKUP_RULES_FIXED_AMOUNT));
			markupFixed.setWidthFull();
			binder.forField(markupFixed)
			.asRequired()
			.bind(MarkupRuleBean::getMarkupFixed, MarkupRuleBean::setMarkupFixed);
			formLayout.add(markupFixed);
			
			BigDecimalField markupPercentage = new BigDecimalField(getTranslation(UITranslation.MARKUP_RULES_MARKUP));
			markupPercentage.setWidthFull();
			binder.forField(markupPercentage)
			.asRequired()
			.bind(MarkupRuleBean::getMarkupPercentage, MarkupRuleBean::setMarkupPercentage);
			formLayout.add(markupPercentage);
			
			BigDecimalField minCostMarkupPercentage = new BigDecimalField(getTranslation(UITranslation.MARKUP_RULES_MIN_COST_MARKUP));
			minCostMarkupPercentage.setWidthFull();
			binder.forField(minCostMarkupPercentage)
			.asRequired()
			.bind(MarkupRuleBean::getMinCostMarkupPercentage, MarkupRuleBean::setMinCostMarkupPercentage);
			formLayout.add(minCostMarkupPercentage);
			
			Select<MarkupRoundType> roundType = new Select<>();
			roundType.setLabel(getTranslation(UITranslation.MARKUP_RULES_ROUNDING));
			roundType.setWidthFull();
			roundType.setItems(MarkupRoundType.values());
			roundType.setItemLabelGenerator(XRoadsUIUtils::getMarkupRoundTypeLabel);
			roundType.setTextRenderer(XRoadsUIUtils::getMarkupRoundTypeLabel);
			binder.forField(roundType)
			.asRequired()
			.bind(MarkupRuleBean::getRoundType, MarkupRuleBean::setRoundType);
			formLayout.add(roundType);
			
			PropertiesField markupRulePropertyField = new PropertiesField(markupRule.getProperties(), getTagTokensFields);
			binder.forField(markupRulePropertyField)
			.bind(MarkupRuleBean::getProperties, MarkupRuleBean::setProperties);
			
			Button save = new Button(getTranslation(UITranslation.MARKUP_RULES_SAVE), event -> {					
				if (binder.validate().isOk() && binder.writeBeanIfValid(markupRule)) {
					try {
						markupService.saveMarkupRule(markupRule, xRoadsModule, markupRuleType, getMarkupRulesParameter());
						onSaveMarkupRule(markupRule);
						close();
						updateContainerDataSource();
					} catch (IllegalArgumentException e) {
						NotificationUtils.show(NotificationType.ERROR, e.getMessage());
					} catch (Exception e) {
						NotificationUtils.show(NotificationType.ERROR, getTranslation(UITranslation.MARKUP_RULES_SAVE_ERROR));
					}
				}
			});
			save.addClassName("save");
			Button cancel = new Button(getTranslation(UITranslation.MARKUP_RULES_CANCEL), e -> {
				close();
				updateContainerDataSource();
			});
			cancel.addClassName("cancel");
			
			HorizontalLayout footerLayout = new HorizontalLayout();
			footerLayout.setWidthFull();
			footerLayout.setPadding(true);
			footerLayout.setJustifyContentMode(JustifyContentMode.END);
			footerLayout.add(cancel, save);
			
			if(titleText != null) {
				add(new H3(titleText));
			}
			add(formLayout, markupRulePropertyField, footerLayout);
		}
		
	}

	public static class PropertiesField extends CustomField<JsonNode> implements Serializable, HasValidator<JsonNode>{
		private static final long serialVersionUID = 2L;

		private transient JsonNode value;
		
		private FormLayout layout;
		private BigDecimalField minBasePriceField;
		private BigDecimalField maxBasePriceField;
		private List<TagTokenField> tagTokensFields;
		
		public PropertiesField(JsonNode properties, Function<ObjectNode, List<TagTokenField>> getTagTokensFields){
			tagTokensFields = new ArrayList<TagTokenField>();
			if (properties == null || properties instanceof NullNode){
				value = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
			} else {
				value = properties.deepCopy();
			}

			layout = new FormLayout();
			layout.setResponsiveSteps(
					new ResponsiveStep("0", 1, LabelsPosition.TOP),
					new ResponsiveStep("550px", 2, LabelsPosition.TOP)
			);			
			
			// BASE PRICE PROPERTIES
			minBasePriceField = new BigDecimalField(getTranslation(UITranslation.MARKUP_RULES_MIN_BASE_PRICE));
			minBasePriceField.setErrorMessage(getTranslation(UITranslation.MARKUP_RULES_MIN_BASE_PRICE_ERROR_MESSAGE));
			minBasePriceField.addValueChangeListener(e -> {
				isMinValueInvalid(e.getValue());
				if (e.getValue() != null) {
					((ObjectNode) value).put(MarkupRuleUtils.MIN_BASE_PRICE_KEY, e.getValue().toPlainString());
				} else {
					((ObjectNode) value).remove(MarkupRuleUtils.MIN_BASE_PRICE_KEY);
				}
				updateValue();
			});
			maxBasePriceField = new BigDecimalField(getTranslation(UITranslation.MARKUP_RULES_MAX_BASE_PRICE));
			maxBasePriceField.setErrorMessage(getTranslation(UITranslation.MARKUP_RULES_MAX_BASE_PRICE_ERROR_MESSAGE));
			maxBasePriceField.addValueChangeListener(e -> {
				isMaxValueInvalid(e.getValue());
				if (e.getValue() != null) {
					((ObjectNode) value).put(MarkupRuleUtils.MAX_BASE_PRICE_KEY, e.getValue().toPlainString());
				} else {
					((ObjectNode) value).remove(MarkupRuleUtils.MAX_BASE_PRICE_KEY);
				}
				updateValue();
			});

			layout.add(minBasePriceField, maxBasePriceField);
			// TAG PROPERTIES
			for(TagTokenField tagTokenField : getTagTokensFields.apply((ObjectNode) value)) {
				tagTokenField.addValueChangeListener(e -> {
					String tagIdStr = String.valueOf(tagTokenField.getTagId());
					if (tagTokenField.getValue() != null && tagTokenField.getValue().length > 0) {
						((ObjectNode) value).set(tagIdStr, toArrayNode(tagTokenField.getValue()));
					} else {
						((ObjectNode) value).remove(tagIdStr);
					}
					updateValue();
				});
				tagTokensFields.add(tagTokenField);
				layout.add(tagTokenField);
			}
			
			add(layout);
		}

		@Override
		protected JsonNode generateModelValue() {
			return value;
		}

		@Override
		protected void setPresentationValue(JsonNode value) {
			if (value != null && !(value instanceof NullNode)) {
				this.value = value.deepCopy();
			} else {
				this.value = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
			}
			
			if (!value.path(MarkupRuleUtils.MIN_BASE_PRICE_KEY).isMissingNode()) {
				minBasePriceField.setValue(new BigDecimal(value.path(MarkupRuleUtils.MIN_BASE_PRICE_KEY).asText()));
			} else {
				minBasePriceField.setValue(null);
			}
			if (!value.path(MarkupRuleUtils.MAX_BASE_PRICE_KEY).isMissingNode()) {
				maxBasePriceField.setValue(new BigDecimal(value.path(MarkupRuleUtils.MAX_BASE_PRICE_KEY).asText()));
			} else {
				maxBasePriceField.setValue(null);
			}
			
			for(TagTokenField tagTokenField : tagTokensFields) {
				ArrayNode arrayNode1 = (ArrayNode) value.get(String.valueOf(tagTokenField.getTagId()));
				if (arrayNode1 != null){
					tagTokenField.setValue(toArray(arrayNode1));
				} else if(tagTokenField.getValue() != null) {
					tagTokenField.setValue(new String[0]);
				}
			}
		}
		
		@Override
		public Validator<JsonNode> getDefaultValidator() {
//			if(ParamDao.getInstance().getParameterAsBoolean(xRoadsModule, RewixSourceParamType.FEATURES_MARKUP_RULE_PRICE_FILTER)) {
				return (value, context) -> {
					if(value == null) {
						return ValidationResult.ok();
					}
					BigDecimal minVal = null;
					String minValStr = value.path(MarkupRuleUtils.MIN_BASE_PRICE_KEY).asText();
					if(minValStr != null && !minValStr.isEmpty()) {
						minVal = new BigDecimal(minValStr);
					}
					BigDecimal maxVal = null;
					String maxValStr = value.path(MarkupRuleUtils.MAX_BASE_PRICE_KEY).asText();
					if(maxValStr != null && !maxValStr.isEmpty()) {
						maxVal = new BigDecimal(maxValStr);
					}					
					if(!isMinValueInvalid(minVal) && !isMaxValueInvalid(maxVal)) {
						return ValidationResult.ok();
					}
					return ValidationResult.error("");
				};
//			}
//			return HasValidator.super.getDefaultValidator();
		}
		
		private void writeObject(ObjectOutputStream os) throws IOException {
			os.defaultWriteObject();
			os.writeObject(value == null ? null : JacksonUtil.toString(value));
		}

		private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
			is.defaultReadObject();
			Object data = is.readObject();
			if (data == null) {
				this.value = null;
			}else {
				this.value = JacksonUtil.toJsonNode((String)data);
			}
		}
		
		private String[] toArray(ArrayNode arrayNode) {
			String[] result = new String[arrayNode.size()];
			Iterator<JsonNode> it = arrayNode.iterator();
			int i = 0;
			while(it.hasNext()){
				result[i++] = it.next().asText();
			}
			return result;
		}
		
		private JsonNode toArrayNode(String[] values) {
			ArrayNode arrayNode = XRoadsUtils.OBJECT_MAPPER.createArrayNode();
			for (String value: values){
				arrayNode.add(value);
			};
			return arrayNode;
		}
		
		private boolean isMinValueInvalid(BigDecimal minValue) {
			boolean invalidMin = false;
			if (minValue != null && minValue.compareTo(BigDecimal.ZERO) < 0) {
					invalidMin = true;
			}
			minBasePriceField.setInvalid(invalidMin);
			return invalidMin;
		}
		
		private boolean isMaxValueInvalid(BigDecimal manValue) {
			boolean invalidMax = false;
			if (manValue != null && manValue.compareTo(BigDecimal.ZERO) < 1) {
				invalidMax = true;
			}
			maxBasePriceField.setInvalid(invalidMax);
			return invalidMax;
		}
	}
}