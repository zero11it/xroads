package it.zero11.xroads.modules.rewix.cron;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.Invoice;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.RewixAPI;
import it.zero11.xroads.modules.rewix.api.model.InvoiceBean;
import it.zero11.xroads.modules.rewix.api.model.InvoiceBean.InvoiceItemBean;
import it.zero11.xroads.modules.rewix.api.model.InvoiceFilterBean;
import it.zero11.xroads.modules.rewix.api.model.InvoiceListBean;
import it.zero11.xroads.modules.rewix.model.RewixParamType;
import it.zero11.xroads.modules.rewix.utils.RewixConversionUtils;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

@CronSchedule(hour = {0}, minute = {0}, second = {0})
public class RewixInvoiceCron extends AbstractXRoadsCronRunnable<XRoadsRewixModule>{

	private static final Logger log = Logger.getLogger(RewixInvoiceCron.class);
	private RewixAPI api; 

	@Override
	public void run() {
		log.info("Start Import Invoices");
		if(xRoadsModule.getXRoadsCoreService().getParameterAsBoolean(xRoadsModule, RewixParamType.ENABLE_EXPORT_INVOICES)) {
			api = new RewixAPI(xRoadsModule.getConfiguration().getUsername(), xRoadsModule.getConfiguration().getPassword(), xRoadsModule.getConfiguration().getEndpoint());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				InvoiceListBean invoiceList = api.getInvoiceList(new InvoiceFilterBean());
				for(Integer invoiceId : invoiceList.getInvoices()) {
					importRewixInvoice(api.getInvoice(invoiceId), df);
				}
			} catch (SyncException e1) {
				log.error("An error ocuring during invoice sync cron : " + e1.getMessage());
				e1.printStackTrace();
				throw new RuntimeException(e1);
			}
		}
		log.info("End import Invoices");
	}

	private void importRewixInvoice(InvoiceBean rewixInvoice, SimpleDateFormat df) throws SyncException {

		Invoice invoice = XRoadsUtils.getInvoiceInstance();
		invoice.setSourceId(rewixInvoice.getBillId().toString());
		invoice.setVatNumber(rewixInvoice.getVatNumber());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(rewixInvoice.getDate());	
		invoice.setYear(calendar.get(Calendar.YEAR));
		invoice.setCustomerSourceId(rewixInvoice.getUsername());
		invoice.setDocumentType(rewixInvoice.getType());
		invoice.setInvoiceNumber(rewixInvoice.getBillNo());
		int i = 1;
		ObjectNode lineItems = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		for (InvoiceItemBean item : rewixInvoice.getItems()) {
			ObjectNode lineItem = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_SKU_KEY, item.getSKU());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_MODEL_ID_KEY, item.getStockModelId() != null ? item.getStockModelId().toString() : null);
			lineItem.put(XRoadsJsonKeys.INVOICE_ITEM_DISCOUNT, item.getDiscount().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_NAME_KEY, item.getName()); 
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_QUANTITY_KEY, item.getQuantity());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_VAT_KEY, item.getVat().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_UNIT_TAXABLE_KEY, item.getUnitTaxable().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_UNIT_LISTING_KEY, item.getTaxable().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_UNIT_PRICE_KEY, item.getUnitPrice().toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_TOTAL_TAXABLE_KEY, item.getTotalTaxable().subtract(item.getTotalDiscount()).toPlainString());
			lineItem.put(XRoadsJsonKeys.ORDER_ITEM_TOTAL_PRICE_KEY, item.getTotalPrice().toPlainString());	
			lineItems.set(Integer.toString(i++), lineItem);
		}
		invoice.setLineItems(lineItems);

		ObjectNode invoiceAddress = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		invoiceAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_CITY_KEY, rewixInvoice.getCity());
		invoiceAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY, rewixInvoice.getCountryCode());
		invoiceAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_ZIP_KEY, rewixInvoice.getZip());
		invoiceAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_PROVINCE_KEY, rewixInvoice.getProv());
		invoiceAddress.put(XRoadsJsonKeys.CUSTOMER_COMPANY_KEY, rewixInvoice.getBillTo());
		invoiceAddress.put(XRoadsJsonKeys.CUSTOMER_ADDRESS_NAME_KEY, rewixInvoice.getAddress());
		((ObjectNode )invoice.getData()).set(XRoadsJsonKeys.CUSTOMER_ADDRESS_BILLING_KEY, invoiceAddress);

		((ObjectNode)invoice.getData()).put(XRoadsJsonKeys.PAYMENT_GATEWAY, rewixInvoice.getPaymentGatewayId());
		((ObjectNode)invoice.getData()).set(XRoadsJsonKeys.INVOICE_ORDER_IDS, XRoadsUtils.OBJECT_MAPPER.valueToTree(rewixInvoice.getOrderIds()));
		((ObjectNode)invoice.getData()).put(XRoadsJsonKeys.CUSTOMER_FISCAL_CODE, rewixInvoice.getFiscalCode());
		if(rewixInvoice.getBillReferenceId() != null) {
			((ObjectNode)invoice.getData()).put(XRoadsJsonKeys.INVOICE_REFERENCE, rewixInvoice.getBillReferenceId());
		}
		ArrayNode vats = XRoadsUtils.OBJECT_MAPPER.createArrayNode();
		rewixInvoice.getVatAmounts().forEach(vat -> {
			vats.add(vat.getVat().toPlainString());
		});
		((ObjectNode)invoice.getData()).set(XRoadsJsonKeys.ORDER_ITEM_VAT_KEY, vats);
		((ObjectNode)invoice.getData()).put(XRoadsJsonKeys.INVOICE_DATE, df.format(rewixInvoice.getDate()));
		((ObjectNode)invoice.getData()).put(XRoadsJsonKeys.ECREDIT, rewixInvoice.getEcredit().toPlainString());

		((ObjectNode)invoice.getTotals()).put(XRoadsJsonKeys.INVOICE_TOTAL, rewixInvoice.getTotal().toString());
		((ObjectNode)invoice.getTotals()).put(XRoadsJsonKeys.INVOICE_VAT_TOTAL, rewixInvoice.getVatAmountTotal().toString());

		for(Integer orderId :rewixInvoice.getOrderIds()) {
			Order o = xRoadsModule.getXRoadsCoreService().getEntity(Order.class, orderId.toString());
			if(o == null) {
				o = RewixConversionUtils.getOrderFromOrderBean(null, api.getOrder(Long.valueOf(orderId)), xRoadsModule);
			
				Customer customer = null;
				if(xRoadsModule.getXRoadsCoreService().getParameterAsBoolean(xRoadsModule, RewixParamType.ENABLE_EXPORT_CUSTOMERS)) {
					customer = xRoadsModule.getXRoadsCoreService().getEntity(Customer.class, o.getCustomerSourceId());
				}
				customer = RewixConversionUtils.getOrUpdateCustomerFromOrder(o, customer);

				xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, customer);
				xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, o);
				
			}
		}
		
		xRoadsModule.getXRoadsCoreService().consume(xRoadsModule, invoice);
	}

}
