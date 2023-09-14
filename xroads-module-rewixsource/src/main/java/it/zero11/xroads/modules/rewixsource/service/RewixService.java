package it.zero11.xroads.modules.rewixsource.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.RewixAPI;
import it.zero11.xroads.modules.rewixsource.api.UnauthorizedException;
import it.zero11.xroads.modules.rewixsource.api.model.PageData;
import it.zero11.xroads.modules.rewixsource.api.model.ProductTag;
import it.zero11.xroads.modules.rewixsource.api.model.RewixProduct;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.modules.rewixsource.utils.RewixProductUtils;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.utils.LocalCache;
import it.zero11.xroads.utils.modules.core.utils.LocalCache.LocalCacheType;
import it.zero11.xroads.utils.modules.core.utils.LocalCache.TTL;
import it.zero11.xroads.utils.modules.core.utils.LocalCache.UpdateMode;

public class RewixService {

	private static final Logger log = Logger.getLogger(RewixService.class);

	public List<RewixProduct> getProducts(XRoadsRewixSourceModule xRoadsModule, Map<String, List<String>> filterNode, String filterString) throws IOException, UnauthorizedException {
		PageData pageData = new RewixAPI(xRoadsModule).getCatalogForPlatformAndParse();
		String locale = xRoadsModule.getConfiguration().getLocale();
		List<RewixProduct> products = new ArrayList<>();
		if ((filterNode == null || filterNode.size() == 0) && (filterString == null || filterString.trim().isEmpty())){
			products.addAll(pageData.getItems());
		}else{
			filterString = filterString.toLowerCase();
			for (RewixProduct product:pageData.getItems()){
				if(product.getCode().toLowerCase().contains(filterString) || product.getName().toLowerCase().contains(filterString)) {
					if (productMatchTagsMap(product, filterNode, locale, -1)){
						products.add(product);
					}
				}
			}
		}
		return products;
	}

	private boolean productMatchTagsMap(RewixProduct product, Map<String, List<String>> filterNode, String locale,
			int tagId) {
		try {
			for (Map.Entry<String, List<String>> entry : filterNode.entrySet()) {
				Integer tagIdInt = Integer.valueOf(entry.getKey());
				List<String> tagValues = new ArrayList<String>(
						RewixProductUtils.getTag(locale, product.getTags(), tagIdInt, product.getCode()).values());
				List<String> values = entry.getValue();

				if (!tagValues.stream().filter(tagValue -> (values.contains(tagValue)) && tagIdInt != tagId).findAny()
						.isPresent()) {
					return false;
				}
			}
			;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Map<String, String> getTagNames(XRoadsRewixSourceModule xRoadsModule){
		return LocalCache.getInstance().getOrGenerate(LocalCache.buildKey(LocalCacheType.TAGNAMES, xRoadsModule.getName(), ""), UpdateMode.CALLER_THREAD, TTL.LONG, ()->{
			try{
				PageData products = new RewixAPI(xRoadsModule).getCatalogForPlatformAndParse();
				Map<String, String> values = new TreeMap<>();
				for (RewixProduct product:products.getItems()){
					if (product.getTags() != null) {
						for (ProductTag tag : product.getTags()) {
							if (tag.getLocaleValues() != null && tag.getLocaleValues().size() > 0) {
								values.putIfAbsent(String.valueOf(tag.getId()), tag.getLocaleValues().get(0).getValue());
							}
						}
					}
				}
				return values;
			}catch (Exception e) {
				return new TreeMap<String, String>();
			}
		});
	}

	public Map<String, String> getTagValues(XRoadsRewixSourceModule xRoadsModule, String tagId){
		return LocalCache.getInstance().getOrGenerate(LocalCache.buildKey(LocalCacheType.TAG, xRoadsModule.getName(), tagId), UpdateMode.CALLER_THREAD, TTL.LONG, ()->{
			try{
				String locale = ParamDao.getInstance().getParameter(xRoadsModule, RewixSourceParamType.REWIX_LOCALE);
				PageData products = new RewixAPI(xRoadsModule).getCatalogForPlatformAndParse();
				Map<String, String> values = new TreeMap<>();
				for (RewixProduct product:products.getItems()){
					if (product.getTags() != null) {
						try{
							Map<String, String> tagValues = RewixProductUtils.getTag(locale, product.getTags(), Integer.valueOf(tagId), product.getCode());
							tagValues.forEach((tagValue, translatedTagValue) -> {
								values.putIfAbsent(tagValue, translatedTagValue);
							});
						}catch (Exception e) {
						}
					}
				}
				return values;
			}catch (Exception e) {
				return new TreeMap<String, String>();
			}
		});
	}

	public Map<String, String> getFilteredTagValues(XRoadsRewixSourceModule xRoadsModule, String tagId, Map<String, List<String>> filterNode){
			try{
				PageData products = new RewixAPI(xRoadsModule).getCatalogForPlatformAndParse();
				String locale = xRoadsModule.getConfiguration().getLocale();
				Map<String, String> values = new TreeMap<>();
				int tagIdInt = Integer.valueOf(tagId).intValue();
				for (RewixProduct product:products.getItems()){
					if (product.getTags() != null) {
						try{
							if(productMatchTagsMap(product, filterNode, locale, tagIdInt)) {
								Map<String, String> tagValues = RewixProductUtils.getTag(locale, product.getTags(), tagIdInt, product.getCode());
								tagValues.forEach((tagValue, translatedTagValue) -> {
									values.putIfAbsent(tagValue, translatedTagValue);
								});
							}
						}catch (Exception e) {
						}
					}
				}
				return values;
			}catch (Exception e) {
				return new TreeMap<String, String>();
			}
	}

}