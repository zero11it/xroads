package it.zero11.xroads.ui.view;

import java.util.List;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;

import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.ui.components.VerticalScrollLayout;
import it.zero11.xroads.ui.i18n.RewixUITranslation;
import it.zero11.xroads.ui.view.setting.SupplierLocaleConfigurationMultiLocalesSettings;
import it.zero11.xroads.ui.view.setting.TagMappingConfigurationSettings;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;

public abstract class AbstractSettingsView extends VerticalScrollLayout {
	private static final long serialVersionUID = 1L;

	private XRoadsRewixSourceModule xRoadsModule;
	private List<XRoadsRewixSourceModule> xRoadsModules;

	public AbstractSettingsView() {
		this.xRoadsModules = getXRoadsModules();
		this.xRoadsModule = xRoadsModules.get(0);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);

		setSpacing(true);
		setPadding(true);

		add(new H2(xRoadsModule.getConfiguration().getApiEndpoint()));

		HorizontalLayout topBar = new HorizontalLayout();
		topBar.setWidth("100%");
		
		Select<XRoadsRewixSourceModule> xRoadsModulesSelect = new Select<>();
		xRoadsModulesSelect.setItems(xRoadsModules);
		xRoadsModulesSelect.setItemLabelGenerator(XRoadsRewixSourceModule::getName);
		xRoadsModulesSelect.setTextRenderer(XRoadsRewixSourceModule::getName);
		xRoadsModulesSelect.addValueChangeListener((event) -> {
			this.xRoadsModule = event.getValue();
		});
		
		topBar.add(xRoadsModulesSelect);
		add(topBar);

//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_SUPPLIER_ACCOUNT), VaadinIcon.BUILDING.create(),
//				(event) -> {
//					Dialog w = new Dialog();
//					w.setWidth("450px");
//					w.add((Component) new WizardStepSupplierAccountConfiguration().buildStep(() -> {
//						w.close();
//					}, platform, false));
//					w.open();
//				}));
//
//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_CURRENCY_SETTING), VaadinIcon.EURO.create(),
//				(event) -> {
//					Dialog w = new Dialog();
//					w.setWidth("450px");
//					w.add(new WizardStepCurrecyRateConfiguration().buildStep(() -> {
//						w.close();
//					}, platform, false));
//					w.open();
//				}));
//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_ORDER_SETTING), VaadinIcon.FILE_TEXT.create(),
//				(event) -> {
//					Dialog w = new Dialog();
//					w.setWidth("450px");
//					w.add(new WizardStepOrderConfiguration().buildStep(() -> {
//						w.close();
//					}, platform, false));
//					w.open();
//				}));
//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_TARGET_GENERALSETTING), VaadinIcon.FILE_TEXT.create(),
//				(event) -> {
//					Dialog w = new Dialog();
//					w.setWidth("450px");
//					w.add(new WizardStepTargetGeneralSettingConfiguration().buildStep(() -> {
//						w.close();
//					}, platform, false));
//					w.open();
//				}));
//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_TARGET_ACCOUNT), VaadinIcon.FILE_TEXT.create(),
//				(event) -> {
//					Dialog w = new Dialog();
//					w.setWidth("450px");
//					w.add(new WizardStepTargetPlatformAccountConfiguration().buildStep(() -> {
//						w.close();
//					}, platform, false));
//					w.open();
//				}));
//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_TARGET_PLATFORMSETTING),
//				VaadinIcon.FILE_TEXT.create(), (event) -> {
//					Dialog w = new Dialog();
//					w.setWidth("450px");
//					w.add(new WizardStepTargetPlatformSettingConfiguration().buildStep(() -> {
//						w.close();
//					}, platform, false));
//					w.open();
//				}));
//
		add(new Button(getTranslation(RewixUITranslation.SETTINGS_LANGUAGE_SETTING), VaadinIcon.FLAG.create(),
				(event) -> {
					Dialog w = new Dialog();
					w.setWidth("450px");
					w.add(new SupplierLocaleConfigurationMultiLocalesSettings().buildUI(xRoadsModule, (p) -> {
						w.close();
					}));
					w.open();
				}));

		add(new Button(getTranslation(RewixUITranslation.SETTINGS_TAG_SETTING), VaadinIcon.TAGS.create(), (event) -> {
			Dialog w = new Dialog();
			w.setCloseOnOutsideClick(false);
			w.setCloseOnEsc(true);
			w.addDialogCloseActionListener(dialog -> {
				Dialog confirmDialog = new Dialog();
				confirmDialog.add(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_WARNING_MAPPING_EXIT,
						xRoadsModule.getConfiguration().getSupplierName()));
				HorizontalLayout buttonsLayout = new HorizontalLayout();
				buttonsLayout.getStyle().set("margin-top", "10px");
				buttonsLayout.add(new Button(
						getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_OK, xRoadsModule.getConfiguration().getSupplierName()),
						e -> {
							confirmDialog.close();
							w.close();
						}), new Button(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_TAG_CANCEL,
								xRoadsModule.getConfiguration().getSupplierName()), eCancel -> {
									confirmDialog.close();
								}));
				confirmDialog.add(buttonsLayout);
				confirmDialog.open();

			});
			w.setWidth("450px");
			w.add(new TagMappingConfigurationSettings().buildUI(xRoadsModule, (p) -> {
				w.close();
			}));
			w.open();
		}));

//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_FULL_SYNC), VaadinIcon.REFRESH.create(), (event) -> {
//			ConfirmWindow.askConfirm(getTranslation(RewixUITranslation.SETTINGS_FULL_SYNC),
//					getTranslation(RewixUITranslation.SETTINGS_FULL_SYNC_CONFIRMATION), (confirmEvent) -> {
//						ConfirmWindow.askConfirm(getTranslation(RewixUITranslation.SETTINGS_FULL_SYNC),
//								getTranslation(RewixUITranslation.SETTINGS_FULL_SYNC_CONFIRMATION2),
//								(confirmEvent2) -> {
//									try {
//										RewixSyncService.getInstance().forceFullSync(platform);
//
//										CronDao.getInstance().addSchedule(QuantitySyncCron.class.getSimpleName(),
//												platform.getTenant(), null, new Date(), false);
//										CronScheduler.get().checkNewCron();
//
//										UI.getCurrent().navigate(DashboardView.class);
//									} catch (Exception e) {
//										e.printStackTrace();
//										UIUtils.showErrorNotification(
//												getTranslation(RewixUITranslation.SETTINGS_FULL_SYNC_ERROR));
//									}
//								});
//					});
//		}));
//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_DELETE_SUPPLIER), VaadinIcon.TRASH.create(),
//				(event) -> {
//					if (ProductDao.getInstance().getRewixIds(platform).size() > 0) {
//						Notification.show(getTranslation(RewixUITranslation.SETTINGS_DELETE_SUPPLIER_ALERT)); // ,
//																												// Type.WARNING_MESSAGE);
//					} else {
//						ConfirmWindow.askConfirm(getTranslation(RewixUITranslation.SETTINGS_DELETE_SUPPLIER),
//								getTranslation(RewixUITranslation.SETTINGS_DELETE_SUPPLIER_CONFIRMATION),
//								(confirmEvent) -> {
//									try {
//										TenantDao.getInstance().deletePlatform(platform);
//										// If not platform left we create a new one
//										if (TenantDao.getInstance()
//												.getPlatforms(SessionUtils.getLoggedUser(VaadinSession.getCurrent()))
//												.size() == 0) {
//											TenantDao.getInstance().createNewPlatform(tenant);
//											UI.getCurrent().getPage().reload();
//										} else {
//											UI.getCurrent().navigate(DashboardView.class);
//										}
//									} catch (Exception e) {
//										e.printStackTrace();
//										UIUtils.showErrorNotification(
//												getTranslation(RewixUITranslation.SETTINGS_DELETE_SUPPLIER_ERROR));
//									}
//								});
//					}
//				}));
//
//		add(new Button(getTranslation(RewixUITranslation.SETTINGS_ADD_SUPPLIER), VaadinIcon.PLUS.create(), (event) -> {
//			ConfirmWindow.askConfirm(getTranslation(RewixUITranslation.SETTINGS_ADD_SUPPLIER),
//					getTranslation(RewixUITranslation.SETTINGS_ADD_SUPPLIER_CONFIRMATION), (confirmEvent) -> {
//						TenantDao.getInstance()
//								.createNewPlatform(SessionUtils.getLoggedUser(VaadinSession.getCurrent()));
//						UI.getCurrent().navigate(RewixWizardView.class);
//					});
//		}));

		xRoadsModulesSelect.setValue(this.xRoadsModule);

	}
	private List<XRoadsRewixSourceModule> getXRoadsModules() {
		return XRoadsCoreServiceBean.getInstance()
				.getEnabledModules(false)
				.values()
				.stream()
				.filter(XRoadsRewixSourceModule.class::isInstance)
				.map(XRoadsRewixSourceModule.class::cast)
				.toList();
	}

}
