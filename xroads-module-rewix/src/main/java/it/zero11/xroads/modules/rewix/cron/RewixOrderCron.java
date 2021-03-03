package it.zero11.xroads.modules.rewix.cron;

import org.apache.log4j.Logger;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.RewixAPI;
import it.zero11.xroads.modules.rewix.api.model.OrderFilterBean;
import it.zero11.xroads.modules.rewix.api.model.OrderListBean;
import it.zero11.xroads.modules.rewix.api.model.OrderListBean.OrderStatusInfo;
import it.zero11.xroads.modules.rewix.model.RewixParamType;
import it.zero11.xroads.modules.rewix.utils.RewixConversionUtils;
import it.zero11.xroads.sync.SyncException;

@CronSchedule(hour = {}, minute = {0,20,40}, second = {0})
public class RewixOrderCron extends AbstractXRoadsCronRunnable<XRoadsRewixModule>{

	private static final Logger log = Logger.getLogger(RewixOrderCron.class);
	private RewixAPI api; 
	
	
	@Override
	public void run() {
		log.info("Start Import Orders");

		api = new RewixAPI(xRoadsModule.getConfiguration().getUsername(), xRoadsModule.getConfiguration().getPassword(), xRoadsModule.getConfiguration().getEndpoint());
		
		for (String platform : xRoadsModule.getConfiguration().getOrderPlatforms()) {
			try {
				checkOrder(platform);
			} catch (Exception e) {
				log.error("An error ocuring during order sync cron : " + e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		log.info("End import Orders");
	}


	private void checkOrder(String platform) throws SyncException{

		log.info("Retrieving rewix orders ");

		OrderFilterBean orderFilterBean = new OrderFilterBean();
		orderFilterBean.setPlatformUid(platform);
		orderFilterBean.setMaxResult(1000);
		orderFilterBean.setOrderStatuses(xRoadsModule.getConfiguration().getOrderStatusToSync());
		if (xRoadsModule.getConfiguration().getOrderSubStatusToSync().size() > 0) {
			orderFilterBean.setOrderSubstatuses(xRoadsModule.getConfiguration().getOrderSubStatusToSync());
		}

		OrderListBean orderListBean = api.getAllOrders(orderFilterBean);

		StringBuilder exceptionsEncountered = new StringBuilder();
		for (OrderStatusInfo info : orderListBean.getOrders()) {
			try {
				processOrder(platform, info);
			} catch (Exception e) {
				exceptionsEncountered.append("Order ").append(info.getOrder_id()).append(" ").append(e.getMessage()).append("\n");
			}
		}
		
		if(exceptionsEncountered.length() > 0) {
			throw new SyncException(exceptionsEncountered.toString());
		}

	}

	public void processOrder(String platform,OrderStatusInfo info) throws SyncException {
		
		Order existing = xRoadsModule.getXRoadsCoreService().getEntity(Order.class, Integer.toString(info.getOrder_id()));
		if (existing != null) {
			return;
		}
		boolean isRewixCustomerSource = xRoadsModule.getXRoadsCoreService().getParameterAsBoolean(xRoadsModule, RewixParamType.ENABLE_EXPORT_CUSTOMERS);
		Order order = RewixConversionUtils.getOrderFromOrderBean(platform, api.getOrder(Long.valueOf(info.getOrder_id())), xRoadsModule, isRewixCustomerSource);

		Customer customer = xRoadsModule.getXRoadsCoreService().getEntity(Customer.class, order.getCustomerSourceId());
		customer = RewixConversionUtils.getOrUpdateCustomerFromOrder(order, customer);

		xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, customer);
		xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, order);
	}

}
