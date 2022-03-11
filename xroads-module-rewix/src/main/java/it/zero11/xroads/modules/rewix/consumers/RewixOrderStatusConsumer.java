package it.zero11.xroads.modules.rewix.consumers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.Order;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.RewixAPIException;
import it.zero11.xroads.modules.rewix.api.model.OrderAttachmentBean;
import it.zero11.xroads.modules.rewix.api.model.OrderAttachmentsBean;
import it.zero11.xroads.modules.rewix.api.model.UpdateDropshippingOrderStatusBean;
import it.zero11.xroads.modules.rewix.utils.Constants;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.XRoadsJsonKeys;

public class RewixOrderStatusConsumer extends AbstractRewixConsumer implements EntityConsumer<Order> {

	public RewixOrderStatusConsumer(XRoadsRewixModule xRoadsModule) {
		super(xRoadsModule);
	}

	@Override
	public void consume(Order order) throws RewixAPIException {
//		RewixOrderApiClient client = new RewixOrderApiClient(verticle.getVertx(), verticle.getEcomService(), verticle.getWebClient(), verticle.getRewixConfig());	
//		final Order order = MessageUtils.getMessageBodyAs(message, Order.class);
//		final Order old = MessageUtils.getMessageRevisionAs(message, Order.class);
//
//		String oldTracking = old != null ? old.getData().path(XRoadsJsonKeys.REWIX_ORDER_TRACKING_KEY).asText() : null;
//		if (oldTracking == null) oldTracking = "";
		String newTracking = order.getData().path(XRoadsJsonKeys.REWIX_ORDER_TRACKING_KEY).asText();
		String newTrackingCode = order.getData().path(XRoadsJsonKeys.REWIX_ORDER_TRACKING_CODE).asText();
//		if (newTracking == null) newTracking = "";
//		String oldInvoice = old != null ? old.getData().path(XRoadsJsonKeys.REWIX_ORDER_IVOICE_DOCUMENT_KEY).asText() : null;
//		if (oldInvoice == null) oldInvoice = "";
//		String newInvoice = order.getData().path(XRoadsJsonKeys.REWIX_ORDER_IVOICE_DOCUMENT_KEY).asText();
//		if (newInvoice == null) newInvoice = "";

		if (order.getSource().equals(xRoadsModule.getName()) && order.getStatus() > 0) {
			UpdateDropshippingOrderStatusBean info = new UpdateDropshippingOrderStatusBean();
			info.setOrderId(Integer.parseInt(order.getSourceId()));
//			boolean statusChanged = false;
//			if (! old.getStatus().equals(order.getStatus())) {
//				statusChanged = true;
				switch (order.getStatus()) {
				case 2:
					info.setSubstatus(Constants.ORDER_TODISPATCH);
					break;
				case 3:
				case 4:
					info.setSubstatus(Constants.ORDER_DISPATCHED);
					break;
				}				
//			}											
//			if (! oldTracking.equals(newTracking)) {
//				statusChanged = true;
//				info.setTrackingUrl(newTracking);
//			}	
			info.setTrackingCode(newTrackingCode);
			info.setTrackingUrl(newTracking);	
			api.updateOrderStatus(info);
			
//			if (statusChanged) {
//				client.updateOrderStatus(info)
//				.setHandler(h -> {
//					if (h.failed())
//						LogUtils.logError(log, "processOrderStatusUpdate", message, h.cause());				
//				});				
//			}

//			if (! oldInvoice.equals(newInvoice)) {
//				client.updateOrderAttachment(order)
//				.setHandler(h -> {
//					if (h.failed())
//						LogUtils.logError(log, "updateOrderAttachment", message, h.cause());				
//				});	
//			}
			
			
			log.info("updateOrderAttachment for order " + order.getSourceId());

			List<ObjectNode> attachmentList = new ArrayList<>();
			for (int i = 0; i < order.getData().path(XRoadsJsonKeys.REWIX_ORDER_TRACKING_KEY).size(); i++) {
				attachmentList.add(((ObjectNode)order.getData().path(XRoadsJsonKeys.REWIX_ORDER_TRACKING_KEY).path(i)));
			}			
			if (attachmentList.size() > 0) {
				OrderAttachmentsBean attachments = new OrderAttachmentsBean();
				attachments.setOrderId(Integer.parseInt(order.getSourceId()));
				attachments.setOrderAttachments(new ArrayList<>());

				for (ObjectNode attachmentPath : attachmentList) {
					OrderAttachmentBean bean = new OrderAttachmentBean();
					bean.setName(attachmentPath.path("name").asText());

					InputStream stream = null;
					try {
						stream = (new URL( attachmentPath.path("url").asText())).openStream();
						byte[] b = IOUtils.toByteArray(stream);
						bean.setData(b);
						attachments.getOrderAttachments().add(bean);	
					} catch (Exception e) {
						// FIXME which exception should be thrown?
						log.error("error while downloading atachement : " + e.getMessage()); 
						e.printStackTrace();
						throw new RuntimeErrorException(null);					
					} finally {
						try { if (stream != null) stream.close(); } catch (IOException e) {}
					}

				}							

				if (attachments.getOrderAttachments().size() > 0) {
					api.updateOrderAttachment(attachments);
				}
			}
		}	
		
		getXRoadsModule().getXRoadsCoreService().updateExternalReferenceId(xRoadsModule, order, order.getSourceId());
	}

}
