package it.zero11.xroads.modules.rewixsource.cron;

import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.RewixAPI;
import it.zero11.xroads.modules.rewixsource.api.model.OrderStatusUpdate;
import it.zero11.xroads.modules.rewixsource.api.model.OrderStatusUpdate.OrderStatusInfo;
import it.zero11.xroads.sync.XRoadsConstants;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.StackTraceUtil;
import it.zero11.xroads.utils.XRoadsUtils;

@CronSchedule(hour = {}, minute = { 0, 20, 40 }, second = { 0 }, onDeploy = false)
public class RewixSourceOrderStatusUpdateCron extends AbstractXRoadsCronRunnable<XRoadsRewixSourceModule> {

	private static final Logger log = Logger.getLogger(RewixSourceOrderStatusUpdateCron.class);
	private static final int BATCH_SIZE = 100;

	@Override
	public void run() {
		log.info("Start Update Orders Info for module : " + xRoadsModule.getName());

		StringBuilder errorsStringBuilder = new StringBuilder();
		RewixAPI api = new RewixAPI(xRoadsModule);

		List<Order> ordersToProcess = null;
		do {
			ordersToProcess = xRoadsModule.getXRoadsCoreService().getActiveEntities(Order.class,
					ordersToProcess != null ? ordersToProcess.get(ordersToProcess.size() - 1).getSourceId() : null,
					BATCH_SIZE);

			for (Order order : ordersToProcess) {
				try {
					String supplierOrderId = XRoadsUtils.getExternalReferenceId(order, xRoadsModule);
					if (supplierOrderId != null && !supplierOrderId.equals("-")) {
						OrderStatusUpdate status = api.getOrderStatusByRewixOrderId(supplierOrderId);
						OrderStatusInfo info = status.getOrders().get(0);
						
						final Integer newStatus = switch (info.getStatus().intValue()) {
						case OrderStatusInfo.ORDER_TODISPATCH -> XRoadsConstants.ORDER_CONFIRMED;
						case OrderStatusInfo.ORDER_WORKING_ON -> XRoadsConstants.ORDER_WORKING_ON;
						case OrderStatusInfo.ORDER_DISPATCHED -> XRoadsConstants.ORDER_DISPATCHED;
						default -> null;
						};
						
						if (newStatus != null && order.getStatus() != newStatus) {
							
							xRoadsModule.getXRoadsCoreService().updateEntityInTransaction(order, xRoadsModule, o -> {
								o.setStatus(newStatus);
								if(newStatus.equals(Integer.valueOf(XRoadsConstants.ORDER_DISPATCHED))) {
									((ObjectNode) o.getData()).put(XRoadsJsonKeys.REWIX_ORDER_TRACKING_CODE, info.getTracking_code());
									((ObjectNode) o.getData()).put(XRoadsJsonKeys.REWIX_ORDER_TRACKING_KEY, info.getTracking_url());
								}
							});
						}
					}
				} catch (Exception e) {
					errorsStringBuilder.append("Failed update status for order : " + order.getSourceId()
							+ StackTraceUtil.getStackTraceAsHTML(e));
				}
			}
		} while (ordersToProcess.size() == BATCH_SIZE);

		log.info("End Upddate Orders Info for module : " + xRoadsModule.getName());
	}

}
