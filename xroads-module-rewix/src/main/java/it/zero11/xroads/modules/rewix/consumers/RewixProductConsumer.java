package it.zero11.xroads.modules.rewix.consumers;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.ProductRevision;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.ProductNotFoundException;
import it.zero11.xroads.modules.rewix.api.RewixAPIException;
import it.zero11.xroads.modules.rewix.api.model.ProductBean;
import it.zero11.xroads.modules.rewix.api.model.ProductImageBean;
import it.zero11.xroads.modules.rewix.api.model.ProductImagesBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagMetaBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagMetasBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagsBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTranslationBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTranslationsBean;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixProductConsumer extends AbstractRewixConsumer implements EntityConsumer<Product> {

	public RewixProductConsumer(XRoadsRewixModule xRoadsModule) {
		super(xRoadsModule);
	}


	@Override
	public void consume(Product product) throws SyncException{
		final ProductRevision revision = getXRoadsModule().getXRoadsCoreService().getEntityRevision(ProductRevision.class,
				product.getSourceId(),
				XRoadsUtils.getExternalReferenceVersion(product, xRoadsModule));

		log.info("Updating rewix product " + product.getSku());

		final String rewixIdStr = XRoadsUtils.getExternalReferenceId(product, xRoadsModule);
		final int rewixId;
		if (rewixIdStr == null) {
			rewixId = updateProductHead(product, true);
		}else {
			rewixId = Integer.parseInt(rewixIdStr);
		}

		log.debug("Updating rewix product external reference " + product.getSku() + " --> " + rewixId);
		getXRoadsModule().getXRoadsCoreService().updateExternalReferenceIdAndVersion(xRoadsModule, product, Integer.toString(rewixId), -1);
		//FIXME: set version to -1 only if id changed or no version present
		
		boolean failed = true;
		try {
			if (revision == null || !revision.getDescriptions().equals(product.getDescriptions()) || !revision.getNames().equals(product.getNames())) {
				log.debug("Updating rewix product translations " + product.getSku());
				updateProductTranslations(rewixId, product);
			}
	
			if (revision == null || !revision.getTags().equals(product.getTags())) {
				log.debug("Updating rewix product tags " + product.getSku());
				updateProductTags(rewixId, product, revision);
			}
	
			if (revision == null || !revision.getTags().equals(product.getTags()) || !revision.getData().equals(product.getData())) {
				log.debug("Updating rewix product tag translations " + product.getSku());
				updateProductTagTranslations(rewixId, product);
			}
	
			if (revision == null || !revision.getImages().equals(product.getImages())) {
				updateProductImages(rewixId, product);		
			}
			
			failed = false;
		}finally {
			//If we have some exception, for example while importing some image, we set the product offline
			if (failed) {
				updateProductHead(product, true);
			}else {
				updateProductHead(product, false);
			}
		}

		getXRoadsModule().getXRoadsCoreService().updateExternalReferenceId(xRoadsModule, product, Integer.toString(rewixId));
	}


	private Integer updateProductHead(Product product, boolean forceOffline) throws RewixAPIException {
		log.debug("updateProductHead for product " + product.getSku());

		ProductBean rewixProduct = new ProductBean();
		rewixProduct.setCode(product.getSku());
		rewixProduct.setPriority(product.getData().path(XRoadsJsonKeys.REWIX_PRODUCT_PRIORITY_KEY).asInt());
		rewixProduct.setName(product.getName());
		rewixProduct.setBrand(product.getBrand());
		rewixProduct.setIntangible(product.getVirtual());
		if (forceOffline) {
			rewixProduct.setOnline(false);
		}else {
			rewixProduct.setOnline(product.getOnline());
		}
		rewixProduct.setCost(product.getCost());
		rewixProduct.setMadeIn(product.getTags().path(XRoadsJsonKeys.TAG_MADE_IN).asText());
		rewixProduct.setVatClassId(xRoadsModule.getConfiguration().getVat().get(product.getData().path(XRoadsJsonKeys.REWIX_PRODUCT_VAT_CLASS_KEY).asText()));

		final String id = XRoadsUtils.getExternalReferenceId(product, xRoadsModule);
		if (id != null)
			rewixProduct.setStockProductId(Integer.parseInt(id));

		return api.updateProduct(rewixProduct);
	}

	protected void updateProductImages(Integer rewixId, Product product) throws SyncException {
		log.debug("updateProductImages for product " + product.getSku());

		List<String> imagesList = new ArrayList<>();
		for (int i = 0; i < product.getImages().path("urls").size(); i++) {
			imagesList.add(product.getImages().path("urls").path(i).asText());
		}			
		if (imagesList.size() > 0) {

			ProductImagesBean images = new ProductImagesBean();
			images.setStockProductId(rewixId);
			images.setProductImages(new ArrayList<>());
			images.setDeleteExisting(true);

			for (String imagePath : imagesList) {
				try(InputStream stream = xRoadsModule.getXRoadsCoreService().getResource(new URI(imagePath))){
					ProductImageBean bean = new ProductImageBean();
					bean.setData(IOUtils.toByteArray(stream));
					images.getProductImages().add(bean);	
				} catch (Exception e) {
					if (!xRoadsModule.getConfiguration().isIgnoreMissingImages()) {
						throw new SyncException("Failed to open/download image " + imagePath + " " + e.getMessage());
					}
				}
			}							

			if (images.getProductImages().size() > 0) {				
				api.updateImages(images);				
			}
		}
	}

	public void updateProductTags(Integer rewixId, Product product, ProductRevision revision) throws RewixAPIException, ProductNotFoundException {
		log.debug("updateProductTags for product " + product.getSku());

		Map<String, List<String>> tagMap = new HashMap<>();
		product.getTags().fields().forEachRemaining(tag -> {
			if (! tag.getKey().equals("translations")) {
				List<String> values = new ArrayList<>();
				JsonNode content = tag.getValue();
				if (content.isArray()) {
					content.elements().forEachRemaining(element -> values.add(element.asText()));
				} else  if(!content.isNull())
					values.add(content.asText());
				tagMap.put(tag.getKey(), values);
			}
		});
		if (tagMap.size() > 0) {
			ProductTagsBean tags = new ProductTagsBean();
			tags.setStockProductId(rewixId);
			tags.setProductTags(new ArrayList<>());			
			for (String key : tagMap.keySet()) {					
				ProductTagBean tag = new ProductTagBean();
				Integer tagId = getXRoadsModule().getConfiguration().getTagMap().get(key);
				if (tagId != null) {
					tag.setTagId(tagId);					
					tag.setTagValues(tagMap.get(key));					
					tags.getProductTags().add(tag);															
				}
			}
			if (revision != null) { //Metto a null quelli scomparsi

				revision.getTags().fields().forEachRemaining(revisionTag -> {
					if (! tagMap.keySet().contains(revisionTag.getKey())) {
						ProductTagBean tag = new ProductTagBean();
						Integer tagId = getXRoadsModule().getConfiguration().getTagMap().get(revisionTag.getKey());
						if (tagId != null) {
							tag.setTagId(tagId);					
							tag.setTagValues(new ArrayList<>());					
							tags.getProductTags().add(tag);															
						}
					}
				});
			}
			
			api.updateProductTags(tags);
		}
	}
	protected void updateProductTagTranslations(Integer rewixId, Product product) throws RewixAPIException {
		if (product.getData().path("platforms") == null || product.getData().path("platforms").asText().length() == 0 ||
				! product.getTags().has("translations")) {
			return;
		}

		//String[] platforms = product.getData().getString("platforms").split(",");		
		JsonNode urlkeys = product.getTags().path("urlkeys");
		JsonNode translations = product.getTags().path("translations");
		if (translations.fields().hasNext() == false) {
			return;
		}

		Set<String> tagNames =  new HashSet<>();
		product.getTags().fields().forEachRemaining(tag -> {
			tagNames.add(tag.getKey());
		});
		//Set<String> langs = translations.fieldNames();
		tagNames.remove("translations");
		List<ProductTagMetasBean> beans = new ArrayList<>();
		for (String tagName : tagNames) {
			Integer tagId = getXRoadsModule().getConfiguration().getTagMap().get(tagName);
			if (tagId != null) {
				String tagValue = product.getTags().path(tagName).asText(null);
				if (tagValue != null) {
					List<ProductTagMetaBean> productTagMetasList = new ArrayList<>();
					ProductTagMetasBean bean = new ProductTagMetasBean();
					bean.setTagId(tagId);
					bean.setTagValue(tagValue);
					JsonNode langs = translations.path(tagName);
					if (langs != null)
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
									String urlKey = urlkeys.path(tagName).path(lang.getKey()).path(platform.getKey()).asText(null);
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
		if (beans.size() > 0) {		
			for (ProductTagMetasBean bean : beans) {
				api.updateProductTagMetaTranslations(bean);
			}
		}
	}

	protected void updateProductTranslations(Integer rewixId, Product product) throws RewixAPIException, ProductNotFoundException {
		log.debug("updateTranslations for product " + product.getSku());

		List<ProductTranslationsBean> beans = new ArrayList<>();

		Map<String, Map<String, String>> descriptionMap = new HashMap<>(); //LANG CODE -> PLATFORM:DESCRIPTION
		{
			product.getDescriptions().fields().forEachRemaining(description -> {
				Map<String, String> platforms = new HashMap<>();
				description.getValue().fields().forEachRemaining(platform -> {
					platforms.put(platform.getKey(), product.getDescriptions().path(description.getKey()).path(platform.getKey()).asText());
				});
				descriptionMap.put(description.getKey(), platforms);
			});
			ProductTranslationsBean translations = new ProductTranslationsBean();
			translations.setType(0); //Description
			translations.setValue(null);
			translations.setStockProductId(rewixId);	
			List<ProductTranslationBean> productTranslations = new ArrayList<>();
			for (String lang : descriptionMap.keySet()) {
				for (String platform : descriptionMap.get(lang).keySet()) {
					ProductTranslationBean translation = new ProductTranslationBean();
					translation.setLocaleCode(lang);
					translation.setTranslation(descriptionMap.get(lang).get(platform));
					translation.setPlatformUid(platform);
					productTranslations.add(translation);					
				}
			}
			translations.setProductTranslations(productTranslations);
			if (descriptionMap.size() > 0)
				beans.add(translations);
		}


		Map<String, Map<String, String>> namesMap = new HashMap<>(); //LANG CODE -> PLATFORM:DESCRIPTION
		{
			product.getNames().fields().forEachRemaining(productName -> {
				Map<String, String> platforms = new HashMap<>();
				productName.getValue().fields().forEachRemaining(platform -> {
					platforms.put(platform.getKey(), productName.getValue().path(platform.getKey()).asText());
				});
				namesMap.put(productName.getKey(), platforms);
			});
			ProductTranslationsBean translations = new ProductTranslationsBean();
			translations.setType(3); //Name
			translations.setValue(null);
			translations.setStockProductId(rewixId);
			List<ProductTranslationBean> productTranslations = new ArrayList<>();
			for (String lang : namesMap.keySet()) {
				for (String platform : namesMap.get(lang).keySet()) {
					ProductTranslationBean translation = new ProductTranslationBean();
					translation.setLocaleCode(lang);
					translation.setTranslation(namesMap.get(lang).get(platform));
					translation.setPlatformUid(platform);
					productTranslations.add(translation);					
				}
			}
			translations.setProductTranslations(productTranslations);
			if (namesMap.size() > 0)
				beans.add(translations);
		}

		if (beans.size() > 0) {			
			for (ProductTranslationsBean bean : beans) {
				api.updateProductTranslations(bean);
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
