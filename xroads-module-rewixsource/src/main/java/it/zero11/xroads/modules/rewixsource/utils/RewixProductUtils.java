package it.zero11.xroads.modules.rewixsource.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.model.LocaleValue;
import it.zero11.xroads.modules.rewixsource.api.model.ProductTag;
import it.zero11.xroads.modules.rewixsource.api.model.RewixModel;
import it.zero11.xroads.modules.rewixsource.api.model.RewixProduct;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixProductUtils {

	public static Map<String, List<String>> getTranslatedTags(String locale, RewixProduct rewixProduct) {
		return rewixProduct.getTags().stream()
				.collect(Collectors.toMap(pTag -> String.valueOf(pTag.getId()),
						pTag -> new ArrayList<String>(
								getTag(locale, rewixProduct.getTags(), pTag.getId(), rewixProduct.getCode()).values()),
						(existingList, newList) -> {
							existingList.addAll(newList);
							return existingList;
						}, HashMap::new));
	}

	public static Map<String, String> getTag(String locale, List<ProductTag> tags, int id, String sku) {
		Map<String, String> tagValue = new HashMap<String, String>();
		for (ProductTag tag : tags) {
			if (tag.getId() == id) {
				try {
					tagValue.putIfAbsent(tag.getValue().getValue(),
							getWithLocale(locale, tag.getValue().getLocaleValues()));
				} catch (Exception e) {
					tagValue.putIfAbsent(tag.getValue().getValue(), tag.getValue().getValue());
				}
			}
		}
		if (tagValue.isEmpty()) {
			throw new IllegalArgumentException("Missing tag " + id + " on sku: " + sku);
		}
		return tagValue;
	}

	public static String getWithLocale(String locale, List<LocaleValue> descriptions) {
		for (LocaleValue t:descriptions){
			if (t.getLocalecode().equals(locale))
				return t.getValue();
		}
		throw new IllegalArgumentException("Missing description");
	}

	public static String getXroadsProductSourceId(RewixProduct rewixProduct, XRoadsRewixSourceModule xRoadsModule) {
		return xRoadsModule.getConfiguration().getPrefixSourceId() + rewixProduct.getId();
	}

	public static Integer getRewixProductId(Product product, XRoadsRewixSourceModule xRoadsModule) {
		return Integer.valueOf(product.getSourceId().replace(xRoadsModule.getConfiguration().getPrefixSourceId(), ""));
	}

	public static Product getXRoadsBaseStructureProductFromRewixProduct(RewixProduct rewixProduct,
			XRoadsRewixSourceModule xRoadsModule, boolean forceOffline) {
		Product product = XRoadsUtils.getV2ProductInstance();
		Map<String, String> vatClassMap = xRoadsModule.getConfiguration().getVatClassMap();

		product.setSourceId(getXroadsProductSourceId(rewixProduct, xRoadsModule));
		product.setBrand(rewixProduct.getBrand() != null ? rewixProduct.getBrand() : "");
		product.setSku(rewixProduct.getCode());
		product.setName(rewixProduct.getName());
		product.setCost(rewixProduct.getBestTaxable());
		product.setVirtual(false);
		product.setOnline(forceOffline ? false : rewixProduct.isOnline());

		((ObjectNode) product.getData()).put(XRoadsJsonKeys.REWIX_PRODUCT_VAT_CLASS_KEY,
				vatClassMap.get(rewixProduct.getVatClassId().toString()));
		return product;
	}

	public static boolean isProuctOfThisModule(Product xRoadsProduct, XRoadsRewixSourceModule xRoadsModule) {
		return xRoadsProduct.getSourceId().startsWith(xRoadsModule.getConfiguration().getPrefixSourceId());
	}

	public static boolean isModelOfThisModule(Model xRoadsModel, XRoadsRewixSourceModule xRoadsModule) {
		return xRoadsModel.getSourceId().startsWith(xRoadsModule.getConfiguration().getPrefixSourceId());
	}

	public static String getXroadsModelSourceId(RewixModel rewixModel, XRoadsRewixSourceModule xRoadsModule) {
		return xRoadsModule.getConfiguration().getPrefixSourceId() + rewixModel.getId();
	}

	public static String getXroadsModelSku(RewixModel rewixModel, XRoadsRewixSourceModule xRoadsModule) {
		return xRoadsModule.getConfiguration().getPrefixSourceId() + rewixModel.getCode();
	}

	public static Integer getRewixModelId(Model model, XRoadsRewixSourceModule xRoadsModule) {
		return  Integer.valueOf(StringUtils.removeStart(model.getSourceId(), xRoadsModule.getConfiguration().getPrefixSourceId()));
	}

}
