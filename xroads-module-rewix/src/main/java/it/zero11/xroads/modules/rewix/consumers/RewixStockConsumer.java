package it.zero11.xroads.modules.rewix.consumers;

import java.math.BigDecimal;

import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Stock;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.model.ProductModelLotBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelLotsOperationType;
import it.zero11.xroads.modules.rewix.api.model.ProductModelLotsType;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixStockConsumer extends AbstractRewixConsumer implements EntityConsumer<Stock> {

	public RewixStockConsumer(XRoadsRewixModule xRoadsModule) {
		super(xRoadsModule);
	}

	@Override
	public void consume(Stock stock) throws SyncException {
		log.debug("Updating rewix stock " + stock.getModelSourceId());
		
		Model model = getXRoadsModule().getXRoadsCoreService().getEntity(Model.class, stock.getModelSourceId());
		if (model == null) {
			throw new SyncException("Model not found");
		}
		
		ProductModelLotBean productModelLotBean = new ProductModelLotBean();
		productModelLotBean.setAmount(Math.max(0, stock.getAvailability()));
		final boolean virtual = stock.getData().path(XRoadsJsonKeys.STOCK_VIRTUAL).asBoolean(false);
		if (virtual)
			productModelLotBean.setType(ProductModelLotsType.VIRTUAL);
		else
			productModelLotBean.setType(ProductModelLotsType.PHYSICAL);
		productModelLotBean.setOperation(ProductModelLotsOperationType.SET);
		productModelLotBean.setSingleLot(true);
		productModelLotBean.setReference("");	
		productModelLotBean.setSupplierId(xRoadsModule.getConfiguration().getSuppliers().get(stock.getSupplier()));
		productModelLotBean.setWarehouseId(xRoadsModule.getConfiguration().getWarehouses().get(stock.getWarehouse()));
		productModelLotBean.setCost(BigDecimal.ZERO);
		
		final String id = XRoadsUtils.getExternalReferenceId(model, xRoadsModule);
		if (id == null) {
			throw new SyncException("Model "+ model.getSku() + " is not yet on rewix");
		}	
		
		productModelLotBean.setStockModelId(Integer.parseInt(id));
		
		api.updateProductModelLotBean(productModelLotBean);
		
		getXRoadsModule().getXRoadsCoreService().updateExternalReferenceId(xRoadsModule, stock, stock.getSourceId());
	}

}
