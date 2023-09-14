package it.zero11.xroads.modules.rewixsource.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.model.GhostEnvelope;
import it.zero11.xroads.modules.rewixsource.api.model.GhostEnvelope.GhostOrder.Item;
import it.zero11.xroads.modules.rewixsource.api.model.GhostEnvelope.GhostOrder.RecipientDetails;
import it.zero11.xroads.modules.rewixsource.api.model.GhostEnvelope.GhostOrder.RecipientDetails.Address;
import it.zero11.xroads.modules.rewixsource.api.model.OrderStatusUpdate;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixSourceOrderConsumer extends AbstractRewixSourceConsumer implements EntityConsumer<Order> {

	public RewixSourceOrderConsumer(XRoadsRewixSourceModule xRoadsModule) {
		super(xRoadsModule);
	}

	@Override
	public void consume(Order order) throws SyncException {
		if ("rewix".equals(order.getSource())) {
			// if order is not for this module ignore it for this module and mark as synchronized
			String rewixExternalReference = "-";
			if (XRoadsUtils.getExternalReferenceId(order, xRoadsModule) != null) {
				rewixExternalReference = XRoadsUtils.getExternalReferenceId(order, xRoadsModule);
			} else {
				GhostEnvelope envelope = getOrderData(order);
				if (envelope != null) {
					try {
						api.sendDropshippingOrder(envelope);
						OrderStatusUpdate status = api.getOrderStatusByRef(order.getSourceId());
						if (status == null || status.getOrders() == null || status.getOrders().size() == 0) {
							throw new RuntimeException("Failed to send order!");
						}
						rewixExternalReference = status.getOrders().get(0).getOrder_id().toString();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
			xRoadsModule.getXRoadsCoreService().updateExternalReferenceId(xRoadsModule, order, rewixExternalReference);
		}
	}

	public GhostEnvelope getOrderData(Order order) {
		GhostEnvelope.GhostOrder ghostOrder;
		try {
			ghostOrder = new GhostEnvelope.GhostOrder(order.getSourceId().toString(), order.getOrderDate());
			
			GhostEnvelope envelope = new GhostEnvelope();
			envelope.setOrder_list(new ArrayList<>());
			envelope.getOrder_list().add(ghostOrder);

			Map<Integer, Item> ghostItemList = new HashMap<>();
			Iterator<JsonNode> lineItemIterator = order.getLineItems().iterator();
			while (lineItemIterator.hasNext()) {
				ObjectNode currentLineItem = (ObjectNode) lineItemIterator.next();
				if (currentLineItem.path(XRoadsJsonKeys.ORDER_ITEM_MODEL_ID_KEY).asText("")
						.startsWith(xRoadsModule.getConfiguration().getPrefixSourceId())) {
					Model model = xRoadsModule.getXRoadsCoreService().getEntity(Model.class,
							currentLineItem.path(XRoadsJsonKeys.ORDER_ITEM_MODEL_ID_KEY).asText());
					if (model == null) {
						throw new RuntimeException(
								"Model " + currentLineItem.path(XRoadsJsonKeys.ORDER_ITEM_MODEL_ID_KEY).asText()
										+ " with module prefix not found, enexpeced case !");
					}
					Integer rewixSupplierItemId = Integer
							.valueOf(XRoadsUtils.getExternalReferenceId(model, xRoadsModule)
									.replace(xRoadsModule.getConfiguration().getPrefixSourceId(), ""));
					Item ghostItem = ghostItemList.get(rewixSupplierItemId);
					if (ghostItem == null) {
						ghostItem = new Item();
						ghostItem.setStock_id(rewixSupplierItemId);
						ghostItem.setQuantity(0);
						ghostItemList.put(rewixSupplierItemId, ghostItem);
					}
					ghostItem.setQuantity(ghostItem.getQuantity()
							+ currentLineItem.path(XRoadsJsonKeys.ORDER_ITEM_QUANTITY_KEY).asInt());
				}
			}
			if(ghostItemList.size() == 0) {
				return null;
			}
			ghostOrder.setItem_list(new ArrayList<>(ghostItemList.values()));

			RecipientDetails recipient = generateAddressFromOrder(order); // Null?
			ghostOrder.setRecipient_details(recipient);

			return envelope;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private RecipientDetails generateAddressFromOrder(Order order) {
		RecipientDetails recipient = new RecipientDetails();
		ObjectNode xroadsDispatchAddress = (ObjectNode) order.getShippingAddress();
		ObjectNode anagrafica = (ObjectNode) order.getAnagrafica();
		recipient.setRecipient("(" + xroadsDispatchAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESSEE_KEY).asText() + ")");
		Address address = new Address();	
		address.setStreet_name(xroadsDispatchAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_NAME_KEY).asText());
		address.setZip(xroadsDispatchAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_ZIP_KEY).asText());
		address.setCity(xroadsDispatchAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_CITY_KEY).asText());
		address.setCountrycode( xroadsDispatchAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY).asText());
		address.setProvince(xroadsDispatchAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_PROVINCE_KEY).asText());
		recipient.setAddress(address);
		recipient.setPhone(new GhostEnvelope.GhostOrder.RecipientDetails.Phone(
				anagrafica.path(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY).asText(),
				anagrafica.path(XRoadsJsonKeys.CUSTOMER_CELL_KEY).asText()));
		recipient.setCareof(xroadsDispatchAddress.path(XRoadsJsonKeys.CUSTOMER_CARE_OF_KEY).asText());
		recipient.setCfpiva(anagrafica.path(XRoadsJsonKeys.CUSTOMER_VAT_NUMBER_KEY).asText());
		recipient.setCustomer_key(order.getCustomerSourceId());
		
		return recipient;
	}

}
