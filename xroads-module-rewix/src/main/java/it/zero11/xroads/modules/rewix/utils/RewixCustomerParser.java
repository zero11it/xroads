package it.zero11.xroads.modules.rewix.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.Customer;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixCustomerParser extends DefaultHandler{

	private Customer currentCustomer;
	private XRoadsRewixModule xroadsModule;
	private SimpleDateFormat dateFromatter = new SimpleDateFormat("yyyy-MM-dd");
	
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
							currentCustomer.setDateOfBirth(dateFromatter.parse(attributeValue));
						} catch (ParseException e) {e.printStackTrace();}
					}
					break;
				case "validationdate":
					if(attributeValue != null && !attributeValue.trim().isEmpty()) {
						((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_VALIDATION_DATE, attributeValue);
					}
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
				case "roles":
					((ObjectNode) currentCustomer.getData()).put("roles", attributeValue);
					break;
				case "loyality_card":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_LOYALITY_CARD, attributeValue);
					break;
				case "eori_code":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_EORI_CODE, attributeValue);
					break;
				case "status":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_STATUS_KEY, Integer.valueOf(attributeValue));
					break;
				case "fiscal_code":
					currentCustomer.setFiscalCode(attributeValue);
					break;
					//					case "title":
					//
					//						break;
					//					case "fax":
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
					switch (attributeName) {
					case "countrycode":
						attributeName = XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY;
						break;
					case "prov":
						attributeName = XRoadsJsonKeys.CUSTOMER_ADDRESS_PROVINCE_KEY;
						break;
					case "careof":
						attributeName = XRoadsJsonKeys.CUSTOMER_CARE_OF_KEY;
						break;
					case "cel_prefix":
						attributeName = XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY;
						break;
					case "street":
						attributeName = XRoadsJsonKeys.CUSTOMER_ADDRESS_NAME_KEY;
						break;
					case "cap":
						attributeName = XRoadsJsonKeys.CUSTOMER_ADDRESS_ZIP_KEY;
						break;
					default:
						break;
					}
					address.put(attributeName, attributeValue);
				}
			}
		} else if(localName.equals("group")) {
			for (int i = 0; i < attributes.getLength(); i++) {
				String attributeName = attributes.getLocalName(i);
				String attributeValue = attributes.getValue(i);

				switch (attributeName) {
				case "name":
					JsonNode customerGroups = currentCustomer.getGroups().path(XRoadsJsonKeys.CUSTOMER_GROUPS_KEY);
					if(customerGroups.isMissingNode() || !customerGroups.isArray()) {
						((ObjectNode) currentCustomer.getGroups()).set(XRoadsJsonKeys.CUSTOMER_GROUPS_KEY, XRoadsUtils.OBJECT_MAPPER.createArrayNode());
					}
					((ArrayNode) currentCustomer.getGroups().get(XRoadsJsonKeys.CUSTOMER_GROUPS_KEY)).add(attributeValue);
					break;

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
