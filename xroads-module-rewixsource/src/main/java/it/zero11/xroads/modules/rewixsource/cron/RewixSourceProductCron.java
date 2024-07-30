package it.zero11.xroads.modules.rewixsource.cron;

import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Price;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.RewixAPI;
import it.zero11.xroads.modules.rewixsource.api.model.Image;
import it.zero11.xroads.modules.rewixsource.api.model.PageData;
import it.zero11.xroads.modules.rewixsource.api.model.RewixModel;
import it.zero11.xroads.modules.rewixsource.api.model.RewixProduct;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.modules.rewixsource.utils.RewixPriceUtils;
import it.zero11.xroads.modules.rewixsource.utils.RewixProductUtils;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.utils.XRoadsUtils;

@CronSchedule(hour = {}, minute = { 10, 40 }, second = { 0 })
public class RewixSourceProductCron extends AbstractXRoadsCronRunnable<XRoadsRewixSourceModule> {
	private static final Logger log = Logger.getLogger(RewixSourceProductCron.class);

	private RewixAPI api;

	public void run() {
		log.info("Start Import products" + xRoadsModule.getName());
		try {
			api = new RewixAPI(xRoadsModule);
			
			PageData pageData = api.getCatalogForPlatformAndParse();
			
			importProducts(pageData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
		log.info("End Import products " + xRoadsModule.getName());
	}

	private void importProducts(PageData pageData) throws SyncException {
		JsonNode localeMap = xRoadsModule.getConfiguration().getLocaleMap();
		String lastSourceId = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.LAST_SYNCED_PRODUCT_SOURCE_ID);
		Integer syncedEntities = 0;
		List<Product> productToSync = null;
		productsOfAnotherModule:
		do {
			productToSync = xRoadsModule.getXRoadsCoreService().getEntities(Product.class,
					productToSync != null ? productToSync.get(productToSync.size() - 1).getSourceId()
							: xRoadsModule.getConfiguration().getPrefixSourceId() + "0",
					XRoadsRewixSourceModule.BATCH_SIZE, null, xRoadsModule);
			Integer productToSyncSize = productToSync.size();
			
			for(Product product : productToSync) {
				if(!product.getSourceId().startsWith(xRoadsModule.getConfiguration().getPrefixSourceId())) {
					break productsOfAnotherModule;
				}
				RewixProduct rewixProduct = findRewixProduct(pageData, RewixProductUtils.getRewixProductId(product, xRoadsModule));
				if(rewixProduct == null) {
					if(product.getOnline()) {
						xRoadsModule.getXRoadsCoreService().updateEntityInTransaction(product, xRoadsModule,
								p -> p.setOnline(false));
					}
					continue;
				}
				
				product = RewixProductUtils.getXRoadsBaseStructureProductFromRewixProduct(rewixProduct, xRoadsModule, false);
				
				// TAGS
				ObjectNode productTags = (ObjectNode) product.getTags();
				
				JsonNode tagMap = xRoadsModule.getConfiguration().getTagMap();
				tagMap.fields().forEachRemaining(tagConfig -> {
					String targetTagName = tagConfig.getKey();
					JsonNode targetTagConfigValue = tagConfig.getValue();
					Integer supplierTagId = tagConfig.getValue().path("sourceTagId").asInt(0);
					
					if (targetTagConfigValue.has("fixedValue") && !targetTagConfigValue.path("fixedValue").asText("").isEmpty()) {
						productTags.put(targetTagName, targetTagConfigValue.path("fixedValue").asText());
					} else {
						if (supplierTagId != 0) {
							rewixProduct.getTags()
							.stream()
							.filter(productTag -> productTag.getId() == supplierTagId)
							.findFirst()
							.ifPresent(productTag -> {
									targetTagConfigValue.path("valueMapping")
									.fields().forEachRemaining(tagValueMappingConfig -> {
										String tagValue = tagValueMappingConfig.getKey();
										if (tagValue.equals(productTag.getValue().getValue())) {
											productTags.put(targetTagName, tagValueMappingConfig.getValue().asText());
										}
									});
									if (!productTags.has(targetTagName)) {
										productTags.put(targetTagName, productTag.getValue().getValue());
									}
							});
						}
					}
				});

				// NAMES
				List<String> namePlatforms = xRoadsModule.getConfiguration().getNamePlatforms();
				if(namePlatforms != null) {
					ObjectNode names = ((ObjectNode) product.getNames());
					localeMap.fields().forEachRemaining(localeConfig -> {
						String targetLocale = localeConfig.getKey();
						String supplierLocale = localeConfig.getValue().asText();
						
						rewixProduct.getProductLocalizations()
						.getProductName()
						.stream()
						.filter(localeValue -> localeValue.getLocalecode().equals(supplierLocale))
						.findFirst()
						.ifPresent(localeValue -> {
							namePlatforms.forEach(platform -> {
								putLanguage(names, targetLocale, platform, localeValue.getValue());
							});
						});
					});
				}

				// OPTIONS
				List<String> optionPlatforms = xRoadsModule.getConfiguration().getOptionPlatforms();
				if(optionPlatforms != null) {
					ObjectNode option1 = ((ObjectNode) product.getOption1());
					ObjectNode option2 = ((ObjectNode) product.getOption2());
					ObjectNode option3 = ((ObjectNode) product.getOption3());
					
					localeMap.fields().forEachRemaining(localeConfig -> {
						String targetLocale = localeConfig.getKey();
						String supplierLocale = localeConfig.getValue().asText();
						//option1
						rewixProduct.getProductLocalizations()
						.getOption1()
						.stream()
						.filter(localeValue -> localeValue.getLocalecode().equals(supplierLocale))
						.findFirst()
						.ifPresent(localeValue -> {
							optionPlatforms.forEach(platform -> {
								putLanguage(option1, targetLocale, platform, localeValue.getValue());
							});
						});
						//option2
						rewixProduct.getProductLocalizations()
						.getOption2()
						.stream()
						.filter(localeValue -> localeValue.getLocalecode().equals(supplierLocale))
						.findFirst()
						.ifPresent(localeValue -> {
							optionPlatforms.forEach(platform -> {
								putLanguage(option2, targetLocale, platform, localeValue.getValue());
							});
						});
						//option3
						rewixProduct.getProductLocalizations()
						.getOption3()
						.stream()
						.filter(localeValue -> localeValue.getLocalecode().equals(supplierLocale))
						.findFirst()
						.ifPresent(localeValue -> {
							optionPlatforms.forEach(platform -> {
								putLanguage(option3, targetLocale, platform, localeValue.getValue());
							});
						});
					});
				}

				// DESCRIPTIONS
				List<String> descriptionPlatforms = xRoadsModule.getConfiguration().getDescriptionPlatforms();
				if(descriptionPlatforms != null) {
					ObjectNode descriptions = ((ObjectNode) product.getDescriptions());
					localeMap.fields().forEachRemaining(localeConfig -> {
						String targetLocale = localeConfig.getKey();
						String supplierLocale = localeConfig.getValue().asText();
						
						rewixProduct.getProductLocalizations()
						.getDescription()
						.stream()
						.filter(localeValue -> localeValue.getLocalecode().equals(supplierLocale))
						.findFirst()
						.ifPresent(localeValue -> {
							descriptionPlatforms.forEach(platform -> {
								putLanguage(descriptions, targetLocale, platform, localeValue.getValue());
							});
						});
					});
				}

				// IMAGES
				for (Image image : rewixProduct.getImages()) {
					ObjectNode productImage = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
					ArrayNode modelsOfImage = XRoadsUtils.OBJECT_MAPPER.createArrayNode();
					productImage.put("uri", xRoadsModule.getConfiguration().getApiEndpoint() + image.getUrl());
					productImage.set("models", modelsOfImage);
					((ArrayNode) product.getImages()).add(productImage);
				}

				// CONSUME PRODUCT
				xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, product);
				
				// MODELS
				importModels(rewixProduct);
				// PRICES
				importPrices(rewixProduct);
				
			}
			if(productToSyncSize == XRoadsRewixSourceModule.BATCH_SIZE) {
				lastSourceId = productToSync.get(productToSyncSize - 1).getSourceId();
			} else {
				lastSourceId = null;
			}
		}while(productToSync.size() == XRoadsRewixSourceModule.BATCH_SIZE && syncedEntities < XRoadsRewixSourceModule.MAX_ENTITIES_TO_SYNC);
		xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.LAST_SYNCED_PRODUCT_SOURCE_ID, lastSourceId);
	}

	private void importPrices(RewixProduct rewixProduct) throws SyncException {
		String productSourceId = RewixProductUtils.getXroadsProductSourceId(rewixProduct, xRoadsModule);
		List<Price> xRoadsproductPrices = RewixPriceUtils.getXRoadsPricesFromRewixProduct(xRoadsModule, rewixProduct);
		xRoadsModule.getXRoadsCoreService().consumeProductGroupped(xRoadsModule, productSourceId, xRoadsproductPrices);
	}

	private void importModels(RewixProduct rewixProduct) throws SyncException {
		for (RewixModel rewixModel : rewixProduct.getModels()) {
			Model model = XRoadsUtils.getModelInstance();
			model.setSourceId(RewixProductUtils.getXroadsModelSourceId(rewixModel, xRoadsModule));
			model.setProductSourceId(RewixProductUtils.getXroadsProductSourceId(rewixProduct, xRoadsModule));
			String modelSku =  RewixProductUtils.getXroadsModelSku(rewixModel, xRoadsModule);
			if(rewixProduct.getCode().equals(rewixModel.getCode())) {
				modelSku += ("_" + rewixModel.getOption1());
			}
			model.setSku(modelSku);
			model.setWeight(rewixModel.getModelWeight());
			if(rewixModel.getOption1() != null && !rewixModel.getOption1().isEmpty()) {
				((ObjectNode) model.getOptions()).put("size", rewixModel.getOption1());
			}
			if(rewixModel.getOption2() != null && !rewixModel.getOption2().isEmpty()) {
				((ObjectNode) model.getOptions()).put("color", rewixModel.getOption2());
			}

			model.setMerchantCode(xRoadsModule.getConfiguration().getMerchantCode());
			//We do not import ean of offline products to prevent duplicated eans between seasons
			if (rewixProduct.getAvailability() > 0) {
				if (rewixModel.getBarcode().length() == 13) {
					model.setEan(rewixModel.getBarcode());
				}
				model.setAdditionalBarcode(rewixModel.getAdditionalBarcodes());
			}
			xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, model);
		}
	}

	private RewixProduct findRewixProduct(PageData products, Integer productId) {
		for (RewixProduct rewixProduct : products.getItems()) {
			if (rewixProduct.getId().equals(productId))
				return rewixProduct;
		}

		return null;
	}

	private void putLanguage(JsonNode descriptions, String language, String platform, String description) {
		JsonNode descriptionPlatform = descriptions.path(language);
		if (descriptionPlatform == null || descriptionPlatform.isMissingNode()) {
			descriptionPlatform = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
			((ObjectNode) descriptions).set(language, descriptionPlatform);
		}
		((ObjectNode) descriptionPlatform).put(platform, description);
	}

}
