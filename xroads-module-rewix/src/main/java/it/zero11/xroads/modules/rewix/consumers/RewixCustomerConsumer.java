package it.zero11.xroads.modules.rewix.consumers;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.CustomerRevision;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.ProductNotFoundException;
import it.zero11.xroads.modules.rewix.api.RewixAPIException;
import it.zero11.xroads.modules.rewix.api.model.AddressBean;
import it.zero11.xroads.modules.rewix.api.model.AnagraficaBean;
import it.zero11.xroads.modules.rewix.api.model.UserConsentBean;
import it.zero11.xroads.modules.rewix.api.model.UserConsentsBean;
import it.zero11.xroads.modules.rewix.api.model.UserCreateBean;
import it.zero11.xroads.modules.rewix.api.model.UserListBean;
import it.zero11.xroads.modules.rewix.api.model.UserTradeAgentBean;
import it.zero11.xroads.modules.rewix.api.model.UserUpdateBean;
import it.zero11.xroads.modules.rewix.utils.GroupSearchBean;
import it.zero11.xroads.sync.EntityConsumer;
import it.zero11.xroads.sync.SyncException;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;

public class RewixCustomerConsumer extends AbstractRewixConsumer implements EntityConsumer<Customer> {

	public RewixCustomerConsumer(XRoadsRewixModule xRoadsModule) {
		super(xRoadsModule);
	}

	@Override
	public void consume(Customer customer) throws SyncException {

		final CustomerRevision revision = getXRoadsModule().getXRoadsCoreService().getEntityRevision(CustomerRevision.class,
				customer.getSourceId(),
				XRoadsUtils.getExternalReferenceVersion(customer, xRoadsModule));

		boolean sendEmail = xRoadsModule.getConfiguration().isSendEmailOnNewUsers();

		String rewixId = XRoadsUtils.getExternalReferenceId(customer, xRoadsModule);
		if (rewixId == null) {
			if (isEnabledCustomer(customer)) {
				rewixId = createCustomer(customer, sendEmail);
			}
		}
		if(rewixId != null) {

			log.debug("Updating rewix customer external reference " + " --> " + rewixId);
			getXRoadsModule().getXRoadsCoreService().updateExternalReferenceIdAndVersion(xRoadsModule, customer, rewixId, -1);


			if (revision == null || !revision.getEmail().equals(customer.getEmail()) || !revision.getLanguageCode().equals(customer.getLanguageCode())
					|| !revision.getData().equals(customer.getData())) {
				updateCustomerHead(customer, rewixId);		
			}

			if(revision == null || !revision.getAddresses().equals(customer.getAddresses())) {
				updateCustomerAddresses(customer, rewixId);		
				deleteCustomerAddresses(rewixId, customer, revision);
			}

			if(revision == null || !revision.getPhone().equals(customer.getPhone()) || !revision.getVatNumber().equals(customer.getVatNumber()) 
					|| !revision.getData().equals(customer.getData())) {
				updateCustomerData(customer, rewixId);		
			}

			// FIXME 
			if(revision == null || !revision.getGroups().equals(customer.getGroups()) || !revision.getData().equals(customer.getData())) {
				updateCustomerGroups(customer, rewixId);
				updateCustomerOptionalGroups(customer, rewixId);
				removeCustomerGroups(customer, revision, rewixId);
			}

			if(revision == null || !revision.getPaymentTerms().equals(customer.getPaymentTerms())) {
				updateCustomerPaymentTerms(customer, rewixId);
				removeCustomerPaymentTerms(customer, revision, rewixId);
			}

			if(revision == null || !revision.getData().equals(customer.getData())) {
				updateCustomerConsents(customer, rewixId);
			}

			if(revision == null || !revision.getTradeAgent().equals(customer.getTradeAgent())) {
				updateCustomerTradeAgent(customer, rewixId);
				removeCustomerTradeAgent(customer, revision, rewixId);
			}

			getXRoadsModule().getXRoadsCoreService().updateExternalReferenceId(xRoadsModule, customer, rewixId);
		}
	}

	protected void updateCustomerTradeAgent(Customer customer, String rewixId) throws RewixAPIException {
		ArrayNode tradeAgents = ((ArrayNode) customer.getTradeAgent());
		for(int i = 0; i < tradeAgents.size(); i++) {
			JsonNode tradeAgent = tradeAgents.get(i);
			String merchantId = tradeAgent.path(XRoadsJsonKeys.REWIX_CUSTOMER_MERCHANT_KEY).asText(null);
			String tradeAgentUsername = tradeAgent.path("email").asText(null);
			if(tradeAgentUsername == null && merchantId == null) {
				return;
			}
			if(tradeAgentUsername != null && merchantId == null) {
				throw new RuntimeException("No merchant associeted for tradeagent " + tradeAgentUsername);
			}
			UserTradeAgentBean user = new UserTradeAgentBean();			
			String tradeAgentRewix= xRoadsModule.getConfiguration().getRewixTradeAgentMap().get(tradeAgentUsername);
			Integer merchantRewix =  xRoadsModule.getConfiguration().getMerchantMap().get(merchantId);
			if(tradeAgentUsername != null && tradeAgentRewix == null) {
				throw new RuntimeException("No mapping for tradeagent " + tradeAgentUsername);
			}
			if(merchantId != null && merchantRewix == null) {
				throw new RuntimeException("No mapping for merchant " + merchantId);
			}
			user.setTradeAgentUsername(tradeAgentRewix);
			api.updateUserMerchantTradeAgent(user, merchantRewix.toString(), rewixId);
		}		
	}

	protected void removeCustomerTradeAgent(Customer customer, CustomerRevision revision, String rewixId) throws RewixAPIException {
		ArrayNode customerTradeAgents = ((ArrayNode) customer.getTradeAgent());
		ArrayNode revisionTradeAgents = null;
		if(revision != null && revision.getTradeAgent() != null) {
			revisionTradeAgents = ((ArrayNode) revision.getTradeAgent());
			for(int i = 0; i < revisionTradeAgents.size(); i++) {
				JsonNode revisionTradeAgent = revisionTradeAgents.get(i);
				String revisionTradeagentEmail = revisionTradeAgent.path("email").asText();
				Integer revisionMerchantId = xRoadsModule.getConfiguration().getMerchantMap()
						.get(revisionTradeAgent.path(XRoadsJsonKeys.REWIX_CUSTOMER_MERCHANT_KEY).asText(""));
				if(revisionMerchantId == null) {
					throw new RuntimeException("Is not possible to remove " + revisionTradeagentEmail + " for merchant "
							+ revisionMerchantId + " beacuse not exists in merchant map!");
				}
				boolean toRemove = true;
				for (int j = 0; j < customerTradeAgents.size(); j++) {
					JsonNode customerTradeAgent = customerTradeAgents.get(j);
					String customerTradeagentEmail = customerTradeAgent.path("email").asText();
					Integer customerMerchantId = xRoadsModule.getConfiguration().getMerchantMap()
							.get(customerTradeAgent.path(XRoadsJsonKeys.REWIX_CUSTOMER_MERCHANT_KEY).asText(""));
					if(revisionTradeagentEmail.equals(customerTradeagentEmail) && revisionMerchantId.equals(customerMerchantId)) {
						toRemove = false;
						break;
					}
				}
				if(toRemove) {
					UserTradeAgentBean user = new UserTradeAgentBean();
					user.setTradeAgentUsername(null);
					api.updateUserMerchantTradeAgent(user, revisionMerchantId.toString(), rewixId);
				}
			}
		}
	}

	private boolean isEnabledCustomer(Customer customer) {
		return customer.getData().path("enabled").asBoolean(true);
	}

	protected void updateCustomerAddresses(Customer customer, String rewixId) throws RewixAPIException {

		Iterator<Map.Entry<String, JsonNode>> addresses = customer.getAddresses().fields();
		while(addresses.hasNext()) {
			Map.Entry<String,JsonNode> address = addresses.next();
			updateCustomerAddress(customer, address.getValue(), address.getKey(), rewixId);
		}
	}
	
	protected void deleteCustomerAddresses(String rewixId, Customer customer, CustomerRevision revision) throws RewixAPIException {
		if(revision != null && revision.getAddresses() != null) {
			Iterator<Map.Entry<String, JsonNode>> addresses = revision.getAddresses().fields();
			ObjectNode customerAddresses = ((ObjectNode) customer.getAddresses());
			while(addresses.hasNext()) {
				Map.Entry<String,JsonNode> address = addresses.next();
				if(customerAddresses.path(address.getKey()).isMissingNode()) {
					AddressBean addressBean = new AddressBean();
					addressBean.setType(address.getKey());
					api.updateAddress(rewixId, addressBean);
				}
			}
		}
	}

	protected void updateCustomerAddress(Customer customer, JsonNode jsonAddress, String addressType, String rewixId) throws RewixAPIException {
		log.debug("Updating address for rewix customer" + customer.getSourceId());

		AddressBean address = new AddressBean();
		address.setType(addressType);
		address.setAddressee(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESSEE_KEY).asText());
		address.setCareOf(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_CARE_OF_KEY).asText());
		address.setStreet(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_NAME_KEY).asText());
		address.setNumber(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_NUMBER_KEY).asText());
		address.setZip(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_ZIP_KEY).asText());
		address.setCity(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_CITY_KEY).asText());
		address.setProvince(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_PROVINCE_KEY).asText());
		address.setCountryCode(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY).asText());		
		address.setMobilePhonePrefix(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY).asText());
		address.setMobilePhone(jsonAddress.path(XRoadsJsonKeys.CUSTOMER_CELL_KEY).asText());

		api.updateAddress(rewixId, address);
	}

	protected void updateCustomerHead(Customer customer, String rewixId) throws RewixAPIException {
		log.debug("Updating head for rewix customer " + customer.getSourceId());

		UserUpdateBean user = new UserUpdateBean();
		user.setEmail(customer.getEmail());		
		user.setAutoConfirmPaymentTermId(null);
		user.setIgnoreCartRules(false);
		user.setIgnoreRestrinctions(false);
		user.setLocaleCode(customer.getLanguageCode());
		user.setPermanentDiscount(new BigDecimal(0));
		user.setStatus(customer.getData().path(XRoadsJsonKeys.CUSTOMER_STATUS_KEY).asInt());
		user.setSendStatusUpdateEmail(true);
		
		List<String> tags = new ArrayList<>();
		if(customer.getData().path(XRoadsJsonKeys.CUSTOMER_TAGS_KEY).isArray()) {
			ArrayNode tagsArray = ((ArrayNode) customer.getData().path(XRoadsJsonKeys.CUSTOMER_TAGS_KEY));
			for(int i = 0; i < tagsArray.size(); i++) {
				String tagValue = tagsArray.get(i).asText(null);
				if(tagValue != null) {
					tags.add(tagValue);
				}
			}
		}
		user.setTags(tags);
		if (customer.getData().has(XRoadsJsonKeys.REWIX_CUSTOMER_TRADE_AGENT_KEY))
			user.setTradeAgentUsername(customer.getData().get(XRoadsJsonKeys.REWIX_CUSTOMER_TRADE_AGENT_KEY).asText());

		api.updateUserHead(rewixId, user);
	}

	protected void updateCustomerOptionalGroups(Customer customer, String rewixId) throws RewixAPIException, SyncException {

		String platform = customer.getData().path(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText(); 
		Set<GroupSearchBean> groups = new HashSet<>();
		if(customer.getGroups() != null) {
			JsonNode customerGroups = customer.getGroups().path(XRoadsJsonKeys.CUSTOMER_GROUPS_KEY);
			if(!customerGroups.isMissingNode() && customerGroups.isArray()) {
				for(JsonNode customerGroup : customerGroups) {
					groups.add(new GroupSearchBean(platform, customerGroup.asText()));
				}
			}
		}

		if (groups.size() > 0) {
			if (groups.size() > 0) {
				for(Map.Entry<GroupSearchBean, Integer> group : getGroupIds(groups).entrySet()) {
					UserListBean users = new UserListBean();
					users.setUsers(new ArrayList<String>());
					users.getUsers().add(rewixId);
					api.addUsersToGroup(users, group.getValue());
				}
			}

		}	
	}

	protected void updateCustomerGroups(Customer customer, String rewixId) throws SyncException {

		String platform = customer.getData().path(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText();
		Set<GroupSearchBean> groups = new HashSet<>();

		if(customer.getGroups() != null) {
			JsonNode customerGroups = customer.getGroups().path(XRoadsJsonKeys.CUSTOMER_GROUPS_KEY);
			if(!customerGroups.isMissingNode() && customerGroups.isArray()) {
				for(JsonNode customerGroup : customerGroups) {
					groups.add(new GroupSearchBean(platform, customerGroup.asText()));
				}
			}
		}

		if (groups.size() > 0) {
			for(Map.Entry<GroupSearchBean, Integer> group : getOrCreateGroupIds(groups).entrySet()) {
				UserListBean users = new UserListBean();
				users.setUsers(new ArrayList<String>());
				users.getUsers().add(rewixId);
				api.addUsersToGroup(users, group.getValue());
			}
		}	
	}

	protected void removeCustomerGroups(Customer customer, CustomerRevision customerRevision, String rewixId) throws SyncException {
		if(customerRevision != null) {
			Set<String> customerRevisionGroups = new HashSet<>();
			if(customerRevision.getGroups() != null) {
				JsonNode oldCustomerGroups = customerRevision.getGroups().get(XRoadsJsonKeys.CUSTOMER_GROUPS_KEY);
				if(oldCustomerGroups != null) {
					for(JsonNode customerGroup : oldCustomerGroups) {
						customerRevisionGroups.add(customerGroup.asText());
					}
				}
				JsonNode newCustomerGroups = customer.getGroups().get(XRoadsJsonKeys.CUSTOMER_GROUPS_KEY);
				if(newCustomerGroups != null && newCustomerGroups.isArray() && customerRevisionGroups.size() > 0) {
					for(JsonNode customerGroup : newCustomerGroups) {
						customerRevisionGroups.remove(customerGroup.asText());
					}
				}
			}
			if(customerRevisionGroups.size() > 0) {
				Set<GroupSearchBean> groupsToremove = new HashSet<>();
				String platform = customer.getData().path(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText();
				customerRevisionGroups.forEach(groupToRemove -> {
					groupsToremove.add(new GroupSearchBean(platform, groupToRemove));
				});
				if (groupsToremove.size() > 0) {
					for(Map.Entry<GroupSearchBean, Integer> groupToRemoveBean : getGroupIds(groupsToremove).entrySet()) {
						UserListBean users = new UserListBean();
						users.setUsers(new ArrayList<String>());
						users.getUsers().add(rewixId);
						api.removeUsersFromGroup(users, groupToRemoveBean.getValue());
					}
				}
			}
		}
	}

	protected void updateCustomerPaymentTerms(Customer customer, String rewixId) throws SyncException {
		if(customer.getPaymentTerms() != null) {		
			Map<String, Integer> paymentTermsMap = null;
			if(customer.getPaymentTerms() != null) {
				JsonNode paymentTermsNode =  customer.getPaymentTerms().path(XRoadsJsonKeys.CUSTOMER_PAYMENT_TERMS_KEY);
				if(!paymentTermsNode.isMissingNode() && paymentTermsNode.isArray()) {
					List<String> paymenttermsList = new ArrayList<String>();
					for(JsonNode paymentTerm : paymentTermsNode) {
						paymenttermsList.add(paymentTerm.asText());
					}
					try {
						paymentTermsMap = getPaymentTermsIds(paymenttermsList);
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				}
			}
			if (paymentTermsMap != null && paymentTermsMap.size() > 0) {
				for(Map.Entry<String, Integer> paymentTerm : paymentTermsMap.entrySet()) {
					UserListBean users = new UserListBean();
					users.setUsers(new ArrayList<String>());
					users.getUsers().add(rewixId);
					api.addUsersToPaymentTerm(users, paymentTerm.getValue());
				}
			}	
		}
	}

	protected void removeCustomerPaymentTerms(Customer customer, CustomerRevision customerRevision, String rewixId) throws SyncException {
		if(customerRevision != null) {
			Set<String> customerRevisionPaymentTerms = new HashSet<>();
			if(customerRevision.getPaymentTerms() != null) {
				JsonNode oldCustomerPaymentTerms = customerRevision.getPaymentTerms().get(XRoadsJsonKeys.CUSTOMER_PAYMENT_TERMS_KEY);
				if(oldCustomerPaymentTerms != null && oldCustomerPaymentTerms.isArray()) {
					for(JsonNode oldCustomerPaymentTerm : oldCustomerPaymentTerms) {
						customerRevisionPaymentTerms.add(oldCustomerPaymentTerm.asText());
					}
				}
			}
			if(customer.getPaymentTerms() != null) {
				JsonNode newCustomerPaymentTerms = customer.getPaymentTerms().path(XRoadsJsonKeys.CUSTOMER_PAYMENT_TERMS_KEY);
				if(newCustomerPaymentTerms != null && newCustomerPaymentTerms.isArray() && customerRevisionPaymentTerms.size() > 0) {
					for(JsonNode newCustomerPaymentTerm : newCustomerPaymentTerms) {
						customerRevisionPaymentTerms.remove(newCustomerPaymentTerm.asText());
					}
				}
			}
			if(customerRevisionPaymentTerms.size() > 0) {
				Map<String, Integer> paymentTermsMap;
				try {
					paymentTermsMap = getPaymentTermsIds(new ArrayList<String>(customerRevisionPaymentTerms));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}	
				for(Map.Entry<String, Integer> paymentTerm : paymentTermsMap.entrySet()) {
					UserListBean users = new UserListBean();
					users.setUsers(new ArrayList<String>());
					users.getUsers().add(rewixId);		
					api.removeUsersFromPaymentTerm(users, paymentTerm.getValue());
				}

			}
		}
	}

	protected void updateCustomerData(Customer customer, String rewixId) throws RewixAPIException, ProductNotFoundException {
		log.debug("Updating registry for rewix customer" + customer.getSourceId());

		AnagraficaBean registry = new AnagraficaBean();
		registry.setBusinessName(customer.getCompany());
		if (!customer.getPhone().path(XRoadsJsonKeys.CUSTOMER_CELL_KEY).isMissingNode()) {
			registry.setMobilePhone(customer.getPhone().path(XRoadsJsonKeys.CUSTOMER_CELL_KEY).asText());
		}
		if (!customer.getPhone().path(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY).isMissingNode()) {
			registry.setMobilePhonePrefix(customer.getPhone().path(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY).asText());
		}

		if (!customer.getPhone().path(XRoadsJsonKeys.CUSTOMER_CELL_KEY).isMissingNode()) {
			registry.setPhone(customer.getPhone().path(XRoadsJsonKeys.CUSTOMER_TELL_KEY).asText());
		}
		if (!customer.getPhone().path(XRoadsJsonKeys.CUSTOMER_CELL_PREFIX_KEY).isMissingNode()) {
			registry.setPhonePrefix(customer.getPhone().path(XRoadsJsonKeys.CUSTOMER_TELL_PREFIX_KEY).asText());
		}		

		if(!customer.getData().path(XRoadsJsonKeys.CUSTOMER_PEC_KEY).asText().isEmpty()) {
			registry.setPec(customer.getData().path(XRoadsJsonKeys.CUSTOMER_PEC_KEY).asText());
		}
		
		if(!customer.getData().path(XRoadsJsonKeys.CUSTOMER_SDI_KEY).asText().isEmpty()) {
			registry.setSdi(customer.getData().path(XRoadsJsonKeys.CUSTOMER_SDI_KEY).asText());
		}
		
		if(!customer.getData().path(XRoadsJsonKeys.CUSTOMER_LOYALITY_CARD).asText().isEmpty()) {
			registry.setLoyalityCard(customer.getData().path(XRoadsJsonKeys.CUSTOMER_LOYALITY_CARD).asText());
		}
		
		if(!customer.getData().path(XRoadsJsonKeys.CUSTOMER_ANAGRAFICA_REFERENCE).asText().isEmpty()) {
			registry.setAnagraficaReference(customer.getData().path(XRoadsJsonKeys.CUSTOMER_ANAGRAFICA_REFERENCE).asText());
		}
		
		registry.setFirstName(customer.getFirstname());
		registry.setLastName(customer.getLastname());

		registry.setVatNumber(customer.getVatNumber());

		api.updateUserAnagrafica(rewixId, registry);

	}

	public void updateCustomerConsents(Customer customer, String rewixId) throws RewixAPIException, ProductNotFoundException {
		log.debug("Updating customer consents for rewix customer" + customer.getSourceId());

		if (! customer.getData().has("consents")) { 
			return;
		}

		UserConsentsBean consents = new UserConsentsBean();
		consents.setUserConsents(new ArrayList<>());
		JsonNode consentsJson = customer.getData().get("consents");

		consentsJson.fields().forEachRemaining(customerItem -> {
			UserConsentBean bean = new UserConsentBean();			
			bean.setConsentId(Integer.parseInt(customerItem.getKey()));
			bean.setValue(consentsJson.get(customerItem.getKey()).asInt() == 1);
			consents.getUserConsents().add(bean);
		});

		api.updateUserConsents(rewixId, consents);
	}

	public String createCustomer(Customer customer, boolean sendEmail) throws RewixAPIException {
		log.info("Creating rewix customer " + customer.getSourceId());			

		UserCreateBean rewixUser = new UserCreateBean();
		rewixUser.setEmail(customer.getEmail());			
		rewixUser.setPassword(RandomStringUtils.random(8, true, true));
		rewixUser.setClausola1("on");			
		rewixUser.setLocaleCode(customer.getLanguageCode());			
		rewixUser.setPlatformUid(customer.getData().path(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText());
		String countryCode = customer.getData().path(XRoadsJsonKeys.CUSTOMER_ADDRESS_COUNTRY_KEY).asText();
		if(!countryCode.isEmpty())
			rewixUser.setCountryCode(countryCode);
		rewixUser.setSendActivationEmail(sendEmail);

		return api.createUser(rewixUser);		
	}
}