package it.zero11.xroads.modules.rewixsource.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.Price;
import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.model.RewixModel;
import it.zero11.xroads.modules.rewixsource.api.model.RewixProduct;
import it.zero11.xroads.modules.rewixsource.model.RewixMarkupPriceType;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleBean;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleType;
import it.zero11.xroads.utils.modules.core.service.MarkupRuleService;
import it.zero11.xroads.utils.modules.core.utils.MarkupRuleUtils;

public class RewixPriceUtils {

	public static List<Price> getXRoadsPricesFromRewixProduct(XRoadsRewixSourceModule xRoadsModule, RewixProduct rewixProduct) throws SyncException {
		MarkupRuleService<XRoadsRewixSourceModule> markupService = new MarkupRuleService<XRoadsRewixSourceModule>();
		BigDecimal exchangeRate = ParamDao.getInstance().getParameterAsBigDecimal(xRoadsModule, RewixSourceParamType.REWIX_CURRENCY_CONVERSION);
		if (exchangeRate == null)
			exchangeRate = new BigDecimal("1.0");
		
		String locale = ParamDao.getInstance().getParameter(xRoadsModule, RewixSourceParamType.REWIX_LOCALE);
		Map<String, BigDecimal> productMarkupPriceTypeToPriceeMap = getMarkupPriceTypeToPriceeMap(xRoadsModule, rewixProduct);
		Map<String, List<String>> translatedTags = RewixProductUtils.getTranslatedTags(locale, rewixProduct);
		
		// rules configuration list
		SortedSet<MarkupRuleBean> taxableRules = 
				markupService.getMarkupByModuleAndRuleType(xRoadsModule, MarkupRuleType.TAXABLE, RewixSourceParamType.MARKUP_RULES);
		SortedSet<MarkupRuleBean> retailRules = 
				markupService.getMarkupByModuleAndRuleType(xRoadsModule, MarkupRuleType.RETAIL, RewixSourceParamType.MARKUP_RULES);
		SortedSet<MarkupRuleBean> suggestedRules = 
				markupService.getMarkupByModuleAndRuleType(xRoadsModule, MarkupRuleType.SUGGESTED, RewixSourceParamType.MARKUP_RULES);

		List<Price> prices = new ArrayList<>();
		List<String> platforms = xRoadsModule.getConfiguration().getListingPlatforms();
		if (platforms.size() > 0) {
			String productSourceId = RewixProductUtils.getXroadsProductSourceId(rewixProduct, xRoadsModule);
			for (String platform : platforms) {
				// product prices
				BigDecimal price = getRewixPrice(xRoadsModule, taxableRules, productMarkupPriceTypeToPriceeMap, translatedTags, exchangeRate);
				BigDecimal retailPrice = getRewixPrice(xRoadsModule, retailRules, productMarkupPriceTypeToPriceeMap, translatedTags, exchangeRate);
				BigDecimal suggestedPrice = getRewixPrice(xRoadsModule, suggestedRules, productMarkupPriceTypeToPriceeMap, translatedTags, exchangeRate);
				// xroads product price
				Price xRoadsPrice = XRoadsUtils.getPriceInstance();
				xRoadsPrice.setSourceId(productSourceId + "-" + platform);
				xRoadsPrice.setProductSourceId(productSourceId);
				xRoadsPrice.setRetailPrice(retailPrice);
				xRoadsPrice.setSellPrice(price);
				xRoadsPrice.setSuggestedPrice(suggestedPrice);
				xRoadsPrice.setBuyPrice(rewixProduct.getBestTaxable());
				xRoadsPrice.setMerchantCode(xRoadsModule.getConfiguration().getMerchantCode());
				xRoadsPrice.setData(XRoadsUtils.OBJECT_MAPPER.createObjectNode().put(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY, platform));
				prices.add(xRoadsPrice);

				// models price
				for(RewixModel rewixModel : rewixProduct.getModels()) {
					Map<String, BigDecimal> modelMarkupPriceTypeToPriceeMap = getMarkupPriceTypeToPriceeMap(xRoadsModule, rewixModel);
					// model prices
					BigDecimal modelPrice = getRewixPrice(xRoadsModule, taxableRules, modelMarkupPriceTypeToPriceeMap, translatedTags, exchangeRate);
					BigDecimal modelRetailPrice = getRewixPrice(xRoadsModule, retailRules, modelMarkupPriceTypeToPriceeMap, translatedTags, exchangeRate);
					BigDecimal modelSuggestedPrice = getRewixPrice(xRoadsModule, suggestedRules, modelMarkupPriceTypeToPriceeMap, translatedTags, exchangeRate);
					// xroads model price
					Price xRoadsModelPrice = XRoadsUtils.getPriceInstance();
					xRoadsModelPrice.setSourceId(productSourceId + "-" + platform + "-" + rewixModel.getOption1() + "-" + rewixModel.getOption2());
					xRoadsModelPrice.setProductSourceId(productSourceId);
					xRoadsModelPrice.setRetailPrice(modelRetailPrice);
					xRoadsModelPrice.setSellPrice(modelPrice);
					xRoadsModelPrice.setSuggestedPrice(modelSuggestedPrice);
					xRoadsModelPrice.setBuyPrice(rewixProduct.getBestTaxable());
					xRoadsModelPrice.setMerchantCode(xRoadsModule.getConfiguration().getMerchantCode());
					if(rewixModel.getOption1() != null && !rewixModel.getOption1().isEmpty()) {
						((ObjectNode) xRoadsModelPrice.getData()).put("option1", rewixModel.getOption1());
					}
					if(rewixModel.getOption2() != null && !rewixModel.getOption2().isEmpty()) {
						((ObjectNode) xRoadsModelPrice.getData()).put("option2", rewixModel.getOption2());
					}
					((ObjectNode) xRoadsModelPrice.getData()).put(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY, platform);
					((ObjectNode) xRoadsModelPrice.getData()).put(XRoadsJsonKeys.REWIX_PRICE_PRIORITY_KEY, -1);
					prices.add(xRoadsModelPrice);
				}
			}
		}
		return prices;
	}

	private static BigDecimal getRewixPrice(XRoadsRewixSourceModule xRoadsModule, SortedSet<MarkupRuleBean> markupRules,
			Map<String, BigDecimal> markupPriceTypeToPriceeMap, Map<String, List<String>> translatedTags, BigDecimal exchangeRate) {
		BigDecimal price = null;
		if(markupRules != null && markupRules.size() > 0) {
			price = MarkupRuleUtils.applyRule(xRoadsModule, markupRules, markupPriceTypeToPriceeMap, "BestTaxable", translatedTags, exchangeRate);
		}
		return price;
	}

	public static Map<String, BigDecimal> getMarkupPriceTypeToPriceeMap(XRoadsRewixSourceModule xRoadsModule, RewixProduct rewixProduct) {
		return getMarkupPriceTypeToPriceeMap(xRoadsModule, rewixProduct.getStreetPrice(), rewixProduct.getSuggestedPrice(),
				rewixProduct.getTaxable(), rewixProduct.getBestTaxable());
	}

	public static Map<String, BigDecimal> getMarkupPriceTypeToPriceeMap(XRoadsRewixSourceModule xRoadsModule, RewixModel rewixModel) {
		return getMarkupPriceTypeToPriceeMap(xRoadsModule, rewixModel.getStreetPrice(), rewixModel.getSuggestedPrice(),
				rewixModel.getTaxable(), rewixModel.getBestTaxable());
	}
	
	private static Map<String, BigDecimal> getMarkupPriceTypeToPriceeMap(XRoadsRewixSourceModule xRoadsModule, BigDecimal streetPrice, 
			BigDecimal suggestedPrice, BigDecimal taxable, BigDecimal bestTaxable) {
		Map<String, BigDecimal> markupPriceTypeToPriceeMap = new HashMap<String, BigDecimal>();
		markupPriceTypeToPriceeMap.put(RewixMarkupPriceType.Cost.value, bestTaxable);
		markupPriceTypeToPriceeMap.put(RewixMarkupPriceType.StreetPrice.value, streetPrice);
		if (ParamDao.getInstance().getParameterAsBoolean(xRoadsModule, RewixSourceParamType.MIN_MARKUP_BASED_ON_BEST_TAXABLE)) {
			markupPriceTypeToPriceeMap.put("BestTaxable", bestTaxable);
		} else {
			markupPriceTypeToPriceeMap.put("BestTaxable", taxable);
		}
		if (suggestedPrice != null && suggestedPrice.compareTo(BigDecimal.ZERO) > 0){
			markupPriceTypeToPriceeMap.put(RewixMarkupPriceType.Suggested.value, suggestedPrice);
		}else{
			markupPriceTypeToPriceeMap.put(RewixMarkupPriceType.Suggested.value, streetPrice);
		}
		
		return markupPriceTypeToPriceeMap;
	}

}
