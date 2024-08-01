package it.zero11.xroads.ui.abstractview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.ModuleOrder;
import it.zero11.xroads.model.ModuleStatus;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;
import it.zero11.xroads.utils.modules.core.model.WrapFilter;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;

public abstract class AbstractEntityGridView<T extends AbstractEntity>  extends VerticalLayout {
	
	private static final long serialVersionUID = 1L;
	
	// Class variables
	private final Class<T> typeParameterClass;

	// UI components
	private HorizontalLayout topBar;
	private TextField searchBar;
	private Select<ModuleStatus> filterSelect;
	protected Select<XRoadsModule> moduleComboBox;
	private Button refreshButton;
	private Grid<T> genericGrid;
	private HorizontalLayout footerLayout;
	private Span countItemsSpan;

	// Data providers
	private DataProvider<T, WrapFilter> dataProvider;
	private ConfigurableFilterDataProvider<T, Void, WrapFilter> configurableDataProvider;

	// Formatting utilities
	private SimpleDateFormat stringToDateFormatter;
	private SimpleDateFormat dateToStringFormatter;

	// Filters
	private WrapFilter filters;

	public AbstractEntityGridView(Class<T> typeParameterClass) {
		filters = new WrapFilter();
		this.typeParameterClass = typeParameterClass;
		stringToDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateToStringFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		setSizeFull();	
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);

		List<XRoadsModule> modulesList = XRoadsCoreServiceBean.getInstance().getEnabledModules(false).values().stream().filter(xRoadsModule -> XRoadsUtils.moduleHasConsumer(xRoadsModule, typeParameterClass)).collect(Collectors.toList());
		if (modulesList.size() == 0) {
			add(new Paragraph("No consumer configured"));
			return;
		}
		
		genericGrid = new Grid<>();
		dataProvider = DataProvider.fromFilteringCallbacks(
				query -> {
					int offset = query.getOffset();
					int limit = query.getLimit();
					return EntityDao.getInstance().getEntities(typeParameterClass, offset, limit, query.getFilter().orElse(null),
							(query.getFilter().orElse(null) != null ?
									(((query.getFilter().get().getModuleStatus() != null && query.getFilter().get().getModuleStatus().equals(ModuleStatus.SYNC_ERRORS)) 
									|| (query.getFilter().get().getModuleStatus() != null && query.getFilter().get().getModuleStatus().equals(ModuleStatus.TO_SYNC))) ?
									ModuleOrder.LAST_ERROR_DATE : ModuleOrder.SOURCE_ID) : ModuleOrder.SOURCE_ID), moduleComboBox.getValue()).stream();
				},
				query ->{
					Integer countItemsInt = EntityDao.getInstance().countItems(typeParameterClass, query.getFilter().orElse(null),  moduleComboBox.getValue());
					countItemsSpan.setText(countItemsInt.toString());
					return countItemsInt;
				});
		configurableDataProvider = dataProvider.withConfigurableFilter();
		// set filter object to evict nullpointer when filter not exist
		configurableDataProvider.setFilter(filters);
		
		topBar = new HorizontalLayout();
		refreshButton = new Button("Refresh", event -> {
			dataProvider.refreshAll();
		});
		refreshButton.setIcon(VaadinIcon.REFRESH.create());		
		// search bar
		searchBar = new TextField();
		searchBar.setPlaceholder("Search");
		searchBar.setLabel("Search");
		searchBar.setValueChangeMode(ValueChangeMode.LAZY);
		searchBar.addValueChangeListener(event -> {
			filters.setSearchKey(event.getValue());
			configurableDataProvider.setFilter(filters);
		});
		
		
		filterSelect = new Select<>();
		filterSelect.setEmptySelectionAllowed(true);
		filterSelect.setEmptySelectionCaption("All status");
		topBar.setWidth("100%");
		filterSelect.setLabel("FILTER");
		filterSelect.setItems(ModuleStatus.values());
		filterSelect.setTextRenderer(moduleStatus -> {
			return moduleStatus.getName();
		});
		filterSelect.addValueChangeListener((event) -> {
			filters.setModuleStatus(event.getValue());
			configurableDataProvider.setFilter(filters);
		});

		moduleComboBox = new Select<>();
		moduleComboBox.setLabel("MODULE");
		moduleComboBox.setItems(modulesList);
		moduleComboBox.setTextRenderer(module -> module.getName());
		moduleComboBox.setValue(modulesList.size() > 0 ? modulesList.get(0) : null);
		moduleComboBox.addValueChangeListener(event -> {
			dataProvider.refreshAll();
		});

		genericGrid.setItems(configurableDataProvider);
		genericGrid.addColumn(T::getSourceId)
		.setWidth("220px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Source Id");

		genericGrid.addColumn(T::getVersion)
		.setWidth("100px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Version");

		addColumns(genericGrid);

		genericGrid.addColumn(entity -> {
			String moduleId = entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_ID).asText();
			if(moduleId.isEmpty())
				return "-";
			else 
				return moduleId;
		}).setWidth("220px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Module Id");

		genericGrid.addColumn(entity -> {
			String moduleVersion = entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION).asText();
			if(moduleVersion.isEmpty())
				return "-";
			else
				return moduleVersion;
		}).setWidth("150px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Module Version");


		genericGrid.addColumn(entity -> {
			String error = entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR).asText();
			Integer externalReferenceVersion = null;
			if(!entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE).asText().isEmpty())
				return error;
			externalReferenceVersion = entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION).asInt();
			if( externalReferenceVersion != null && (!entity.getVersion().equals(externalReferenceVersion)))
				return "Not Imported";
			if( externalReferenceVersion != null && (entity.getVersion().equals(externalReferenceVersion)))
				return "Ok";
			return "Not Processed";
		}).setHeader("Status");

		genericGrid.addColumn( new ComponentRenderer<>(entity -> {
			String stackTrace = entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_STACK_TRACE).asText();
			if(!stackTrace.isEmpty()) {
				Dialog errorDialog = new Dialog();
				errorDialog.setWidth("900px");
				errorDialog.setMaxHeight("750px");
				Paragraph errorParagraph = new Paragraph();
				errorParagraph.add(stackTrace);
				errorDialog.add(errorParagraph);
				return new Button(VaadinIcon.CLOSE_CIRCLE.create(), event -> {
					errorDialog.open();
				});
			} else {
				return new Label("-");
			}
		})).setWidth("95px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Error");
		genericGrid.addColumn(entity ->  {
			String lastErrorDate = entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE).asText();
			if(lastErrorDate.isEmpty()) {
				return "-";
			} else {
				try {
					return dateToStringFormatter.format(stringToDateFormatter.parse(lastErrorDate));
				} catch (ParseException e) {
					return "-";
				}
			}
		}).setWidth("160px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Date LastError");

		genericGrid.addColumn(new ComponentRenderer<>(entity -> { 
			String id = entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_ID).asText();
			int externalVersion = entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION).asInt();
			int diff = 0;
			try {
				Date lastErrorDate = stringToDateFormatter.parse(entity.getExternalReferences().path(moduleComboBox.getValue().getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE).asText());
				diff = (int) ((new Date(System.currentTimeMillis()).getTime() - lastErrorDate.getTime()) / (60 * 1000)) ;
			} catch (Exception e2) {}
			boolean forceSyncPerDate = diff > 0 && diff < 60;			
			
			if(enableforce(moduleComboBox.getValue()) && ((externalVersion == entity.getVersion()) || forceSyncPerDate)) {
				Button forceSyncButton = new Button("FORCE", VaadinIcon.REFRESH.create(), e -> {
					if(forceSyncPerDate) {
						EntityDao.getInstance().updateExternalReferenceIdAndVersion(entity, moduleComboBox.getValue(), id, externalVersion);
					} else {
						EntityDao.getInstance().updateExternalReferenceIdAndVersion(entity, moduleComboBox.getValue(), id, externalVersion - 1);					
					}
					dataProvider.refreshItem(EntityDao.getInstance().getEntity(typeParameterClass, entity.getSourceId()));
				});		
				return forceSyncButton;
			} else {
				return new Label("-");
			}
		})).setWidth("150px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Force Sync");

		footerLayout = new HorizontalLayout();
		footerLayout.setWidthFull();
		footerLayout.setJustifyContentMode(JustifyContentMode.END);
		countItemsSpan = new Span();
		footerLayout.add(new Text("Items: "), countItemsSpan);
		
		topBar.add(searchBar);
		topBar.add(filterSelect);
		topBar.add(moduleComboBox);
		topBar.add(refreshButton);
		topBar.setAlignItems(Alignment.BASELINE);
		add(topBar);
		add(genericGrid);
		add(footerLayout);

	}

	public abstract void addColumns(Grid<T> grid);

	protected abstract boolean enableforce(XRoadsModule module);
	
}
