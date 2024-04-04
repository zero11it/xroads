package it.zero11.xroads.ui.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid.Column;

import it.zero11.xroads.ui.i18n.UITranslation;
import it.zero11.xroads.utils.modules.core.model.MarkupRoundType;

public class XRoadsUIUtils {

	public static <T> List<Column<T>> orderVaadinColumnsList(SortedMap<Integer, Column<T>> columnsToAdd,
			List<Column<T>> columns) {
		if (columnsToAdd.size() == 0) {
			return columns;
		}
		List<Column<T>> orderedColumnList = new ArrayList<>();
		int addedElements = 0;
		for (int i = 0; i < columns.size() + columnsToAdd.size(); i++) {
			if (columnsToAdd.containsKey(i)) {
				orderedColumnList.add(i, columnsToAdd.get(i));
				addedElements++;
			} else {
				orderedColumnList.add(i, columns.get(i - addedElements));
			}
		}
		return orderedColumnList;
	}

//	public static String getMarkupPriceTypeLabel(MarkupPriceType markupPriceType) {
//		UI ui = UI.getCurrent();
//		return switch (markupPriceType) {
//		case Cost -> ui.getTranslation(UITranslation.MARKUP_RULES_PRICE_COST);
//		case StreetPrice -> ui.getTranslation(UITranslation.MARKUP_RULES_PRICE_STREETPRICE);
//		case Suggested -> ui.getTranslation(UITranslation.MARKUP_RULES_PRICE_SUGGESTED);
//		};
//	}

	public static String getMarkupRoundTypeLabel(MarkupRoundType markupRoundType) {
		UI ui = UI.getCurrent();
		return switch (markupRoundType) {
		case None -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_NONE);
		case Round000 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_000);
		case Round00000 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_00000);
		case Round000000 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_000000);
		case Round099 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_099);
		case Round900 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_900);
		case Round90000 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_90000);
		case Round900000 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_900000);
		case Round99000 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_99000);
		case Round990000 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_990000);
		case Round999 -> ui.getTranslation(UITranslation.MARKUP_RULES_ROUND_999);
		};
	}
}
