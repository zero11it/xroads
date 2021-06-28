package it.zero11.xroads.modules.rewix.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.ModelRevision;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.ProductNotFoundException;
import it.zero11.xroads.modules.rewix.api.RewixAPIException;
import it.zero11.xroads.modules.rewix.api.model.ProductModelBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelUpdateProductBean;
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

	protected Integer updateModelHead(int rewixProductId, Integer rewixModelId, Product product, Model model) throws RewixAPIException {
		log.debug("updateModelHead for model " + model.getSku());

		ProductModelBean rewixModel = new ProductModelBean();
		if (rewixModelId != null)
			rewixModel.setStockModelId(rewixModelId);
		rewixModel.setCode(model.getSku());
		rewixModel.setBarcode(model.getEan() == null ? "": model.getEan());
		rewixModel.setAdditionalBarcode(model.getAdditionalBarcode());
		rewixModel.setOption1(model.getOptions().path("size").asText());
		rewixModel.setOption2(model.getOptions().path("color").asText());			
		rewixModel.setStockProductId(rewixProductId);			
		rewixModel.setBackorder(model.getData().path(XRoadsJsonKeys.REWIX_MODEL_UNLIMITED_KEY).asBoolean());
		rewixModel.setPriority(model.getData().path(XRoadsJsonKeys.REWIX_MODEL_INDEX_KEY).asInt());
		rewixModel.setModelWeight(model.getWeight() != null ? model.getWeight().floatValue() : null);
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
					break;
				}
			}
			translations.setProductTranslations(productTranslations);
			api.updateProductTranslations(translations);
		}
	}
}
