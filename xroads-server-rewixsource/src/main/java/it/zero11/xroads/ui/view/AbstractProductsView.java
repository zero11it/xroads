package it.zero11.xroads.ui.view;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.function.ValueProvider;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.UnauthorizedException;
import it.zero11.xroads.modules.rewixsource.api.model.Image;
import it.zero11.xroads.modules.rewixsource.api.model.RewixProduct;
import it.zero11.xroads.modules.rewixsource.api.model.TagType;
import it.zero11.xroads.modules.rewixsource.cron.RewixSourceProductCron;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.modules.rewixsource.service.RewixService;
import it.zero11.xroads.modules.rewixsource.utils.RewixPriceUtils;
import it.zero11.xroads.modules.rewixsource.utils.RewixProductUtils;
import it.zero11.xroads.ui.component.ConfirmWindow;
import it.zero11.xroads.ui.component.TagTokenField;
import it.zero11.xroads.ui.i18n.RewixUITranslation;
import it.zero11.xroads.ui.utils.NotificationUtils;
import it.zero11.xroads.ui.utils.NotificationUtils.NotificationType;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleBean;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleType;
import it.zero11.xroads.utils.modules.core.service.MarkupRuleService;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;
import it.zero11.xroads.utils.modules.core.utils.MarkupRuleUtils;

public abstract class AbstractProductsView extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	private Set<String> rewixImportedIds;

	private Grid<RewixProduct> grid;

	private TagTokenField tagBrandTokenField;
	private TagTokenField tagCategoryTokenField;
	private TagTokenField tagSubcategoryTokenField;
	private TagTokenField tagColorTokenField;
	private TagTokenField tagGenderTokenField;
	private TagTokenField tagSeasonTokenField;
	private Details filterDetails;
	private TextField searchField;
	
	private RewixService rewixService;
	private MarkupRuleService<XRoadsRewixSourceModule> markupRuleService;
	private XRoadsRewixSourceModule xRoadsModule;
	private List<XRoadsRewixSourceModule> xRoadsModules;
	private String locale;

	public enum ImportFilterType {
		Queued,
		NotImported,
		Imported
	}

	protected boolean checkAdditionalFilterTypeProduct(RewixProduct rewixProduct) {
		return true;
	};

	public AbstractProductsView() {
		this.markupRuleService = new MarkupRuleService<XRoadsRewixSourceModule>();
		this.xRoadsModules = getXRoadsModules();
		this.xRoadsModule = xRoadsModules.get(0);
		this.locale = this.xRoadsModule.getConfiguration().getLocale();
		this.rewixService = new RewixService();	
		setSizeFull();
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		
		HorizontalLayout topBar = new HorizontalLayout();
		topBar.setWidth("100%");
		
		Select<XRoadsRewixSourceModule> xRoadsModulesSelect = new Select<>();
		xRoadsModulesSelect.setItems(xRoadsModules);
		xRoadsModulesSelect.setItemLabelGenerator(XRoadsRewixSourceModule::getName);
		xRoadsModulesSelect.setTextRenderer(XRoadsRewixSourceModule::getName);
		xRoadsModulesSelect.addValueChangeListener((event) -> {
			this.xRoadsModule = event.getValue();
			this.locale = this.xRoadsModule.getConfiguration().getLocale();
			this.rewixImportedIds = getRewixImportedProductIds();
			
			tagBrandTokenField.setValues(rewixService.getTagValues(xRoadsModule, tagBrandTokenField.getTagId()));
			tagCategoryTokenField.setValues(rewixService.getTagValues(xRoadsModule, tagCategoryTokenField.getTagId()));
			tagSubcategoryTokenField.setValues(rewixService.getTagValues(xRoadsModule, tagSubcategoryTokenField.getTagId()));
			tagColorTokenField.setValues(rewixService.getTagValues(xRoadsModule, tagColorTokenField.getTagId()));
			tagGenderTokenField.setValues(rewixService.getTagValues(xRoadsModule, tagGenderTokenField.getTagId()));
			tagSeasonTokenField.setValues(rewixService.getTagValues(xRoadsModule, tagSeasonTokenField.getTagId()));
			
			updateContainerDataSource();
		});
		
		topBar.add(xRoadsModulesSelect);
		
		Button addButton = null;
		if (getImportFilterType().equals(ImportFilterType.NotImported)) {
			addButton = new Button(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_IMPORT_ALL_FILTERED), VaadinIcon.PLUS.create());
			topBar.add(addButton);
		} else if(getImportFilterType().equals(ImportFilterType.Imported)) {
			addButton = new Button(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_REIMPORT_ALL_FILTERED), VaadinIcon.PLUS.create());
			topBar.add(addButton);
		}			
		
		add(topBar);
		topBar.getElement().getStyle().set("flex-shrink", "0");
		setFlexGrow(0, topBar);
		
		FormLayout filterBar = new FormLayout();
		filterBar.setWidthFull();

		searchField = new TextField();
		searchField.setPlaceholder(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_FILTER_SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(event -> {
			updateContainerDataSource();
		});
		filterBar.add(searchField);
		filterBar.getElement().appendChild(ElementFactory.createBr());
		
		Map<String, String> tagNames = new RewixService().getTagNames(xRoadsModule);
		
		tagBrandTokenField = getTagTokenField(tagNames, TagType.TAG_BRAND);
		filterBar.add(tagBrandTokenField);
		tagCategoryTokenField = getTagTokenField(tagNames, TagType.TAG_CATEGORY);
		filterBar.add(tagCategoryTokenField);
		tagSubcategoryTokenField = getTagTokenField(tagNames, TagType.TAG_SUBCATEGORY);
		filterBar.add(tagSubcategoryTokenField);
		tagColorTokenField = getTagTokenField(tagNames, TagType.TAG_COLOR);
		filterBar.add(tagColorTokenField);
		tagGenderTokenField = getTagTokenField(tagNames, TagType.TAG_GENDER);
		filterBar.add(tagGenderTokenField);
		tagSeasonTokenField = getTagTokenField(tagNames, TagType.TAG_SEASON);
		filterBar.add(tagSeasonTokenField);
		
		filterBar.setResponsiveSteps(
		        new ResponsiveStep("0", 1),
		        new ResponsiveStep("27em", 2),
		        new ResponsiveStep("40em", 3),
		        new ResponsiveStep("80em", 6));
		
		filterDetails = new Details(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_FILTER), filterBar);
		filterDetails.setOpened(true);
		filterDetails.getElement().getStyle().set("width", "100%");
		add(filterDetails);
		filterDetails.getElement().getStyle().set("flex-shrink", "0");
		setFlexGrow(0, filterDetails);
		
		grid = new Grid<>();
		grid.setSizeFull();
		grid.getElement().getStyle().set("min-height", "400px");
		
		grid.addColumn(new ComponentRenderer<Icon, RewixProduct>(rewixProduct -> {
			String xRoadsProductSourceId = RewixProductUtils.getXroadsProductSourceId(rewixProduct, xRoadsModule);
			Icon icon;
			if (!rewixImportedIds.contains(xRoadsProductSourceId)) {
				icon = VaadinIcon.PLUS_CIRCLE.create();
			}else {
				icon = VaadinIcon.CHECK.create();
			}
			icon.addClickListener((event)->{
				try{
					if (rewixImportedIds.contains(xRoadsProductSourceId)){
						NotificationUtils.show(NotificationType.WARNING, getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_ERROR_ALREADY_IMPORTED)); //, Type.WARNING_MESSAGE);
					} else {
						Product xRoadsProduct = RewixProductUtils.getXRoadsBaseStructureProductFromRewixProduct(rewixProduct, xRoadsModule, true);
						xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, xRoadsProduct);
						rewixImportedIds.add(xRoadsProductSourceId);
						grid.getDataProvider().refreshItem(rewixProduct);
						// SCHEDULE CRON
						xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF, "force-full-sync");
						xRoadsModule.getXRoadsCoreService().addScheduleNowIfNotScheduled(getProductImportCronClass(), xRoadsModule);
						NotificationUtils.show(NotificationType.SUCCESS, getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_NOTIFICATION_ADDED_TO_QUEUE));
					}
				}catch (Exception e) {
					e.printStackTrace();
					NotificationUtils.show(NotificationType.ERROR, getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_ERROR_IMPORT_GENERIC));
				}
			});
			return icon;
		})).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_STATUS))
			.setTextAlign(ColumnTextAlign.CENTER).setWidth("54px").setFrozen(true)
			.setVisible(getImportFilterType().equals(ImportFilterType.NotImported));
		
		grid.addColumn(new ProductNameProvider(locale)).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_NAME)).setWidth("120px");
		grid.addColumn(RewixProduct::getCode).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_CODE)).setWidth("140px");
		grid.addColumn(new TagValueValueProvider(locale, TagType.TAG_BRAND.tagId)).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_BRANDS)).setWidth("180px");
		grid.addColumn(new TagValueValueProvider(locale, TagType.TAG_CATEGORY.tagId)).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_CATEGORY)).setWidth("120px");
		grid.addColumn(new TagValueValueProvider(locale, TagType.TAG_SUBCATEGORY.tagId)).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_SUBCATEGORY)).setWidth("120px");
		grid.addColumn(RewixProduct::getAvailability).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_QTY)).setTextAlign(ColumnTextAlign.END).setWidth("60px");
		
		
		grid.addColumn(LitRenderer.<RewixProduct>of("<div><p>${item.bestCost} ${item.currency}</p><p>${item.suggested} ${item.currency}</p><p>${item.street} ${item.currency}</p></div>")
		        .withProperty("bestCost", RewixProduct::getBestTaxable)
		        .withProperty("street", (ValueProvider<RewixProduct, String>) rewixProduct -> rewixProduct.getStreetPrice() != null ? rewixProduct.getStreetPrice().setScale(2, RoundingMode.HALF_UP).toString() : "error")
		        .withProperty("suggested", (ValueProvider<RewixProduct, String>) rewixProduct -> (rewixProduct.getSuggestedPrice() != null) ?
		                rewixProduct.getSuggestedPrice().setScale(2, RoundingMode.HALF_UP).toString() :
		                (rewixProduct.getStreetPrice() != null ? rewixProduct.getStreetPrice().setScale(2, RoundingMode.HALF_UP).toString() : "error"))
		        .withProperty("currency", rewixProduct -> xRoadsModule.getConfiguration().getCurrency()))
		        .setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_PRICES)).setTextAlign(ColumnTextAlign.END).setWidth("120px");


		grid.addColumn((rewixProduct -> {
			String proposed;
			try{
				BigDecimal exchangeRate = ParamDao.getInstance().getParameterAsBigDecimal(xRoadsModule, RewixSourceParamType.REWIX_CURRENCY_CONVERSION);
				if (exchangeRate == null)
					exchangeRate = new BigDecimal("1.0");
				String locale = ParamDao.getInstance().getParameter(xRoadsModule, RewixSourceParamType.REWIX_LOCALE);
				Map<String, List<String>> translatedTags = RewixProductUtils.getTranslatedTags(locale, rewixProduct);
				
				SortedSet<MarkupRuleBean> rules = markupRuleService.getMarkupByModuleAndRuleType(xRoadsModule, MarkupRuleType.TAXABLE, RewixSourceParamType.MARKUP_RULES);
				proposed = MarkupRuleUtils.applyRule(xRoadsModule, rules,
						RewixPriceUtils.getMarkupPriceTypeToPriceeMap(xRoadsModule, rewixProduct), "BestTaxable",
						translatedTags, exchangeRate).toString();
			}catch (Exception e) {
				proposed = getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_ERROR);
			}
			String tenantCurreny = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.CURRENCY);
			return proposed + " " + tenantCurreny;
		})).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_PROPOSED)).setTextAlign(ColumnTextAlign.END).setWidth("120px");

		grid.addColumn(new ComponentRenderer<com.vaadin.flow.component.html.Image, RewixProduct>(rewixProduct -> {
			List<Image> images = rewixProduct.getImages();
			com.vaadin.flow.component.html.Image image;
			if (images != null && images.size() > 0){
				image = new com.vaadin.flow.component.html.Image(xRoadsModule.getConfiguration().getApiEndpoint() + images.get(0).getUrl() + "/xsmall.jpg", "");
			}else{
				image = new com.vaadin.flow.component.html.Image();
			}
			image.setHeight("100px");
			image.getStyle().set("display", "block");
			image.getStyle().set("margin", "0 auto");
						
			return image;
		})).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_IMAGE)).setWidth("130px");
		
		grid.addColumn(new ComponentRenderer<Anchor, RewixProduct>(rewixProduct -> {
			try {
				Anchor anchor = new Anchor(xRoadsModule.getConfiguration().getApiEndpoint() + "/current/product/" + URLEncoder.encode(rewixProduct.getCode(), StandardCharsets.UTF_8.name()),
						VaadinIcon.EXTERNAL_LINK.create());
				anchor.setTarget("_blank");
				return anchor;
			}catch(Exception e) {
				return null;
			}
		})).setHeader(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_GRID_LINK)).setTextAlign(ColumnTextAlign.CENTER).setWidth("70px");
		
		grid.setSelectionMode(SelectionMode.NONE);

		add(grid);
		setFlexGrow(1, grid);
		
		if (addButton != null && getImportFilterType().equals(ImportFilterType.NotImported)) {
			addButton.addClickListener((event)->{
				try {
					final List<RewixProduct> products = new RewixService().getProducts(xRoadsModule, getCurrentFilterNode(), searchField.getValue()).stream()
						.filter((p)-> !this.rewixImportedIds.contains(RewixProductUtils.getXroadsProductSourceId(p, xRoadsModule)))
						.filter(this::checkAdditionalFilterTypeProduct)
						.collect(Collectors.toList());
					
					if (products.isEmpty()) {
						NotificationUtils.show(NotificationType.ERROR, getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_ERROR_NO_PRODUCT_SELECTED));
					}else {
						ConfirmWindow.askConfirm(
								getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_ADD_TO_QUEUE_CONFIRM_TITLE),
								getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_ADD_TO_QUEUE_CONFIRM_MESSAGE,
										products.size()),
								(confirmEvent) -> {
									try {
										for (RewixProduct p : products) {
											Product xRoadsProduct = RewixProductUtils.getXRoadsBaseStructureProductFromRewixProduct(p, xRoadsModule, true);
											xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, xRoadsProduct);
											rewixImportedIds.add(RewixProductUtils.getXroadsProductSourceId(p, xRoadsModule));
										}
										// SCHEDULE CRON
										xRoadsModule.getXRoadsCoreService().addScheduleNowIfNotScheduled(getProductImportCronClass(), xRoadsModule);
										Notification.show(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_NOTIFICATION_ADDED_TO_QUEUE));
									} catch (Exception e) {
										NotificationUtils.show(NotificationType.ERROR, e.getMessage());
									}
								});
					}
				} catch (IOException | UnauthorizedException e) {						
					NotificationUtils.show(NotificationType.ERROR, e.getMessage());
				}
			});
		}
		// fire valueChangeEvent and update all data
		xRoadsModulesSelect.setValue(this.xRoadsModule);
	}

	private Set<String> getRewixImportedProductIds() {
	    Set<String> importedProductIds = new HashSet<String>();
	    List<Product> productToSync;
	    String lastSourceId = null;
	    do {
	        productToSync = xRoadsModule.getXRoadsCoreService().getEntities(Product.class, lastSourceId, 200,
	                null, xRoadsModule);
	        importedProductIds.addAll(productToSync.stream().map(Product::getSourceId).toList());
	        if (productToSync.size() == 200) {
	            lastSourceId = productToSync.get(productToSync.size() - 1).getSourceId();
	        } else {
	            lastSourceId = null;
	        }
	    } while (productToSync.size() == XRoadsRewixSourceModule.BATCH_SIZE);
	    return importedProductIds;
	}

	private TagTokenField getTagTokenField(Map<String, String> tagNames, TagType tagType) {
		String tagIdStr = String.valueOf(tagType.tagId);
		TagTokenField tagTokenField = new TagTokenField(tagIdStr,
				rewixService.getTagValues(xRoadsModule, tagIdStr), "");
		tagTokenField.setLabel(tagNames.getOrDefault(tagType.tagIdStr, tagType.tagName));
		tagTokenField.addValueChangeListener((e)-> {
//			if(ParamDao.getInstance().getParameterAsBoolean(ParamType.FEATURES_CROSSTAG_FILTER)) {
			updateFilteredValues();
//			}
			updateContainerDataSource();
		});
		return tagTokenField;
	}
	
	private void updateFilteredValues() {
		Map<String, List<String>> filterNode = getCurrentFilterNode();
		tagBrandTokenField.setFilter(filterNode, 
				fNode -> rewixService.getFilteredTagValues(xRoadsModule, tagBrandTokenField.getTagId(), fNode));
		tagCategoryTokenField.setFilter(filterNode, 
				fNode -> rewixService.getFilteredTagValues(xRoadsModule, tagCategoryTokenField.getTagId(), fNode));
		tagSubcategoryTokenField.setFilter(filterNode, 
				fNode -> rewixService.getFilteredTagValues(xRoadsModule, tagSubcategoryTokenField.getTagId(), fNode));
		tagColorTokenField.setFilter(filterNode, 
				fNode -> rewixService.getFilteredTagValues(xRoadsModule, tagColorTokenField.getTagId(), fNode));
		tagGenderTokenField.setFilter(filterNode, 
				fNode -> rewixService.getFilteredTagValues(xRoadsModule, tagGenderTokenField.getTagId(), fNode));
		tagSeasonTokenField.setFilter(filterNode, 
				fNode -> rewixService.getFilteredTagValues(xRoadsModule, tagSeasonTokenField.getTagId(), fNode));
	}
	
	private void updateContainerDataSource(){
		Map<String, List<String>> filterNode = getCurrentFilterNode();
		ImportFilterType importFilterType = getImportFilterType();
		
		try{
			grid.setItems(new RewixService().getProducts(xRoadsModule, filterNode, searchField.getValue()).stream()
				.filter((rewixProduct) -> {
					String rewixProductSourceId = RewixProductUtils.getXroadsProductSourceId(rewixProduct, xRoadsModule);
					switch (importFilterType) {
					case NotImported:
						return !rewixImportedIds.contains(rewixProductSourceId)
								&& checkAdditionalFilterTypeProduct(rewixProduct);		
					case Imported:
						return rewixImportedIds.contains(rewixProductSourceId) && checkAdditionalFilterTypeProduct(rewixProduct);
					default:
						return true;
					}
				})
				.filter(this::checkAdditionalFilterTypeProduct)
				.toList()
			);
		}catch (Exception e) {
			e.printStackTrace();
			NotificationUtils.show(NotificationType.ERROR, getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_FILTER_ERROR));
		}
		
		if (filterNode.size() == 0) {
			filterDetails.setSummaryText(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_FILTERS_NO_ACTIVE));
		}else {
			filterDetails.setSummaryText(getTranslation(RewixUITranslation.ABSTRACT_PRODUCTS_VIEW_FILTERS_ACTIVE, filterNode.size()));
		}
	}

	private Map<String, List<String>> getCurrentFilterNode() {
		Map<String, List<String>> filterNode = new HashMap<String, List<String>>();
		
		if (tagBrandTokenField.getValue() != null && tagBrandTokenField.getValue().length > 0){
			filterNode.put(String.valueOf(TagType.TAG_BRAND.tagId), Arrays.asList(tagBrandTokenField.getValue()));
		}
		if (tagCategoryTokenField.getValue() != null && tagCategoryTokenField.getValue().length > 0){
			filterNode.put(String.valueOf(TagType.TAG_CATEGORY.tagId), Arrays.asList(tagCategoryTokenField.getValue()));
		}
		if (tagSubcategoryTokenField.getValue() != null && tagSubcategoryTokenField.getValue().length > 0){
			filterNode.put(String.valueOf(TagType.TAG_SUBCATEGORY.tagId), Arrays.asList(tagSubcategoryTokenField.getValue()));
		}
		if (tagColorTokenField.getValue() != null && tagColorTokenField.getValue().length > 0){
			filterNode.put(String.valueOf(TagType.TAG_COLOR.tagId), Arrays.asList(tagColorTokenField.getValue()));
		}
		if (tagGenderTokenField.getValue() != null && tagGenderTokenField.getValue().length > 0){
			filterNode.put(String.valueOf(TagType.TAG_GENDER.tagId), Arrays.asList(tagGenderTokenField.getValue()));
		}
		if (tagSeasonTokenField.getValue() != null && tagSeasonTokenField.getValue().length > 0){
			filterNode.put(String.valueOf(TagType.TAG_SEASON.tagId), Arrays.asList(tagSeasonTokenField.getValue()));
		}
		
		return filterNode;
	}
	
	protected ImportFilterType getImportFilterType() {
		return ImportFilterType.NotImported;
	}

	protected Class<? extends AbstractXRoadsCronRunnable<XRoadsRewixSourceModule>> getProductImportCronClass() {
		return RewixSourceProductCron.class;
	}

	protected List<XRoadsRewixSourceModule> getXRoadsModules() {
		return XRoadsCoreServiceBean.getInstance()
				.getEnabledModules(false)
				.values()
				.stream()
				.filter(XRoadsRewixSourceModule.class::isInstance)
				.map(XRoadsRewixSourceModule.class::cast)
				.toList();
	}

	private final class ProductNameProvider implements ValueProvider<RewixProduct, String> {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unused")
		private final String locale;

		protected ProductNameProvider(String locale){
			this.locale = locale;
		}

		@Override
		public String apply(RewixProduct rewixProduct) {
			if (rewixProduct.getProductLocalizations() != null && rewixProduct.getProductLocalizations().getProductName() != null &&
					rewixProduct.getProductLocalizations().getProductName().size() > 0) {
				return rewixProduct.getProductLocalizations().getProductName().get(0).getValue(); 
			}else {
				return rewixProduct.getName();
			}
		}
	}

	private final class TagValueValueProvider implements ValueProvider<RewixProduct, String> {
		private static final long serialVersionUID = 1L;

		private final String locale;
		private final int tagId;

		protected TagValueValueProvider(String locale, int tagId){
			this.locale = locale;
			this.tagId = tagId;
		}

		@Override
		public String apply(RewixProduct source) {
			if (source.getTags() != null){
				try{
					return RewixProductUtils.getTag(locale, source.getTags(), tagId, source.getCode()).values().stream()
							.collect(Collectors.joining(", "));
				}catch (Exception e) {
					return "";
				}
			}else{
				return "";
			}
		}
	}
}
