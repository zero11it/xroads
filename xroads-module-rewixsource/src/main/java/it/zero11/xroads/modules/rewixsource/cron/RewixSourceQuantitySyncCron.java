package it.zero11.xroads.modules.rewixsource.cron;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Price;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.Stock;
import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.ExpiredException;
import it.zero11.xroads.modules.rewixsource.api.RewixAPI;
import it.zero11.xroads.modules.rewixsource.api.UnauthorizedException;
import it.zero11.xroads.modules.rewixsource.api.model.LockModel;
import it.zero11.xroads.modules.rewixsource.api.model.PageData;
import it.zero11.xroads.modules.rewixsource.api.model.RewixModel;
import it.zero11.xroads.modules.rewixsource.api.model.RewixProduct;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.modules.rewixsource.utils.RewixPriceUtils;
import it.zero11.xroads.modules.rewixsource.utils.RewixProductUtils;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.utils.XRoadsUtils;

@CronSchedule(hour = {}, minute = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55 }, second = { 0 })
public class RewixSourceQuantitySyncCron extends AbstractXRoadsCronRunnable<XRoadsRewixSourceModule> {
	private static final Logger log = Logger.getLogger(RewixSourceProductCron.class);
	private RewixAPI api;
	
	@Override
	public void run() {
		log.info("Start Import quantities " + xRoadsModule.getName());
		try {
			api = new RewixAPI(xRoadsModule);
			
			sync();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
		log.info("End Import quantities " + xRoadsModule.getName());
	}

	public void sync() throws MalformedURLException, IOException, UnauthorizedException, SyncException {
		final String since = xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF);
		Map<Integer, LockModel> lockModels = api.getGrowingOrder();
		xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF, (String) null);	
		
		boolean isFullSync = (StringUtils.isEmpty(since) || since.equals("force-full-sync"));
		PageData pageData;
		int count = 0;
		if (!isFullSync) { //Incremental
			try (final InputStream stream = api.downloadCatalogForPlatform(since)){
				pageData = api.parseProducts(stream);
			} catch (ConnectException ce) {
				//If we have temporary connection issue we retry at next chance
				xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF, since);
				log.info(ce.getMessage());
				return;
			} catch (JAXBException | IOException e) {
				throw new RuntimeException(e);
			} catch (ExpiredException e) {
				xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF, "force-full-sync");
				log.info("Incremental validity failed.");
				return;
			}
			
			xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTSYNCSTATUS, "Incremental Sync 0 %");
			
			//TODO load product/models in batch
			for (RewixProduct rewixProduct : pageData.getItems()) {
				String productSourceId = RewixProductUtils.getXroadsProductSourceId(rewixProduct, xRoadsModule);
				Product product = xRoadsModule.getXRoadsCoreService().getEntity(Product.class, productSourceId);
				if(product == null) {
					continue;
				}
				
				updateProductPriceAndQuantity(pageData, lockModels, product, rewixProduct);
				if (++count % 25 == 0){
					xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTSYNCSTATUS, "Incremental Sync " + (100 * count / pageData.getItems().size()) + " %");
				}
			}
		} else { //Full
			xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTSYNCSTATUS, "Waiting for full sync to start");

			try{
				pageData = api.getCatalogForPlatformAndParse();
			} catch (ConnectException ce) {
				log.info(ce.getMessage());
				return;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			//Refresh cache
			xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTSYNCSTATUS, "Full Sync 0 %");
			
			final int BATCH_SIZE = 100;
			List<Product> productsToProcess = null;

			do {
				productsToProcess = xRoadsModule.getXRoadsCoreService().getActiveEntities(Product.class,
						productsToProcess != null ? productsToProcess.get(productsToProcess.size() - 1).getSourceId()
								: xRoadsModule.getConfiguration().getPrefixSourceId() + "0",
						BATCH_SIZE);
				
				for (Product product : productsToProcess) {
					if (RewixProductUtils.isProuctOfThisModule(product, xRoadsModule)) {
						RewixProduct rewixProduct = findRewixProduct(pageData, RewixProductUtils.getRewixProductId(product, xRoadsModule));
						
						updateProductPriceAndQuantity(pageData, lockModels, product, rewixProduct);

						if (++count % 25 == 0){
							xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTSYNCSTATUS, "Full Sync " + (100 * count / productsToProcess.size()) + " %");
						}
						
					}
				}
			}while(productsToProcess.size() == BATCH_SIZE);
		}

		xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTSYNCSTATUS, "Complete");
		if ("force-full-sync".equals(xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF))){
			//Something requested a full sync while a sync was already in progress
			xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF, (String) null);
		}else{
			xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF, pageData.getLastUpdate());
		}
	}

	private void updateProductPriceAndQuantity(PageData products, Map<Integer, LockModel> lockModels, Product product, RewixProduct rewixProduct) throws SyncException {
		List<Model> models = xRoadsModule.getXRoadsCoreService().getEntitiesByProductGroup(Model.class, List.of(product.getSourceId()))
				.getOrDefault(product.getSourceId(), List.of());
		
		if (rewixProduct == null) { //Caso che si può verificare solo nella fullsync
			if (models.size() > 0){
				for (Model model : models) {
					setStock(rewixProduct, model, 0);
				}
			}
			return;
		}
		
		String productSourceId = RewixProductUtils.getXroadsProductSourceId(rewixProduct, xRoadsModule);
		List<Price> xRoadsproductPrices = RewixPriceUtils.getXRoadsPricesFromRewixProduct(xRoadsModule, rewixProduct);
		xRoadsModule.getXRoadsCoreService().consumeProductGroupped(xRoadsModule, productSourceId, xRoadsproductPrices);
		
		List<RewixModel> rewixModels = new ArrayList<>(rewixProduct.getModels());
		List<Model> remaining = new ArrayList<>(models);
		for(RewixModel rewixModel : rewixModels){
			Model currentModel = null;
			for (Model model : models) {
				if (RewixProductUtils.getRewixModelId(model, xRoadsModule).equals(rewixModel.getId())) {
					currentModel = model;
					remaining.remove(currentModel);
					break;
				}
			}
			if (currentModel != null) {
				setStock(rewixProduct, currentModel, rewixModel.getAvailability());		
			}
		}
		
		for (Model model:remaining){
			setStock(rewixProduct, model, 0);
		}
	}

	private void setStock(RewixProduct product, Model model, int availability) throws SyncException {
		Stock stock = XRoadsUtils.getStockInstance();
		stock.setAvailability(availability);
		stock.setSourceId(model.getSourceId());
		stock.setModelSourceId(model.getSourceId());
		stock.setSupplier(xRoadsModule.getConfiguration().getSupplierName());
		stock.setWarehouse(xRoadsModule.getConfiguration().getWarehouseName());
		((ObjectNode) stock.getData()).put("virtual", xRoadsModule.getConfiguration().getVirtualQuantities());
		xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, stock);
	}
	
	private RewixProduct findRewixProduct(PageData products, Integer productId) {
		for (RewixProduct rewixProduct : products.getItems()) {
			if (rewixProduct.getId().equals(productId))
				return rewixProduct;
		}

		return null;
	}
}
