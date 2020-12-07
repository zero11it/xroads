package it.zero11.xroads.modules.rewix.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.Customer;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixCustomerParser extends DefaultHandler{

	private Customer currentCustomer;
	private XRoadsRewixModule xroadsModule;

	public RewixCustomerParser(XRoadsRewixModule xroadsModule) {
		this.xroadsModule = xroadsModule;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(localName.equals("user")) {
			currentCustomer = XRoadsUtils.getCustomerInstance();
			((ObjectNode) currentCustomer.getData()).set(XRoadsJsonKeys.CUSTOMER_TURNOVER_KEY, XRoadsUtils.OBJECT_MAPPER.createObjectNode());
			for (int i = 0; i < attributes.getLength(); i++) {
				String attributeName = attributes.getLocalName(i);
				String attributeValue = attributes.getValue(i);
				switch (attributeName.contains("turnover") ? "turnover" : attributeName) {
				case "username":
					currentCustomer.setUsername(attributeValue);
					currentCustomer.setSourceId(attributeValue);
					break;
				case "platform":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY, attributeValue);
					break;
				case "localecode":
					currentCustomer.setLanguageCode(attributeValue);
					break;
				case "email":
					currentCustomer.setEmail(attributeValue);
					break;
				case XRoadsJsonKeys.CUSTOMER_TAGS_KEY:
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_TAGS_KEY, attributeValue);
					break;
				case "firstname":
					currentCustomer.setFirstname(attributeValue);
					break;
				case "lastname":
					currentCustomer.setLastname(attributeValue);
					break;
				case "tel_prefix":
					((ObjectNode) currentCustomer.getPhone()).put(XRoadsJsonKeys.CUSTOMER_TELL_PREFIX_KEY, attributeValue);
					break;
				case "tel":
					((ObjectNode) currentCustomer.getPhone()).put(XRoadsJsonKeys.CUSTOMER_TELL_KEY, attributeValue);
					break;
				case "cel_prefix":
					((ObjectNode) currentCustomer.getPhone()).put(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY, attributeValue);
					break;
				case "cel":
					((ObjectNode) currentCustomer.getPhone()).put(XRoadsJsonKeys.CUSTOMER_CELL_KEY, attributeValue);
					break;
				case "note":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_NOTES_KEY, attributeValue);
					break;
				case "businessname":
					currentCustomer.setCompany(attributeValue);
					break;
				case "cfpiva":
					currentCustomer.setVatNumber(attributeValue);
					break;
				case "birth":
					if(attributeValue != null && !attributeValue.isEmpty()) {
						try {
							currentCustomer.setDateOfBirth(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss'Z'").parse(attributeValue));
						} catch (ParseException e) {e.printStackTrace();}
					}
					break;
				case "turnover":
					((ObjectNode) currentCustomer.getData().get(XRoadsJsonKeys.CUSTOMER_TURNOVER_KEY)).put(attributeName, attributeValue);
					break;
				case "pec":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_PEC_KEY, attributeValue);
					break;
				case "sdi":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.COSTOMER_SDI_KEY, attributeValue);
					break;
				case "website":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_WEBSITE_KEY, attributeValue);
					break;
				case "skype":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_SKYPE_KEY, attributeValue);
					break;
					//					case "title":
					//
					//						break;
					//					case "fax":
					//
					//						break;
					//					case "loyality_card":
					//
					//						break;
					//					case "channel":
					//
					//						break;
					//					case "brands_bought":
					//
					//						break;
					//					case "categories_bought":
					//
					//						break;
					//					case "last_invoice_no":
					//
					//						break;
					//					case "last_invoice_date":
					//
					//						break;
					//					case "last_invoice_brands":
					//
					//						break;
					//					case "last_invoice_categories":
					//
					//						break;
					//					case "last_invoice_amount":
					//
					//						break;
				}
			}
		} else if (localName.equals("address")){
			ObjectNode address = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
			for (int i = 0; i < attributes.getLength(); i++) {
				String attributeName = attributes.getLocalName(i);
				String attributeValue = attributes.getValue(i);
				if(attributeName.equals("type")) {
					if(attributeValue.equals("invoice")) {
						((ObjectNode) currentCustomer.getAddresses()).set(XRoadsJsonKeys.CUSTOMER_ADDRESS_BILLING_KEY, address);
					}
					if(attributeValue.equals("dispatch")) {
						((ObjectNode) currentCustomer.getAddresses()).set(XRoadsJsonKeys.CUSTOMER_ADDRESS_SHIPPING_KEY, address);
					}
				} else {
					address.put(attributeName, attributeValue);
				}
			}

		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("user")) {
			try {
				xroadsModule.getXRoadsCoreService().consume(xroadsModule, currentCustomer);
			} catch (SyncException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
