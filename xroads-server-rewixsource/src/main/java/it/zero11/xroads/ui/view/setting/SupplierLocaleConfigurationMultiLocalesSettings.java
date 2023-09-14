package it.zero11.xroads.ui.view.setting;

import java.io.IOException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.RewixAPI;
import it.zero11.xroads.modules.rewixsource.api.UnauthorizedException;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.ui.i18n.RewixUITranslation;
import it.zero11.xroads.ui.utils.NotificationUtils;
import it.zero11.xroads.ui.utils.NotificationUtils.NotificationType;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;

public class SupplierLocaleConfigurationMultiLocalesSettings extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private Select<String> languageComboBox;
	private Map<String, String> langSupplierMap;
	private Map<String, Select<String>> localesSelect;
	private FormLayout localesLayout = new FormLayout();
	private JSONObject configurationLocalesJson;
	private RewixAPI rewixApi = null;
	private XRoadsRewixSourceModule xRoadsModule = null;
	private Set<String> rewixLocalesImport;
	
	public SupplierLocaleConfigurationMultiLocalesSettings buildUI(XRoadsRewixSourceModule xRoadsModule,  Consumer<Void> onSaveAction) {
		this.xRoadsModule = xRoadsModule;
		rewixApi = new RewixAPI(xRoadsModule);
		localesSelect = new HashMap<String, Select<String>>();
		
		removeAll();
		setWidth("100%");
		setSpacing(true);

		H2 title=new H2(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_LOCALE_TITLE));
		add(title);
		setHorizontalComponentAlignment(Alignment.CENTER, title);
		Paragraph text = new Paragraph(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_LOCALE_INITIAL_NOTES,
				xRoadsModule.getConfiguration().getSupplierName()));
		add(text);

		add(new H3(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_LOCALE_SUBTITLE)));


		configurationLocalesJson = getConfigurationLocales();

		try {
			langSupplierMap = rewixApi.getSupplierLocales();
			rewixApi.getTargetLocales().forEach(
					(k, v) -> {
						languageComboBox = new Select<>();
						languageComboBox.setItemLabelGenerator((lang) -> langSupplierMap.get(lang));
						languageComboBox.setTextRenderer((lang) -> langSupplierMap.get(lang));
						languageComboBox.setMinWidth("200px");
						languageComboBox.setItems(langSupplierMap.keySet());
						languageComboBox.setLabel(v);
						if(configurationLocalesJson != null) {
							try {
								languageComboBox.setValue(configurationLocalesJson.get(k) != null ? configurationLocalesJson.get(k).toString() : "");
							} catch (JSONException  e) {
							}
						}
						localesSelect.put(k, languageComboBox);
						localesLayout.add(languageComboBox);
					}
					);
			add(localesLayout);
			
			add(new Button(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_LOCALE_ACTION_SAVE), e -> {
				completeStep(onSaveAction);
			}));
		} catch (NoSuchAlgorithmException | UnauthorizedException | IOException e) {
			add(new Label("An error occurred during one or more requests. Please try again later."));
		}

		return this;
	}

	public void completeStep(Consumer<Void> function) {
		Map<String, String> localesMap = new HashMap<String, String>();
		rewixLocalesImport = new LinkedHashSet<String>();		
		Boolean isValid = true;
		for(String key : localesSelect.keySet()) 
		{
			Select<String> component = localesSelect.get(key);
			component.setInvalid(false);
			if (component.getValue() == null)
			{
				component.setErrorMessage(getTranslation(RewixUITranslation.INITIAL_WIZARD_STEP_LOCALE_ERROR_LANGUAGE_REQUIRED));
				component.setInvalid(true);
				isValid = false;
				break;
			}
			else 
			{
				localesMap.put(key, component.getValue());
				rewixLocalesImport.add(component.getValue());
			}
		}
		if(isValid && localesSelect.size()>0)
		{
			try {
				String  rewixLocalesImportString = rewixLocalesImport.toString().replaceAll("\\s+","");
				ParamDao.getInstance().updateParam(xRoadsModule, RewixSourceParamType.REWIX_LOCALE, rewixLocalesImportString.substring(1, rewixLocalesImportString.length() - 1) );
				saveImportTagMapping(localesMap);
				function.accept(null);
			}catch (IllegalArgumentException e) {
				NotificationUtils.show(NotificationType.ERROR, e.getMessage());
			}
		}
	}

	public Map<String, String> getTargetLocales() throws ConnectException, NoSuchAlgorithmException {
		return Map.of();
	}

	public void saveImportTagMapping(Map<String, String> localesImportMapping) {
		JSONObject localesimportMapping = new JSONObject(localesImportMapping);
		ParamDao.getInstance().updateParam(xRoadsModule, RewixSourceParamType.LOCALE_MAP, localesimportMapping.toString());
	}

	public JSONObject getConfigurationLocales() {
		String configuration = ParamDao.getInstance().getParameter(xRoadsModule, RewixSourceParamType.LOCALE_MAP);
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
