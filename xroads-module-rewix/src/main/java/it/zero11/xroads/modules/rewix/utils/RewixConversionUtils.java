package it.zero11.xroads.modules.rewix.utils;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.NoResultException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.model.OrderBean;
import it.zero11.xroads.modules.rewix.api.model.OrderBean.OrderItemBean;
import it.zero11.xroads.modules.rewix.api.model.OrderBean.OrderPaymentBean;
import it.zero11.xroads.modules.rewix.api.model.OrderBean.PropertyData;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixConversionUtils {

	public static Order getOrderFromOrderBean(String platform, OrderBean orderBean, XRoadsRewixModule xRoadsModule, boolean isRewixCustomerSource) throws SyncException {
		Order order = XRoadsUtils.getOrderInstance();

		order.setSource("rewix");
		order.setStatus(0);
		order.setSourceId(Integer.toString(orderBean.getId()));

		
		String customerSourceId = null;
		if(isRewixCustomerSource) {
			try {
				customerSourceId = xRoadsModule.getXRoadsCoreService()
						.getEntityIdByModuleAndSourceId(Customer.class, xRoadsModule, orderBean.getUsername());
			} catch (NoResultException e) {
				customerSourceId = orderBean.getUsername();
			}			
		} else {
			try {
				customerSourceId = xRoadsModule.getXRoadsCoreService()
						.getEntityIdByModuleAndSourceId(Customer.class, xRoadsModule, orderBean.getUsername());
			} catch (NoResultException e) {
				throw new RuntimeException("Customer not found for order " + orderBean.getId());
			}
		}	

		order.setCustomerSourceId(customerSourceId);
		order.setCustomerEmail(orderBean.getEmail());
		
		order.setDispatchTaxable(orderBean.getDispatchFixedTaxable().add(orderBean.getDispatchWeightTaxable()));
		order.setDispatchVat(orderBean.getDispatchFixedVatAmount().add(orderBean.getDispatchWeightVatAmount()));
		order.setDispatchTotal(orderBean.getDispatchFixed().add(orderBean.getDispatchWeight()));
		order.setPaymentGateway(orderBean.getPaymentGateway());
		
		ArrayNode paymentsArray = XRoadsUtils.OBJECT_MAPPER.createArrayNode();
		List<OrderPaymentBean> orderPayments = orderBean.getPayments();
		if(orderPayments != null) {
			for(OrderPaymentBean paymentBean : orderPayments) {
				ObjectNode xroadsPayment = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
				xroadsPayment.put(XRoadsJsonKeys.ORDER_PAYMENT_GW_ID, paymentBean.getPaymentGwId());
				xroadsPayment.put(XRoadsJsonKeys.ORDER_PAYMENT_AMOUNT, paymentBean.getAmount());
				xroadsPayment.put(XRoadsJsonKeys.ORDER_PAYMENT_DATE, paymentBean.getDate().toString());
				xroadsPayment.put(XRoadsJsonKeys.ORDER_PAYMENT_ECREDIT_ID, paymentBean.getEcreditId());
				xroadsPayment.put(XRoadsJsonKeys.ORDER_PAYMENT_TRANSACTION_ACCOUNT, paymentBean.getTransactionAccount());
				xroadsPayment.put(XRoadsJsonKeys.ORDER_PAYMENT_TRANSACTION_REFERENCE, paymentBean.getTransactionReference());
				paymentsArray.add(xroadsPayment);
			}
		}
		order.setPayments(paymentsArray);
		
		order.setTotal(orderBean.getTotal());
		order.setTotalVat(orderBean.getVat_amount());
		order.setCurrency(orderBean.getCurrency());
		order.setOrderDate(new Timestamp(orderBean.getSubmitDate().getTime()));
		((ObjectNode)order.getData()).put(XRoadsJsonKeys.REWIX_ORDER_PLATFORM_KEY, platform);
		((ObjectNode)order.getData()).put(XRoadsJsonKeys.REWIX_ORDER_ORIGIN_KEY, orderBean.getOrigin());
		((ObjectNode)order.getData()).put("source",xRoadsModule.getName());
		((ObjectNode)order.getData()).put(XRoadsJsonKeys.ECREDIT,orderBean.getEcredit());
		
		((ObjectNode)order.getData()).put(XRoadsJsonKeys.PAYMENT_FEE_TAXABLE, orderBean.getPaymentFeeTaxable().toPlainString());
		((ObjectNode)order.getData()).put(XRoadsJsonKeys.PAYMENT_FEE_VAT_AMOUNT,orderBean.getPaymentFeeVatAmount().toPlainString());
		
		((ObjectNode)order.getData()).put(XRoadsJsonKeys.DISPATCH_FIXED_TAXABLE, orderBean.getDispatchFixedTaxable().toPlainString());
		((ObjectNode)order.getData()).put(XRoadsJsonKeys.DISPATCH_FIXED_VAT_AMOUNT,orderBean.getDispatchFixedVatAmount().toPlainString());
		
		for (PropertyData property : orderBean.getProperties())
			((ObjectNode)order.getData()).put(property.getKey(), property.getValue());

		((ObjectNode)order.getData()).put(XRoadsJsonKeys.ORDER_NOTES_KEY, orderBean.getNotes());
		((ObjectNode)order.getData()).put(XRoadsJsonKeys.ORDER_ADMIN_NOTES_KEY, orderBean.getAdminNotes());
		if(orderBean.getTrackingData() != null) {
			String carrierName = orderBean.getTrackingData().getCarrierName();
			if(carrierName != null) {
				((ObjectNode)order.getData()).put(XRoadsJsonKeys.ORDER_CARRIER_NAME, carrierName);
			}
		}
		
		ObjectNode anagrafica = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		if (orderBean.getAnagrafica() != null) {

			anagrafica.put(XRoadsJsonKeys.CUSTOMER_VAT_NUMBER_KEY, orderBean.getAnagrafica().getCfpiva());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_VALIDATED_VAT_NUMBER_KEY, orderBean.getAnagrafica().getValidatedVat());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_FISTNAME_KEY, orderBean.getAnagrafica().getFirstName());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_LASTNAME_KEY, orderBean.getAnagrafica().getLastName());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_COMPANY_KEY, orderBean.getAnagrafica().getBusinessName());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_TITLE_KEY, orderBean.getAnagrafica().getTitle());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_TELL_PREFIX_KEY, orderBean.getAnagrafica().getTel_prefix());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_TELL_KEY, orderBean.getAnagrafica().getTel());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY, orderBean.getAnagrafica().getCel_prefix());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_CELL_KEY, orderBean.getAnagrafica().getCel());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_PEC_KEY, orderBean.getAnagrafica().getPec());
			anagrafica.put(XRoadsJsonKeys.COSTOMER_SDI_KEY, orderBean.getAnagrafica().getSdi());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_NAME_KEY, orderBean.getAnagrafica().getAddress());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_ZIP_KEY, orderBean.getAnagrafica().getZip());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_CITY_KEY, orderBean.getAnagrafica().getCity());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_REGION_KEY, orderBean.getAnagrafica().getProv());
			anagrafica.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY, orderBean.getAnagrafica().getCountryCode());
		}
		order.setAnagrafica(anagrafica);

		ObjectNode dispatch = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		dispatch.put(XRoadsJsonKeys.CUSTOMER_ADDRESSEE_KEY, orderBean.getDispatchData().getRecipient());
		dispatch.put(XRoadsJsonKeys.CUSTOMER_CARE_OF_KEY, orderBean.getDispatchData().getCareOf());
		dispatch.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_NAME_KEY, orderBean.getDispatchData().getAddress());
		dispatch.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_ZIP_KEY, orderBean.getDispatchData().getZip());
		dispatch.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_CITY_KEY, orderBean.getDispatchData().getCity());
		dispatch.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_REGION_KEY, orderBean.getDispatchData().getProv());
		dispatch.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY, orderBean.getDispatchData().getCountryCode());		
		order.setShippingAddress(dispatch);
		
		int i = 1;
		ObjectNode lineItems = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		for (OrderItemBean item : orderBean.getItems()) {
			ObjectNode lineItem = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_SKU_KEY, item.getSKU());
			try {
				lineItem.put(XRoadsJsonKeys.ORDER_ITEM_MODEL_ID_KEY, xRoadsModule.getXRoadsCoreService().getEntityIdByModuleAndSourceId(Model.class, xRoadsModule, item.getStockModelId().toString()));
			} catch(NoResultException e) {
				throw new SyncException("Order contains models not present in xroads ");			
			}
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_DESCRIPTION_KEY, item.getDescription());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_NAME_KEY, item.getName()); 
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_QUANTITY_KEY, item.getQuantity());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_VAT_KEY, item.getVat().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_UNIT_TAXABLE_KEY, item.getUnitTaxable().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_UNIT_LISTING_KEY, item.getTaxable().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_UNIT_PRICE_KEY, item.getUnitPrice().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_TOTAL_TAXABLE_KEY, item.getTotalTaxable().subtract(item.getTotalDiscount()).toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_TOTAL_PRICE_KEY, item.getTotalPrice().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_VAT_SYSTEM_KEY, item.getVat_system_id());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_TAX_KEY, item.getTax().toPlainString());
			lineItems.put(Integer.toString(i++), lineItem);
		}
		order.setLineItems(lineItems);

		return order;
	}

	public static Customer getOrUpdateCustomerFromOrder(Order order, Customer customer) {
		if(customer == null) {
			customer = XRoadsUtils.getCustomerInstance();
			customer.setSourceId(order.getCustomerSourceId());
		}
		customer.setEmail(order.getCustomerEmail());
		customer.setFirstname(order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_FISTNAME_KEY).asText());
		customer.setLastname(order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_LASTNAME_KEY).asText());
		customer.setCompany(order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_COMPANY_KEY).asText());
		customer.setVatNumber(order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_VAT_NUMBER_KEY).asText());

		// insert cell and phone information in phones field 
		((ObjectNode)customer.getPhone()).put(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY).asText());
		((ObjectNode)customer.getPhone()).put(XRoadsJsonKeys.CUSTOMER_CELL_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_CELL_KEY).asText());
		((ObjectNode)customer.getPhone()).put(XRoadsJsonKeys.CUSTOMER_TELL_PREFIX_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_TELL_PREFIX_KEY).asText());
		((ObjectNode)customer.getPhone()).put(XRoadsJsonKeys.CUSTOMER_TELL_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_TELL_KEY).asText());

		// insert personal Adress with city, zip, country etc..
		ObjectNode personalAddress = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		personalAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_NAME_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_ADDRESS_NAME_KEY).asText());
		personalAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_ZIP_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_ADDRESS_ZIP_KEY).asText());
		personalAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_CITY_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_ADDRESS_CITY_KEY).asText());
		personalAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_REGION_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_ADDRESS_REGION_KEY).asText());
		personalAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY, order.getAnagrafica().path(XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY).asText());		
		((ObjectNode)customer.getAddresses()).set(XRoadsJsonKeys.CUSTOMER_ADDRESS_PERSONAL_KEY, personalAddress);

		return customer;
	}
	
}
