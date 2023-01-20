package it.zero11.xroads.modules.rewix.consumers;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.ProductRevision;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.ProductNotFoundException;
import it.zero11.xroads.modules.rewix.api.RewixAPIException;
import it.zero11.xroads.modules.rewix.api.model.ProductBean;
import it.zero11.xroads.modules.rewix.api.model.ProductImageBean;
import it.zero11.xroads.modules.rewix.api.model.ProductImagesBean;
import it.zero11.xroads.modules.rewix.api.model.ProductRestrictionBean;
import it.zero11.xroads.modules.rewix.api.model.ProductRestrictionsBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagMetaBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagMetasBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagsBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTranslationBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTranslationsBean;
import it.zero11.xroads.modules.rewix.utils.GroupSearchBean;
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
		Integer version = XRoadsUtils.getExternalReferenceVersion(product, xRoadsModule);
		
		final ProductRevision revision = getXRoadsModule().getXRoadsCoreService().getEntityRevision(ProductRevision.class,
				product.getSourceId(), version);
		
		if(version == null) {
			version = -1;
		}
		
		log.info("Updating rewix product " + product.getSku());

		final String rewixIdStr = XRoadsUtils.getExternalReferenceId(product, xRoadsModule);
		final int rewixId;
		if (rewixIdStr == null) {
			rewixId = updateProductHead(product, true);
			log.debug("Updating rewix product external reference " + product.getSku() + " --> " + rewixId);
			getXRoadsModule().getXRoadsCoreService().updateExternalReferenceIdAndVersion(xRoadsModule, product, Integer.toString(rewixId), version);
		}else {
			if(revision == null && !xRoadsModule.getConfiguration().isEnableFullRewixProductUpdate()) {
				throw new RuntimeException("Revision is null and ENABLE_FULL_REWIX_UPDATE_PRODUCT is false, is not possibile update product !");
			}
			rewixId = Integer.parseInt(rewixIdStr);
		}
		
		boolean failed = true;
		try {
			if (revision == null || !revision.getDescriptions().equals(product.getDescriptions()) 
					|| !revision.getNames().equals(product.getNames()) || !revision.getUrlkeys().equals(product.getUrlkeys())) {
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

			if (revision == null || !revision.getRestrictions().equals(product.getRestrictions())) {
				updateProductRestrictions(rewixId, product);		
			} 
			if (revision != null && !revision.getRestrictions().equals(product.getRestrictions())) {
				removeProductRestrictions(rewixId, product);
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
		rewixProduct.setIntra(product.getData().path(XRoadsJsonKeys.REWIX_PRODUCT_HARMONIZED_SYSTEM_CODE_KEY).asText());
		rewixProduct.setSupplierCode(product.getSupplier());
		
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

		List<ProductImageBean> imageListBean = new ArrayList<ProductImageBean>();
		ProductImagesBean images = new ProductImagesBean();
		images.setStockProductId(rewixId);
		images.setProductImages(new ArrayList<ProductImageBean>());

		if(product.getImages().isArray()) { // new format
			for(JsonNode image : product.getImages()) {
				String uri = image.path("uri").asText(null);
				String name = image.path("name").asText(null);
				JsonNode models = image.path("models");
				
				Integer[] modelIds = null;
				if(!models.isMissingNode() && models.isArray()) {
					modelIds = new Integer[models.size()];
					for(int i = 0; i < models.size(); i++) {
						Model m = xRoadsModule.getXRoadsCoreService().getEntity(Model.class, models.get(i).asText());
						if(m == null || Integer.valueOf(XRoadsUtils.getExternalReferenceId(m, xRoadsModule)) == null) {
							throw new SyncException("Failed to import images, model : " +  models.get(i).asText() + "not imported !");
						}
						modelIds[i] = Integer.valueOf(XRoadsUtils.getExternalReferenceId(m, xRoadsModule));
					}
				}
				try(InputStream stream = xRoadsModule.getXRoadsCoreService().getResource(new URI(uri))){
					ProductImageBean bean = new ProductImageBean();
					bean.setData(IOUtils.toByteArray(stream));
					bean.setModelIds(modelIds);
					bean.setName(name);
					imageListBean.add(bean);	
				} catch (Exception e) {
					if (!xRoadsModule.getConfiguration().isIgnoreMissingImages()) {
						throw new SyncException("Failed to open/download image " + uri + " " + e.getMessage());
					}
				}
			}
		} else {
			// old deprecated format
			List<String> imagesList = new ArrayList<>();
			for (int j = 0; j < product.getImages().path("urls").size(); j++) {
				imagesList.add(product.getImages().path("urls").path(j).asText());
			}

			if (imagesList.size() > 0) {
				for (String imagePath : imagesList) {
					try(InputStream stream = xRoadsModule.getXRoadsCoreService().getResource(new URI(imagePath))){
						ProductImageBean bean = new ProductImageBean();
						bean.setData(IOUtils.toByteArray(stream));
						imageListBean.add(bean);	
					} catch (Exception e) {
						if (!xRoadsModule.getConfiguration().isIgnoreMissingImages()) {
							throw new SyncException("Failed to open/download image " + imagePath + " " + e.getMessage());
						}
					}
				}
			}	
		}

		
		if (imageListBean.size() > 0) {
			images.setDeleteExisting(true);
			for(ProductImageBean image : imageListBean) {
				images.getProductImages().clear();
				images.getProductImages().add(image);
				api.updateImages(images);
				images.setDeleteExisting(false);
			}
		}

	}

	public void updateProductTags(Integer rewixId, Product product, ProductRevision revision) throws RewixAPIException, ProductNotFoundException {
		log.debug("updateProductTags for product " + product.getSku());

		Map<String, Integer> configurationTagMap = getXRoadsModule().getConfiguration().getTagMap();

		Map<String, List<String>> tagMap = new HashMap<>();
		product.getTags().fields().forEachRemaining(tag -> {
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
			ProductTagsBean tags = new ProductTagsBean();
			tags.setStockProductId(rewixId);
			tags.setProductTags(new ArrayList<>());
			for(Map.Entry<String, Integer> configuratedTag : configurationTagMap.entrySet()) {
				ProductTagBean tag = new ProductTagBean();
				tag.setTagId(configuratedTag.getValue());
				List<String> tagValues = tagMap.get(configuratedTag.getKey());
				tag.setTagValues(tagValues);			
				tags.getProductTags().add(tag);
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
				JsonNode tagValueNode = product.getTags().path(tagName);
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
		
		Map<String, Map<String, String>> urlKeysMap = new HashMap<>(); //LANG CODE -> PLATFORM:DESCRIPTION
		{
			product.getUrlkeys().fields().forEachRemaining(urlkey -> {
				Map<String, String> platforms = new HashMap<>();
				urlkey.getValue().fields().forEachRemaining(platform -> {
					platforms.put(platform.getKey(), urlkey.getValue().path(platform.getKey()).asText());
				});
				urlKeysMap.put(urlkey.getKey(), platforms);
			});
			ProductTranslationsBean translations = new ProductTranslationsBean();
			translations.setType(7); // URLkey
			translations.setValue(null);
			translations.setStockProductId(rewixId);
			List<ProductTranslationBean> productTranslations = new ArrayList<>();
			for (String lang : urlKeysMap.keySet()) {
				for (String platform : urlKeysMap.get(lang).keySet()) {
					ProductTranslationBean translation = new ProductTranslationBean();
					translation.setLocaleCode(lang);
					translation.setTranslation(urlKeysMap.get(lang).get(platform));
					translation.setPlatformUid(platform);
					productTranslations.add(translation);					
				}
			}
			translations.setProductTranslations(productTranslations);
			if (urlKeysMap.size() > 0)
				beans.add(translations);
		}
		

		if (beans.size() > 0) {			
			for (ProductTranslationsBean bean : beans) {
				api.updateProductTranslations(bean);
			}
		} 
	}

	protected void updateProductRestrictions(Integer rewixId, Product product) throws SyncException {
		
		if(product.getRestrictions().isEmpty())
			return;
		
		List<ProductRestrictionBean> productVisibleRestrictionsList = null;
		List<ProductRestrictionBean> productHiddenRestrictionsList = null;
		JsonNode visible = product.getRestrictions().path("visible");
		JsonNode hidden = product.getRestrictions().path("hidden");

		Set<GroupSearchBean> groupsToSearch = new HashSet<>();

		if(!visible.isMissingNode()) {
			Iterator<Map.Entry<String, JsonNode>> iter = visible.fields();
			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> groups = iter.next();
				String platform = groups.getKey();
				if(!platform.equals("countries")) {
					JsonNode groupList = groups.getValue();
					if(!groupList.isMissingNode() && groupList.isArray()) {
						for(JsonNode groupName : groupList) {
							groupsToSearch.add(new GroupSearchBean(platform, groupName.asText()));
						}
					}
				}
			}
		}
		if(!hidden.isMissingNode()) {
			Iterator<Map.Entry<String, JsonNode>> iter = hidden.fields();
			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> groups = iter.next();
				String platform = groups.getKey();
				if(!platform.equals("countries")) {
					JsonNode groupList = groups.getValue();
					if(!groupList.isMissingNode() && groupList.isArray()) {
						for(JsonNode groupName : groupList) {
							groupsToSearch.add(new GroupSearchBean(platform, groupName.asText()));
						}
					}
				}
			}
		}

		Map<GroupSearchBean, Integer> groupsId = getGroupIds(groupsToSearch);
		
		if(!visible.isMissingNode()) {
			productVisibleRestrictionsList = new ArrayList<ProductRestrictionBean>();
			JsonNode countryList = visible.path("countries");
			if(!countryList.isMissingNode() && countryList.isArray()) {
				for(JsonNode country : countryList) {
					ProductRestrictionBean p = new ProductRestrictionBean();
					p.setCountryCode(country.asText());
					productVisibleRestrictionsList.add(p);
				}
			}
			Iterator<Map.Entry<String, JsonNode>> iter = visible.fields();
			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> groups = iter.next();
				String platform = groups.getKey();
				if(!platform.equals("countries")) {
					JsonNode groupList = groups.getValue();
					if(!groupList.isMissingNode() && groupList.isArray()) {
						for(JsonNode groupName : groupList) {				
							ProductRestrictionBean p = new ProductRestrictionBean();
							p.setGroupId(groupsId.get(new GroupSearchBean(platform, groupName.asText())));
							productVisibleRestrictionsList.add(p);
						}
					}
				}
			}
		}
		if(!hidden.isMissingNode()) {
			productHiddenRestrictionsList = new ArrayList<ProductRestrictionBean>();
			JsonNode countryList = hidden.path("countries");
			if(!countryList.isMissingNode() && countryList.isArray()) {
				for(JsonNode country : countryList) {
					ProductRestrictionBean p = new ProductRestrictionBean();
					p.setCountryCode(country.asText());
					productHiddenRestrictionsList.add(p);
				}
			}
			Iterator<Map.Entry<String, JsonNode>> iter = hidden.fields();
			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> groups = iter.next();
				String platform = groups.getKey();
				if(!platform.equals("countries")) {
					JsonNode groupList = groups.getValue();
					if(!groupList.isMissingNode() && groupList.isArray()) {
						for(JsonNode groupName : groupList) {				
							ProductRestrictionBean p = new ProductRestrictionBean();
							p.setGroupId(groupsId.get(new GroupSearchBean(platform, groupName.asText())));
							productHiddenRestrictionsList.add(p);
						}
					}
				}
			}
		}

		if(productVisibleRestrictionsList != null || productHiddenRestrictionsList != null) {
			ProductRestrictionsBean productRestrictionsBean = new ProductRestrictionsBean();
			productRestrictionsBean.setStockProductId(rewixId);
			productRestrictionsBean.setProductRestrictionsHidden(productHiddenRestrictionsList);
			productRestrictionsBean.setProductRestrictionsVisible(productVisibleRestrictionsList);
			api.updateProductRestrictions(productRestrictionsBean);
		}
	}

	protected void removeProductRestrictions(Integer rewixId, Product product) {

	}

	private static String encodeUrlKey(String translation) {
        return translation
                        .toLowerCase()
                        .replaceAll("[^\\p{L}0-9]", " ")
                        .replace(" ", "-");
	}
}
