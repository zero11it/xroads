package it.zero11.xroads.modules.rewix.consumers;

import it.zero11.xroads.model.Customer;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.sync.EntityConsumer;

public class RewixCustomerConsumer extends AbstractRewixConsumer implements EntityConsumer<Customer> {

	public RewixCustomerConsumer(XRoadsRewixModule xRoadsModule) {
		super(xRoadsModule);
	}

	@Override
	public void consume(Customer t) {
		/*
		final Customer customer = MessageUtils.getMessageBodyAs(message, Customer.class);
		final CustomerRevision customerRevision = MessageUtils.getMessageRevisionAs(message, CustomerRevision.class);

		RewixCustomerApiClient client = new RewixCustomerApiClient(verticle.getVertx(), verticle.getEcomService(), verticle.getWebClient(), verticle.getRewixConfig());	
		verticle.getEcomService().getCustomer(customer.getSourceId(), json -> {
			if (json.succeeded()) {				
				if (json == null || ! isAlreadyCustomer(json.result())) {
					if (isEnabledCustomer(json.result())) {
						String platform = json.result().getJsonObject("data").getString(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY);
						boolean sendEmail = verticle.getRewixConfig().isSendEmailOnNewUsers();
						client.createCustomer(platform, customer, sendEmail, h -> {
							if (h.succeeded()) {
								Customer c = h.result();
								log.debug("createCustomer completed" + message.body());																					
								client.updateCustomer(c, null, h2 -> {
									if (h2.failed()) 
										LogUtils.logError(log, "updateCustomer", message, h2.cause());
									else
										log.debug("updateCustomer completed" + message.body());//																			
								});		
							}										
							else
								LogUtils.logError(log, "createCustomer", message, h.cause());																		
						});	
					}										
				} else if (!customer.getVersion().equals(XroadsUtils.getExternalReferenceVersion(customer.getExternalReferences(), xRoadsModule.getName()))) {
					if (verticle.getRewixConfig().isUpdateCustomers()) {
						client.updateCustomer(customer, customerRevision, h -> {
							if (h.succeeded())
								log.debug("updateCustomer completed" + message.body());
							else
								LogUtils.logError(log, "updateCustomer", message, h.cause());							
						});			
					}else {
						final String id = XroadsUtils.getExternalReferenceId(customer.getExternalReferences(), xRoadsModule.getName());
						verticle.getEcomService().updateExternalReference(Customer.class.getSimpleName(), customer.getSourceId(), xRoadsModule.getName(), id, hx -> {
							log.debug("External ref updated");
							XroadsUtils.setExternalReference(customer.getExternalReferences(), xRoadsModule.getName(), id, customer.getVersion());
						});	
					}
				}
			} else
				log.error("processCustomer " + message.body() + " --> " + json.cause().getMessage());
		});
		*/
	}
/*
	private boolean isAlreadyCustomer(JsonNode customer) {
		return customer.get("external_references").has(xRoadsModule.getName());
	}

	private boolean isEnabledCustomer(JsonNode customer) {
		return customer.get("data").get("enabled") == null || customer.get("data").get("enabled").asBoolean();
	}


	public void updateCustomer(Customer customer, CustomerRevision revision, Handler<AsyncResult<Void>> handler) {
		log.info("Updating rewix customer " + customer.getSourceId());

		Future<Void> future = Future.future();
		future.setHandler(handler);

		Future<String> customerFuture = updateCustomerHead(customer);		
		customerFuture
		.compose(v -> {
			Future<Void> f = Future.future();
			log.debug("Updating rewix customer address " + customer.getSourceId());
			updateCustomerAddresses(customer).setHandler(f.completer());			
			return f;
		})
		.compose(v -> {
			Future<Void> f = Future.future();
			log.debug("Updating rewix customer registry " + customer.getSourceId());
			updateCustomerData(customer).setHandler(f.completer());		
			return f;
		})
		.compose(v -> {
			Future<Void> f = Future.future();
			log.debug("Updating rewix customer groups " + customer.getSourceId());				
			updateCustomerGroups(customer).setHandler(f.completer());		
			return f;
		})	
		.compose(v -> {
			Future<Void> f = Future.future();
			log.debug("Updating rewix customer optional groups " + customer.getSourceId());				
			updateCustomerOptionalGroups(customer).setHandler(f.completer());
			return f;
		})
		.compose(v -> {
			Future<Void> f = Future.future();
			log.debug("Updating rewix consents " + customer.getSourceId());				
			updateCustomerConsents(customer, revision).setHandler(f.completer());
			return f;
		})
		.compose(v -> {	//Aggiorno la versione	
			final String id = XroadsUtils.getExternalReferenceId(customer.getExternalReferences(), xRoadsModule.getName());			
			ecomService.updateExternalReference(Customer.class.getSimpleName(), customer.getSourceId(), xRoadsModule.getName(), id, hx -> {
				log.debug("External ref updated");
				XroadsUtils.setExternalReference(customer.getExternalReferences(), xRoadsModule.getName(), id, customer.getVersion());
				future.complete();
			});			
		}, future); 


	}

	protected Future<Void> updateCustomerAddresses(Customer customer) {
		Future<Void> future = Future.future();

		List<JsonNode> addresses = new ArrayList<>();
		customer.getAddresses().fields().forEachRemaining(customerr -> {
			addresses.add(customerr.getValue());
		});

		if (addresses.size() > 0) {
			if (addresses.size() == 1) {
				JsonNode address = addresses.get(0);
				JsonNode invoice = address.deepCopy();
				((ObjectNode)invoice).put("type", "invoice");
				addresses.add(invoice);
				JsonNode dispatch = address.deepCopy();
				((ObjectNode)dispatch).put("type", "dispatch");
				addresses.add(dispatch);
			} else {
				JsonNode address = addresses.get(0);
				JsonNode invoice = address.deepCopy();
				((ObjectNode)invoice).put("type", "invoice");
				addresses.add(invoice);
				address = addresses.get(1);
				JsonNode dispatch = address.deepCopy();
				((ObjectNode)dispatch).put("type", "dispatch");
				addresses.add(dispatch);
			}

			@SuppressWarnings("rawtypes")
			List<Future> futures = new ArrayList<>();			
			for (JsonNode address : addresses) {			
				futures.add(updateCustomerAddress(customer, address));			
			}
			CompositeFuture.all(futures)		
			.setHandler(x -> {
				if (x.succeeded()) {							
					future.complete();
				} else
					future.fail(x.cause());						
			});	
		} else
			future.complete();

		return future;
	}

	protected Future<Void> updateCustomerAddress(Customer customer, JsonNode jsonAddress) {
		log.debug("Updating address for rewix customer" + customer.getSourceId());
		Future<Void> future = Future.future();

		AddressBean address = new AddressBean();
		address.setType(jsonAddress.get("type").asText());
		address.setAddressee(jsonAddress.get("addressee").asText());
		address.setCareOf(jsonAddress.get("careOf").asText());
		address.setStreet(jsonAddress.get("street").asText());
		address.setNumber(jsonAddress.get("number").asText());
		address.setZip(jsonAddress.get("zip").asText());
		address.setCity(jsonAddress.get("city").asText());
		address.setProvince(jsonAddress.get("province").asText());
		address.setCountryCode(jsonAddress.get("countryCode").asText());

		String phone = (jsonAddress.get("mobilePhone").asText() != null ? 
				jsonAddress.get("mobilePhone").asText() :
					customer.getPhone().get("mobile").asText());

		if (phone != null) {
			if (phone.contains(" ")) {
				String[] p = phone.split(" ", 2);
				address.setMobilePhonePrefix(p[0]);
				address.setMobilePhone(p[1]);
			} else
				address.setMobilePhone(phone);
		}
		final String id = XroadsUtils.getExternalReferenceId(customer, xRoadsModule.getName());

		api.updateAdress(id, address);
		return future;
	}

	protected Future<String> updateCustomerHead(Customer customer) {
		log.debug("Updating head for rewix customer " + customer.getSourceId());
		Future<String> future = Future.future();

		UserBean user = new UserBean();
		user.setEmail(customer.getEmail());		
		user.setAutoConfirmPaymentTermId(null);
		user.setIgnoreCartRules(false);
		user.setIgnoreRestrinctions(false);
		user.setLocaleCode(customer.getLanguageCode());			
		user.setPermanentDiscount(new BigDecimal(0));
		Boolean online = customer.getData().get(XRoadsJsonKeys.CUSTOMER_ENABLED_KEY).asBoolean();
		if (online != null)
			user.setStatus(online ? 2 : 3);

		List<String> tags = new ArrayList<>();
		if (customer.getData().has(XRoadsJsonKeys.REWIX_CUSTOMER_TAGS_KEY)) {
			JsonNode tagsArray = customer.getData().get(XRoadsJsonKeys.REWIX_CUSTOMER_TAGS_KEY);
			tagsArray.fields().forEachRemaining(tag -> {
				tags.add(tag.getValue().asText());		
			});		
		}

		user.setTags(tags);
		if (customer.getData().has(XRoadsJsonKeys.REWIX_CUSTOMER_TRADE_AGENT_KEY))
			user.setTradeAgentUsername(customer.getData().get(XRoadsJsonKeys.REWIX_CUSTOMER_TRADE_AGENT_KEY).asText());

		final String id = XroadsUtils.getExternalReferenceId(customer, xRoadsModule.getName());
		api.updateUserHead(id, user);
		return future;
	}

	protected Future<Void> updateCustomerOptionalGroups(Customer customer) {
		final Future<Void> future = Future.future();
		String platform = customer.getData().get(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText(); //.split(",");
		Set<GroupSearchBean> groups = new HashSet<>();
		customer.getGroups().fields().forEachRemaining(customerItem -> {
			if (! customerItem.getValue().asBoolean())
				groups.add(new GroupSearchBean(platform, customerItem.getKey()));
		});

		if (groups.size() > 0) {
			getGroupIds(groups).setHandler(groupIds -> {
				if (groupIds.succeeded()) { 
					Map<GroupSearchBean, Integer> map = groupIds.result();
					@SuppressWarnings("rawtypes")
					List<Future> futures = new ArrayList<>();
					if (map.size() > 0)
						for(GroupSearchBean group : map.keySet()) {
							if (map.get(group) != null) {
								UserListBean users = new UserListBean();
								users.addUser(XroadsUtils.getExternalReferenceId(customer.getExternalReferences(), xRoadsModule.getName()));						
								futures.add(addToGroupById(users, map.get(group)));									
								CompositeFuture.all(futures)		
								.setHandler(x -> {
									if (x.succeeded()) {							
										future.complete();
									} else
										future.fail(x.cause());						
								});	
							} else
								future.complete();
						}
					else
						future.complete();
				} else
					future.fail(groupIds.cause());
			});	
		} else
			future.complete();

		return future;		
	}

	protected Future<Void> updateCustomerGroups(Customer customer) {
		Future<Void> future = Future.future();
		String platform = customer.getData().get(XRoadsJsonKeys.REWIX_CUSTOMER_PLATFORMS_KEY).asText();//.split(",");
		Set<GroupSearchBean> groups = new HashSet<>();
		customer.getGroups().fields().forEachRemaining(customerItem -> {
			if (customerItem.getValue().asBoolean())
				groups.add(new GroupSearchBean(platform, customerItem.getKey()));
		});

		if (groups.size() > 0) {
			getOrCreateGroupIds(groups).setHandler(groupIds -> {
				if (groupIds.succeeded()) { 
					Map<GroupSearchBean, Integer> map = groupIds.result();
					@SuppressWarnings("rawtypes")
					List<Future> futures = new ArrayList<>();
					for(GroupSearchBean group : map.keySet()) {			
						UserListBean users = new UserListBean();
						users.addUser(XroadsUtils.getExternalReferenceId(customer.getExternalReferences(), xRoadsModule.getName()));						
						futures.add(addToGroupById(users, map.get(group)));									
						CompositeFuture.all(futures)		
						.setHandler(x -> {
							if (x.succeeded()) {							
								future.complete();
							} else
								future.fail(x.cause());						
						});	
					}
				} else
					future.fail(groupIds.cause());
			});	
		} else
			future.complete();

		return future;		
	}

	protected Future<Void> updateCustomerData(Customer customer) {
		log.debug("Updating registry for rewix customer" + customer.getSourceId());
		Future<Void> future = Future.future();

		AnagraficaBean registry = new AnagraficaBean();
		registry.setBusinessName(customer.getCompany());
		if (customer.getPhone().get("mobile") != null) {
			if (customer.getPhone().get("mobile").asText().contains(" ")) {
				String[] p = customer.getPhone().get("mobile").asText().split(" ", 2);
				registry.setMobilePhonePrefix(p[0]);
				registry.setMobilePhone(p[1]);
			} else
				registry.setMobilePhone(customer.getPhone().get("mobile").asText());
		}
		if (customer.getPhone().get("phone") != null) {
			if (customer.getPhone().get("phone").asText().contains(" ")) {
				String[] p = customer.getPhone().get("phone").asText().split(" ", 2);
				registry.setPhonePrefix(p[0]);
				registry.setPhone(p[1]);
			} else
				registry.setPhone(customer.getPhone().get("phone").asText());
		}
		registry.setVatNumber(customer.getVatNumber());

		final String id = XroadsUtils.getExternalReferenceId(customer, xRoadsModule.getName());

		api.updateUserAnagrafica(id, registry);

		return future;
	}

	public Future<Void> updateCustomerConsents(Customer customer, CustomerRevision revision) {
		log.debug("Updating customer consents for rewix customer" + customer.getSourceId());
		Future<Void> future = Future.future();

		if (! customer.getData().has("consents")) { 
			future.complete();
			return future;
		}

		if (revision != null) {
			JsonNode consentsJson = customer.getData().get("consents");
			JsonNode consentsJsonRevision = revision.getData().get("consents");
			if (consentsJson.equals(consentsJsonRevision)) {
				future.complete();
				return future;
			}				
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

		final String id = XroadsUtils.getExternalReferenceId(customer, xRoadsModule.getName());
		api.updateUserConsents(id, consents);
		return future;
	}

	public void createCustomer(String platform, Customer customer, boolean sendEmail, Handler<AsyncResult<Customer>> handler) {
		log.info("Creating rewix customer " + customer.getSourceId());			

		UserCreateBean rewixUser = new UserCreateBean();
		rewixUser.setEmail(customer.getEmail());			
		rewixUser.setPassword(generatePassword(8));
		rewixUser.setClausola1("on");			
		rewixUser.setLocaleCode(customer.getLanguageCode());			
		rewixUser.setPlatformUid(platform);
		rewixUser.setCountryCode(customer.getData().get("countryCode").asText());
		rewixUser.setSendActivationEmail(sendEmail);

		api.createUser(rewixUser);		
	}*/
}
