package it.zero11.xroads.ui.utils;

import com.vaadin.flow.component.UI;

import it.zero11.xroads.modules.rewixsource.model.RewixMarkupPriceType;
import it.zero11.xroads.ui.i18n.UITranslation;

public class RewixUIUtils {

	public static String getMarkupPriceTypeLabel(RewixMarkupPriceType markupPriceType) {
		UI ui = UI.getCurrent();
		return switch (markupPriceType) {
		case Cost -> ui.getTranslation(UITranslation.MARKUP_RULES_PRICE_COST);
		case StreetPrice -> ui.getTranslation(UITranslation.MARKUP_RULES_PRICE_STREETPRICE);
		case Suggested -> ui.getTranslation(UITranslation.MARKUP_RULES_PRICE_SUGGESTED);
		};
	}

}
