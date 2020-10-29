package it.zero11.xroads.modules.rewix.consumers;

import java.util.ArrayList;
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

		String platformsFlat = product.getData().path("platforms").asText();
		if (platformsFlat == null || platformsFlat.length() == 0) {
			throw new SyncException("No platform configured");
		}

		Set<GroupSearchBean> groups = new HashSet<>();
		String[] platforms = platformsFlat.split(",");
		for (String platform : platforms) {
			for (Price price : prices) {
				if (price.getListingGroup() != null)
					groups.add(new GroupSearchBean(platform.trim(), price.getListingGroup()));												
			}							
		}

		Map<GroupSearchBean, Integer> gs = getOrCreateGroupIds(groups);
		for (String platform : platforms) {
			for (Price price : prices) {
				String pricePlatforms = price.getData().path("platforms").asText();
				if (pricePlatforms == null || pricePlatforms.length() == 0 || pricePlatforms.contains(platform)) {
					ProductTaxableBean srewixPrice = new ProductTaxableBean();		
					if (price.getListingGroup() != null)
						srewixPrice.setGroupId(gs.get(new GroupSearchBean(platform.trim(), price.getListingGroup())));							
					srewixPrice.setRetailPrice(price.getRetailPrice());
					srewixPrice.setSuggestedPrice(price.getSuggestedPrice());
					srewixPrice.setTaxable(price.getSellPrice());
					srewixPrice.setPlatformUid(platform.trim());	
					srewixPrice.setCountry(price.getCountry());	
					srewixPrice.setMinimumQuantity(price.getMinQuantity());
					taxables.add(srewixPrice);		
				}
			}							
		}
		rewixPrice.setProductTaxables(taxables);

		api.updateProductTaxables(rewixPrice);

		for (Price p : prices) {
			getXRoadsModule().getXRoadsCoreService().updateExternalReferenceId(xRoadsModule, p, p.getSourceId());
		}
	}
}
