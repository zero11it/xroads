package it.zero11.xroads.utils.modules.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.utils.modules.core.model.MarkupRoundType;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleBean;

public class MarkupRuleUtils {

	public static final String MIN_BASE_PRICE_KEY = "minBasePrice";
	public static final String MAX_BASE_PRICE_KEY = "maxBasePrice";
	
	/**
	 * Decides if a rule can be applied to rewix product. All tags defined by tule must be present in product
	 * to have matching.
	 * 
	 * @param rule
	 * @param product
	 * @return
	 */
	private static boolean match(XRoadsModule xRoadsModule, MarkupRuleBean rule,
			Map<String, BigDecimal> markupPriceTypToPriceeMap, Map<String, String> tagsValueMap) {

		if (rule.getProperties() == null)
			return true;

		for (Iterator<Entry<String, JsonNode>> iterator = rule.getProperties().fields(); iterator.hasNext();) {
			Entry<String, JsonNode> field = iterator.next();
			String key = field.getKey();			
			
			if (key.equals(MIN_BASE_PRICE_KEY)) {
				BigDecimal basePrice = getBaseProductPriceByMarkupPriceType(markupPriceTypToPriceeMap, rule.getBasePriceType());
				BigDecimal minBasePrice = new BigDecimal(field.getValue().asText());
				if(basePrice.compareTo(minBasePrice) < 1)
					return false;
			} else if(key.equals(MAX_BASE_PRICE_KEY)) {
				BigDecimal basePrice = getBaseProductPriceByMarkupPriceType(markupPriceTypToPriceeMap, rule.getBasePriceType());
				BigDecimal maxBasePrice = new BigDecimal(field.getValue().asText());
				if(basePrice.compareTo(maxBasePrice) > 0)
					return false;
			} else {
				boolean found = false;
				Iterator<JsonNode> valueIterator = field.getValue().elements();
				while (valueIterator.hasNext()) {
					JsonNode value = valueIterator.next();
					try {
						List<String> candidates = tagsValueMap.get(key);
						if (candidates != null) {
							for (String candidate : candidates) {
								if (value.asText().equals(candidate))
									found = true;
							}
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}

				if (!found)
					return false;
			}
		}

		return true;
	}

	private static BigDecimal applyRule(XRoadsModule xRoadsModule, Map<String, BigDecimal> markupPriceTypToPriceeMap,
			String  minMarkupPriceType, MarkupRuleBean rule, BigDecimal exchangeRate) {
		
		BigDecimal basePrice = getBaseProductPriceByMarkupPriceType(markupPriceTypToPriceeMap, rule.getBasePriceType());
		if(basePrice == null) {
			return BigDecimal.ZERO;
		}
		
		final BigDecimal percent = new BigDecimal("100.00");
		BigDecimal price = basePrice.add(basePrice.multiply(rule.getMarkupPercentage().divide(percent)));
		price = price.multiply(exchangeRate);
		
		BigDecimal floor;
		if (rule.getMinCostMarkupPercentage() == null) {
			floor = price;
		} else {
			floor =  markupPriceTypToPriceeMap.get(minMarkupPriceType);
			floor = floor.add( floor.multiply(rule.getMinCostMarkupPercentage().divide(percent)));
			floor = floor.multiply(exchangeRate);
		}
		
		if (rule.getMarkupFixed() != null) {
			price = price.add(rule.getMarkupFixed());
			//we do not apply negative markup fixed otherwise we may generate negative prices
			if (rule.getMarkupFixed().compareTo(BigDecimal.ZERO) > 0) {
				floor = floor.add(rule.getMarkupFixed());
			}
		}
		price = round(price, rule.getRoundType(), true);
		if (price.compareTo(floor) < 0)
			price = round(floor, rule.getRoundType(), false);
				
		return price;
	}
	
	public static BigDecimal applyRule(XRoadsModule xRoadsModule, SortedSet<MarkupRuleBean> rules,
			Map<String, BigDecimal> markupPriceTypToPriceeMap, String minMarkupPriceType,
			Map<String, String> tagsValueMap, BigDecimal exchangeRate) {
		try{
			final MarkupRuleBean rule = selectMarkupRule(xRoadsModule, rules, markupPriceTypToPriceeMap, tagsValueMap);
			return applyRule(xRoadsModule, markupPriceTypToPriceeMap, minMarkupPriceType, rule, exchangeRate);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static MarkupRuleBean selectMarkupRule(XRoadsModule xRoadsModule, SortedSet<MarkupRuleBean> rules,
			Map<String, BigDecimal> markupPriceTypToPriceeMap, Map<String, String> tagsValueMap) {
		for (MarkupRuleBean markupRule : rules) {
			if (MarkupRuleUtils.match(xRoadsModule, markupRule, markupPriceTypToPriceeMap, tagsValueMap))
				return markupRule;
		} 
		
		return null;
	}
	
	private static BigDecimal round(BigDecimal value, MarkupRoundType type, boolean down) {
		switch (type) {
		case Round000 -> {
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.setScale(0, RoundingMode.UP);
		}
		case Round099 -> {
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP).subtract(new BigDecimal("0.01"));
			else
				value = value.add(new BigDecimal("0.01")).setScale(0, RoundingMode.UP).subtract(new BigDecimal("0.01"));
		}
		case Round900 -> {
			value = value.movePointLeft(1);
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.setScale(0, RoundingMode.UP);

			value = value.movePointRight(1);
			value = value.subtract(BigDecimal.ONE);
		}
		case Round999 -> {
			value = value.movePointLeft(1);
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.add(new BigDecimal("0.01")).setScale(0, RoundingMode.UP);
			value = value.movePointRight(1);
			value = value.subtract(new BigDecimal("0.01"));
		}
		case Round00000 -> {
			value = value.movePointLeft(3);
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.setScale(0, RoundingMode.UP);
			value = value.movePointRight(3);
		}
		case Round99000 -> {
			value = value.movePointLeft(3);
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.setScale(0, RoundingMode.UP);
			value = value.movePointRight(2);
			value = value.subtract(BigDecimal.ONE);
			value = value.movePointRight(1);
		}
		case Round90000 -> {
			value = value.movePointLeft(3);
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.setScale(0, RoundingMode.UP);

			value = value.movePointRight(1);
			value = value.subtract(BigDecimal.ONE);
			value = value.movePointRight(2);
		}
		case Round000000 -> {
			value = value.movePointLeft(4);
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.setScale(0, RoundingMode.UP);
			value = value.movePointRight(4);
		}
		case Round990000 -> {
			value = value.movePointLeft(4);
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.setScale(0, RoundingMode.UP);
			value = value.movePointRight(2);
			value = value.subtract(BigDecimal.ONE);
			value = value.movePointRight(2);
		}
		case Round900000 -> {
			value = value.movePointLeft(4);
			if (down)
				value = value.setScale(0, RoundingMode.HALF_UP);
			else
				value = value.setScale(0, RoundingMode.UP);

			value = value.movePointRight(1);
			value = value.subtract(BigDecimal.ONE);
			value = value.movePointRight(3);
		}
		case None -> {
			if (down)
				value = value.setScale(2, RoundingMode.HALF_UP);
			else
				value = value.setScale(2, RoundingMode.UP);
		}
		}

		return value.max(BigDecimal.ZERO);
	}
	
	private static BigDecimal getBaseProductPriceByMarkupPriceType(Map<String, BigDecimal> markupPriceTypToPriceeMap, String markupPriceType) {
		BigDecimal basePrice = markupPriceTypToPriceeMap.get(markupPriceType);
		return basePrice;
	}
	
}
