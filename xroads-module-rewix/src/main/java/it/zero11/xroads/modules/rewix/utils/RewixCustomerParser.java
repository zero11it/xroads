package it.zero11.xroads.modules.rewix.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Map;
import java.util.TreeMap;

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
	private Map<String, String> reverseMerchantMap;
	private Map<String, ObjectNode> tradeagentMap;
	
	public RewixCustomerParser(XRoadsRewixModule xroadsModule, Map<String, String> reverseMerchantMap) {
		this.xroadsModule = xroadsModule;
		this.reverseMerchantMap = reverseMerchantMap;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(localName.equals("user")) {
			currentCustomer = XRoadsUtils.getCustomerInstance();
			tradeagentMap = new TreeMap<String, ObjectNode>();
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
				case "tags":
					((ObjectNode) currentCustomer.getData()).set(XRoadsJsonKeys.CUSTOMER_TAGS_KEY, getCustomerTags(attributeValue));
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
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_SDI_KEY, attributeValue);
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
				case "anagrafica_reference":
					((ObjectNode) currentCustomer.getData()).put(XRoadsJsonKeys.CUSTOMER_ANAGRAFICA_REFERENCE, attributeValue);
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
					}else if(attributeValue.equals("dispatch")) {
						((ObjectNode) currentCustomer.getAddresses()).set(XRoadsJsonKeys.CUSTOMER_ADDRESS_SHIPPING_KEY, address);
					} else {
						((ObjectNode) currentCustomer.getAddresses()).set(attributeValue, address);
					}
				} else if(attributeName.equals("address_type")) {
					//IGNORE THIS FIELD
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
					case "addressee":
						attributeName = XRoadsJsonKeys.CUSTOMER_ADDRESSEE_KEY;
						break;
					case "number":
						attributeName = XRoadsJsonKeys.CUSTOMER_ADDRESS_NUMBER_KEY;
						break;
					case "cel":
						attributeName = XRoadsJsonKeys.CUSTOMER_CELL_KEY;
						break;
					case "city":
						attributeName = XRoadsJsonKeys.CUSTOMER_ADDRESS_CITY_KEY;
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
		} else if(localName.equals("tradeAgent")) {
			ObjectNode tradeAgent = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
			for (int i = 0; i < attributes.getLength(); i++) {
				String attributeName = attributes.getLocalName(i);
				String attributeValue = attributes.getValue(i);
				switch (attributeName) {
				case "merchant":
					tradeAgent.put(XRoadsJsonKeys.REWIX_CUSTOMER_MERCHANT_KEY, reverseMerchantMap.get(attributeValue));
					break;
				case "email":
					tradeAgent.put("email", attributeValue);
					break;
				}
			}
			if(!tradeAgent.has(XRoadsJsonKeys.REWIX_CUSTOMER_MERCHANT_KEY)) {
				throw new RuntimeException("No merchant for tradeagent " + tradeAgent.get("email").toString());
			}
			tradeagentMap.put(tradeAgent.get(XRoadsJsonKeys.REWIX_CUSTOMER_MERCHANT_KEY).toString(), tradeAgent);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("user")) {
			try {
				for(ObjectNode tradeAgent : tradeagentMap.values()) {
					((ArrayNode) currentCustomer.getTradeAgent()).add(tradeAgent);
				}
				xroadsModule.getXRoadsCoreService().consume(xroadsModule, currentCustomer);
			} catch (SyncException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private ArrayNode getCustomerTags(String tagsString) {
		ArrayNode tags = XRoadsUtils.OBJECT_MAPPER.createArrayNode();
		if (tagsString == null || tagsString.isEmpty()) {
			return tags;
		}
		List<String> tagList = new ArrayList<>();
		for (String tag : tagsString.split(",")) {
			if (!tag.startsWith("group:") && !tag.startsWith("consent:")) {
				tagList.add(tag);
			}
		}
		Collections.sort(tagList);
		tagList.forEach(tags::add);
		return tags;
	}
	
}
