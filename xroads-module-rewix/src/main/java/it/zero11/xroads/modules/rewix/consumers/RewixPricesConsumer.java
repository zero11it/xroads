package it.zero11.xroads.modules.rewix.consumers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.zero11.xroads.model.Price;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.model.ProductTaxableBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTaxablesBean;
import it.zero11.xroads.modules.rewix.utils.GroupSearchBean;
import it.zero11.xroads.sync.EntityProductGroupedConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixPricesConsumer extends AbstractRewixConsumer implements EntityProductGroupedConsumer<Price> {

	public RewixPricesConsumer(XRoadsRewixModule xRoadsModule) {
		super(xRoadsModule);
	}

	@Override
	public void consume(List<Price> prices) throws SyncException {
		String productSourceId = XRoadsUtils.getEnsuringAllEquals(prices, Price::getProductSourceId);				

		log.info("Updating rewix price for product " + productSourceId);

		Product product = getXRoadsModule().getXRoadsCoreService().getEntity(Product.class, productSourceId);
		if (product == null){
			log.info("Price update for missing product " + productSourceId);
			throw new SyncException("Missing product " + productSourceId);
		}

		ProductTaxablesBean rewixPrice = new ProductTaxablesBean();

		final String id = XRoadsUtils.getExternalReferenceId(product, xRoadsModule);
		if (id != null) {
			rewixPrice.setStockProductId(Integer.parseInt(id));
		} else {
			log.info("Price update for still not imported product " + productSourceId);
			throw new SyncException("Price update for still not imported product " + productSourceId);
		}

		List<ProductTaxableBean> taxables = new ArrayList<>();
		List<String> platformsWithoutPrice = new ArrayList<String>(xRoadsModule.getConfiguration().getOrderPlatforms());
		
		Set<GroupSearchBean> groups = new HashSet<>();
		for (Price price : prices) {
			if (price.getListingGroup() != null) {
				String[] platforms = price.getData().path(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText().split(",");
				for(String platform : platforms) {
					groups.add(new GroupSearchBean(platform.trim(), price.getListingGroup()));	
				}
			}
		}							

		Map<GroupSearchBean, Integer> gs = getOrCreateGroupIds(groups);
		for (Price price : prices) {
			List<String> pricePlatforms;
			if(!price.getData().path(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText().trim().isEmpty()) {
				pricePlatforms = Arrays.asList(price.getData().path(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText().split(","));
			} else {
				pricePlatforms = xRoadsModule.getConfiguration().getOrderPlatforms();
			}
			for(String platform : pricePlatforms) {
				ProductTaxableBean srewixPrice = new ProductTaxableBean();		
				if (price.getListingGroup() != null)
					srewixPrice.setGroupId(gs.get(new GroupSearchBean(platform.trim(), price.getListingGroup())));							
				srewixPrice.setRetailPrice(price.getRetailPrice());
				srewixPrice.setSuggestedPrice(price.getSuggestedPrice());
				srewixPrice.setTaxable(price.getSellPrice());
				srewixPrice.setPlatformUid(platform.trim());	
				srewixPrice.setCountry(price.getCountry());	
				srewixPrice.setOption1(price.getData().path("option1").asText(null));
				srewixPrice.setOption2(price.getData().path("option2").asText(null));
				srewixPrice.setMinimumQuantity(price.getMinQuantity());
				if(!price.getData().path(XRoadsJsonKeys.REWIX_PRICE_PRIORITY_KEY).isMissingNode()) {
					srewixPrice.setPricePriority(price.getData().path(XRoadsJsonKeys.REWIX_PRICE_PRIORITY_KEY).asInt());
				}
				
				Integer merchantId;
				String merchantCode = price.getMerchantCode();
				if(merchantCode == null) {
					merchantId = null;
				} else {
					merchantId = xRoadsModule.getConfiguration().getMerchantMap().get(merchantCode);
					if(merchantId == null) {
						throw new SyncException("Merchant code " + merchantCode + " not in merchant map !");
					}
				}
				srewixPrice.setMerchantId(merchantId);
				
				taxables.add(srewixPrice);
				platformsWithoutPrice.remove(platform);
			}
		}

		//create null price for platforms that have not price
		for(String platform : platformsWithoutPrice) {
			ProductTaxableBean srewixPrice = new ProductTaxableBean();								
			srewixPrice.setRetailPrice(null);
			srewixPrice.setSuggestedPrice(null);
			srewixPrice.setTaxable(null);
			srewixPrice.setPlatformUid(platform.trim());
			taxables.add(srewixPrice);
		}

		rewixPrice.setProductTaxables(taxables);

		api.updateProductTaxables(rewixPrice);

		for (Price p : prices) {
			getXRoadsModule().getXRoadsCoreService().updateExternalReferenceId(xRoadsModule, p, p.getSourceId());
		}
	}
}
