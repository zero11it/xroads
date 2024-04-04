package it.zero11.xroads.ui.i18n;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class UITranslation {
	private static class SortedProperties extends Properties {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Enumeration keys() {
			Enumeration keysEnum = super.keys();
			Vector<String> keyList = new Vector<String>();
			while(keysEnum.hasMoreElements()){
				keyList.add((String)keysEnum.nextElement());
			}
			Collections.sort(keyList);
			return keyList.elements();
		}
	}
	public static final String ABSTRACT_COMPONENTS_BUTTON_CONFIRM = "abstractcomponents.button.confirm";
	public static final String ABSTRACT_COMPONENTS_BUTTON_CANCEL = "abstractcomponents.button.cancel";
	
	public static final String MARKUP_RULES_ACTIONS = "markup.rules.actions";
	public static final String MARKUP_RULES_MIN_BASE_PRICE = "markup.rules.min.base.price";
	public static final String MARKUP_RULES_MAX_BASE_PRICE = "markup.rules.max.base.price";
	public static final String MARKUP_RULES_MIN_BASE_PRICE_ERROR_MESSAGE = "markup.rules.min.base.price.error.message";
	public static final String MARKUP_RULES_MAX_BASE_PRICE_ERROR_MESSAGE = "markup.rules.max.base.price.error.message";
	public static final String MARKUP_RULES_ADD_BRAND = "markup.rules.add.brand";
	public static final String MARKUP_RULES_ADD_CATEGORY = "markup.rules.add.category";
	public static final String MARKUP_RULES_ADD_COLOR = "markup.rules.add.color";
	public static final String MARKUP_RULES_ADD_GENDER = "markup.rules.add.gender";
	public static final String MARKUP_RULES_ADD_NEW = "markup.rules.add.new";
	public static final String MARKUP_RULES_ADD_SEASON = "markup.rules.add.season";
	public static final String MARKUP_RULES_ADD_SUBCATEGORY = "markup.rules.add.subcategory";
	public static final String MARKUP_RULES_BASE_PRICE = "markup.rules.base.price";	
	public static final String MARKUP_RULES_CANCEL = "markup.rules.cancel";
	public static final String MARKUP_RULES_DEFAULT_RULE = "markup.rules.default.rule";
	public static final String MARKUP_RULES_DELETE = "markup.rules.delete";
	public static final String MARKUP_RULES_EDIT = "markup.rules.edit";
	public static final String MARKUP_RULES_EDIT_WINDOW_TITLE = "markup.rules.edit.window.title";
	public static final String MARKUP_RULES_EDIT_ERROR = "markup.rules.edit.conditions.error";
	public static final String MARKUP_RULES_EDIT_CONDITIONS = "markup.rules.edit.conditions";
	public static final String MARKUP_RULES_FIXED_AMOUNT = "markup.rules.fixed.amount";
	public static final String MARKUP_RULES_CREATE_WINDOW_TITLE = "markup.rules.create.window.title";
	public static final String MARKUP_RULES_INVALID_VALUE = "markup.rules.invalid.value";
	public static final String MARKUP_RULES_MARKUP = "markup.rules.markup";
	public static final String MARKUP_RULES_MIN_COST_MARKUP = "markup.rules.min.cost.markup";
	public static final String MARKUP_RULES_ROUNDING = "markup.rules.rounding";
	public static final String MARKUP_RULES_RULE = "markup.rules.rule";
	public static final String MARKUP_RULES_SAVE = "markup.rules.save";
	public static final String MARKUP_RULES_SAVE_ERROR = "markup.rules.save.error";
	public static final String MARKUP_RULES_PRICE_COST = "markup.rules.price.cost";
	public static final String MARKUP_RULES_PRICE_SUGGESTED = "markup.rules.price.suggested";
	public static final String MARKUP_RULES_PRICE_STREETPRICE = "markup.rules.price.streetprice";
	public static final String MARKUP_RULES_ROUND_NONE = "markup.rules.round.none";
	public static final String MARKUP_RULES_ROUND_000 = "markup.rules.round.000";
	public static final String MARKUP_RULES_ROUND_00000 = "markup.rules.round.00000";
	public static final String MARKUP_RULES_ROUND_000000 = "markup.rules.round.000000";
	public static final String MARKUP_RULES_ROUND_099 = "markup.rules.round.099";
	public static final String MARKUP_RULES_ROUND_900 = "markup.rules.round.900";
	public static final String MARKUP_RULES_ROUND_90000 = "markup.rules.round.90000";
	public static final String MARKUP_RULES_ROUND_900000 = "markup.rules.round.900000";
	public static final String MARKUP_RULES_ROUND_99000 = "markup.rules.round.99000";
	public static final String MARKUP_RULES_ROUND_990000 = "markup.rules.round.990000";
	public static final String MARKUP_RULES_ROUND_999 = "markup.rules.round.999";	

	public static void updateTranslation(String[] files, Class<?>[] clazzes) throws IOException, IllegalArgumentException, IllegalAccessException {
		for (String file : files) {
			FileInputStream in = new FileInputStream(file);
			SortedProperties props = new SortedProperties();
			props.load(in);
			in.close();
	
			boolean changed = false;
			for (Class<?> clazz : clazzes) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field f : fields) {
					if (Modifier.isStatic(f.getModifiers())) {
						if (!props.containsKey(f.get(null))) {
							props.setProperty((String) f.get(null), "");
							changed = true;
						}
					} 
				}
			}
	
			if (changed) {
				FileOutputStream out = new FileOutputStream(file);
				props.store(out, null);
				out.close();
			}
		}
	}
}
