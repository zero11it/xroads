package it.zero11.xroads.modules.rewix.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.ModelRevision;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.ProductNotFoundException;
import it.zero11.xroads.modules.rewix.api.RewixAPIException;
import it.zero11.xroads.modules.rewix.api.model.ProductModelBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelUpdateProductBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagMetaBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagMetasBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTranslationBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTranslationsBean;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixModelConsumer extends AbstractRewixConsumer implements EntityConsumer<Model> {

	public RewixModelConsumer(XRoadsRewixModule xRoadsModule) {
		super(xRoadsModule);
	}

	@Override
	public void consume(Model model) throws SyncException {
		log.info("Checking parent product for rewix model " + model.getSku());
		checkModelOwner(model);

		log.info("Updating rewix model " + model.getSku());

		Product product = getXRoadsModule().getXRoadsCoreService().getEntity(Product.class, model.getProductSourceId());
		if (product == null) {
			throw new SyncException("Missing parent product");
		}			

		final String productId = XRoadsUtils.getExternalReferenceId(product, xRoadsModule);
		if (productId == null) {
			log.debug("Model "+ model.getSku() + "'s product is not yet on rewix");
			throw new SyncException("Product not imported in rewix");
		}
		int rewixProductId = Integer.parseInt(productId);

		final String modelId = XRoadsUtils.getExternalReferenceId(model, xRoadsModule);
		Integer rewixModelId = null;
		if (modelId != null) {
			rewixModelId = Integer.parseInt(modelId);
		}

		rewixModelId = updateModelHead(rewixProductId, rewixModelId, product, model);		

		log.debug("Updating rewix model external reference " + model.getSku() + " --> " + rewixModelId);
		getXRoadsModule().getXRoadsCoreService().updateExternalReferenceIdAndVersion(xRoadsModule, model, Integer.toString(rewixModelId), -1);

		log.debug("Updating rewix model translations " + model.getSku());
		updateModelTranslations(rewixProductId, "size", model);

		log.debug("Updating rewix model translations " + model.getSku());
		updateModelTranslations(rewixProductId, "color", model);

		getXRoadsModule().getXRoadsCoreService().updateExternalReferenceId(xRoadsModule, model, Integer.toString(rewixModelId));
	}

	private void checkModelOwner(final Model model) throws RewixAPIException {
		if (XRoadsUtils.getExternalReferenceVersion(model, xRoadsModule) == null) {
			return;
		}

		final ModelRevision revision = getXRoadsModule().getXRoadsCoreService().getEntityRevision(ModelRevision.class,
				model.getSourceId(),
				XRoadsUtils.getExternalReferenceVersion(model, xRoadsModule));

		if (revision != null) {
			if (!model.getProductSourceId().equals(revision.getProductSourceId())) {
				Integer newProductId = getRewixProductId(model.getProductSourceId());			
				Integer oldProductId = getRewixProductId(revision.getProductSourceId());

				if (newProductId != null && oldProductId != null) {
					updateModelOwner(model, newProductId, oldProductId);	
				}
			}
		}
	}

	private Integer getRewixProductId(String sourceId) {
		Product product = getXRoadsModule().getXRoadsCoreService().getEntity(Product.class, sourceId);
		if (product == null) {
			return null;
		}

		final String productId = XRoadsUtils.getExternalReferenceId(product, xRoadsModule);
		if (productId == null) {
			return null;
		} else {
			return Integer.parseInt(productId);
		}
	}

	public void updateModelOwner(Model model, Integer newProductId, Integer oldProductId) throws RewixAPIException {
		ProductModelUpdateProductBean bean = new ProductModelUpdateProductBean();
		bean.setCurrentStockProductId(oldProductId);
		bean.setNewStockProductId(newProductId);

		Integer stockModelId = Integer.parseInt(XRoadsUtils.getExternalReferenceId(model, xRoadsModule));
		bean.setStockModelId(stockModelId);

		api.moveModelFromProduct(bean);
	}

	protected Integer updateModelHead(int rewixProductId, Integer rewixModelId, Product product, Model model) throws SyncException {
		log.debug("updateModelHead for model " + model.getSku());

		ProductModelBean rewixModel = new ProductModelBean();
		if (rewixModelId != null)
			rewixModel.setStockModelId(rewixModelId);
		rewixModel.setCode(model.getSku());
		rewixModel.setBarcode(model.getEan() == null ? "": model.getEan());
		rewixModel.setAdditionalBarcode(model.getAdditionalBarcode());
		rewixModel.setOption1(model.getOptions().path("size").asText(null));
		rewixModel.setOption2(model.getOptions().path("color").asText(null));
		rewixModel.setOption3(model.getOptions().path("option3").asText(null));
		rewixModel.setStockProductId(rewixProductId);
		rewixModel.setBackorder(model.getData().path(XRoadsJsonKeys.REWIX_MODEL_UNLIMITED_KEY).asBoolean());
		rewixModel.setPriority(model.getData().path(XRoadsJsonKeys.REWIX_MODEL_INDEX_KEY).asInt());
		rewixModel.setModelWeight(model.getWeight() != null ? model.getWeight().floatValue() : null);

		
		Integer merchantId;
		String merchantCode = model.getMerchantCode();
		if(merchantCode == null) {
			merchantId = null;
		} else {
			merchantId = xRoadsModule.getConfiguration().getMerchantMap().get(merchantCode);
			if(merchantId == null) {
				throw new SyncException("Merchant code " + merchantCode + " not in merchant map !");
			}
		}
		rewixModel.setMerchantId(merchantId);
		
		List<ProductTagBean> modelTags = new ArrayList<ProductTagBean>();

		{ // process model tags
			Map<String, Integer> configurationTagMap = getXRoadsModule().getConfiguration().getTagMap();
			Map<String, List<String>> tagMap = new HashMap<>();
			model.getTags().fields().forEachRemaining(tag -> {
				if (!tag.getKey().equals("translations") && !tag.getKey().equals("urlkeys")) {
					List<String> values = new ArrayList<>();
					JsonNode content = tag.getValue();
					if (content.isArray()) {
						content.elements().forEachRemaining(element -> values.add(element.asText()));
					} else  if(!content.isNull())
						values.add(content.asText());
					tagMap.put(tag.getKey(), values);
				}
			});
			if(configurationTagMap != null && configurationTagMap.size() > 0) {
				for(Map.Entry<String, Integer> configuratedTag : configurationTagMap.entrySet()) {
					ProductTagBean tag = new ProductTagBean();
					tag.setTagId(configuratedTag.getValue());
					List<String> tagValues = tagMap.get(configuratedTag.getKey());
					tag.setTagValues(tagValues);			
					modelTags.add(tag);
				}
				if(modelTags.size() > 0) {
					rewixModel.setModelTags(modelTags);
				}
			}
		}

		return api.updateProductVariant(rewixModel);
	}

	protected void updateModelTranslations(Integer rewixId, String type, Model model) throws RewixAPIException, ProductNotFoundException {
		log.debug("updateTranslations for model " + model.getSku());

		if (model.getOptions().path(type).asText() == null) {
			return;
		}

		JsonNode xtranslations = model.getOptions().path("translations");
		if (xtranslations == null) {
			return;
		}

		JsonNode translation = xtranslations.path(type); //size or color
		if (translation == null) {
			return;
		}

		Map<String, Map<String, String>> descriptionMap = new HashMap<>(); //LANG CODE -> PLATFORM:DESCRIPTION
		translation.fields().forEachRemaining(langCodeItem -> {
			Map<String, String> platforms = new HashMap<>();
			langCodeItem.getValue().fields().forEachRemaining(platform -> {
				platforms.put(platform.getKey(), langCodeItem.getValue().path(platform.getKey()).asText());
			});
			descriptionMap.put(langCodeItem.getKey(), platforms);
		});
		ProductTranslationsBean translations = new ProductTranslationsBean();
		translations.setType((type.equals("size") ? 1 : 2));
		translations.setValue(model.getOptions().path(type).asText());
		translations.setStockProductId(rewixId);

		if (descriptionMap.size() > 0) {	
			List<ProductTranslationBean> productTranslations = new ArrayList<>();
			for (String lang : descriptionMap.keySet()) {
				for (String platform : descriptionMap.get(lang).keySet()) {
					ProductTranslationBean translationBean = new ProductTranslationBean();
					translationBean.setLocaleCode(lang);
					translationBean.setTranslation(descriptionMap.get(lang).get(platform));
					//if (descriptionMap.get(lang).keySet().size() == 1)
					translationBean.setPlatformUid(platform);
					productTranslations.add(translationBean);
				}
			}
			translations.setProductTranslations(productTranslations);
			api.updateProductTranslations(translations);
		}
	}
	
	protected void updateModelTagTranslations(Integer rewixId, Model model) throws RewixAPIException {
		if (! model.getTags().has("translations")) {
			return;
		}

		//String[] platforms = product.getData().getString("platforms").split(",");		
		JsonNode urlkeys = model.getTags().path("urlkeys");
		JsonNode translations = model.getTags().path("translations");
		if (translations.fields().hasNext() == false) {
			return;
		}

		Set<String> tagNames =  new HashSet<>();
		model.getTags().fields().forEachRemaining(tag -> {
			tagNames.add(tag.getKey());
		});
		//Set<String> langs = translations.fieldNames();
		tagNames.remove("translations");
		List<ProductTagMetasBean> beans = new ArrayList<>();
		for (String tagName : tagNames) {
			Integer tagId = getXRoadsModule().getConfiguration().getTagMap().get(tagName);
			if (tagId != null) {
				JsonNode tagValueNode = model.getTags().path(tagName);
				String[] values;
				if(tagValueNode.isArray()) {
					values = new String[tagValueNode.size()];
					for(int i = 0; i < tagValueNode.size(); i++) {
						values[i] = tagValueNode.get(i).asText(null);
					}
				} else {
					values = new String[] {tagValueNode.asText(null)};
				}
				
				for(String tagValue : values) {	
					if (tagValue != null) {
						List<ProductTagMetaBean> productTagMetasList = new ArrayList<>();
						ProductTagMetasBean bean = new ProductTagMetasBean();
						bean.setTagId(tagId);
						bean.setTagValue(tagValue);
						JsonNode langs = translations.path(tagName + "-" + tagValue);
						if(langs.isMissingNode()) {
							langs = translations.path(tagName);
						}
						
						if (langs != null && !langs.isMissingNode())
							langs.fields().forEachRemaining(lang -> {
								JsonNode platforms = lang.getValue();	
								if (platforms != null)
									platforms.fields().forEachRemaining(platform -> {
										String translation = platform.getValue().asText();
										ProductTagMetaBean item = new ProductTagMetaBean();
										item.setLocaleCode(lang.getKey());
										item.setPlatformUid(platform.getKey());
										item.setTagTranslation(translation);							
										if (translation.length() > 1)
											item.setTagTranslation(translation.substring(0, 1).toUpperCase() + translation.substring(1));
										else
											item.setTagTranslation(translation);
										
										String urlKey = null;
										if(!urlkeys.path(tagName + "-" + tagValue).isMissingNode()) {
											urlKey = urlkeys.path(tagName + "-" + tagValue).path(lang.getKey()).path(platform.getKey()).asText(null);
										} else {
											urlKey = urlkeys.path(tagName).path(lang.getKey()).path(platform.getKey()).asText(null);
										}
										
										if (urlKey == null) {
											urlKey = encodeUrlKey(translation);
										}
										item.setUrlKey(urlKey);
										productTagMetasList.add(item);
									});
							});
						bean.setProductTagMetas(productTagMetasList);	
						beans.add(bean);
					}
				}
			}								
		}
		if (beans.size() > 0) {		
			for (ProductTagMetasBean bean : beans) {
				api.updateProductTagMetaTranslations(bean);
			}
		}
	}
	
	private static String encodeUrlKey(String translation) {
        return translation
                        .toLowerCase()
                        .replaceAll("[^\\p{L}0-9]", " ")
                        .replace(" ", "-");
	}
}
