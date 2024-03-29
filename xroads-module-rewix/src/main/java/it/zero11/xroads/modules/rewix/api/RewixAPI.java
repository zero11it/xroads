package it.zero11.xroads.modules.rewix.api;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.config.SocketConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;

import it.zero11.xroads.modules.rewix.api.model.AddressBean;
import it.zero11.xroads.modules.rewix.api.model.AnagraficaBean;
import it.zero11.xroads.modules.rewix.api.model.GroupBean;
import it.zero11.xroads.modules.rewix.api.model.InvoiceBean;
import it.zero11.xroads.modules.rewix.api.model.InvoiceFilterBean;
import it.zero11.xroads.modules.rewix.api.model.InvoiceListBean;
import it.zero11.xroads.modules.rewix.api.model.JAXBGenericWrapper;
import it.zero11.xroads.modules.rewix.api.model.OperationResponseBean;
import it.zero11.xroads.modules.rewix.api.model.OrderAttachmentsBean;
import it.zero11.xroads.modules.rewix.api.model.OrderBean;
import it.zero11.xroads.modules.rewix.api.model.OrderFilterBean;
import it.zero11.xroads.modules.rewix.api.model.OrderListBean;
import it.zero11.xroads.modules.rewix.api.model.OrderStatusInfo;
import it.zero11.xroads.modules.rewix.api.model.PaymentTermBean;
import it.zero11.xroads.modules.rewix.api.model.PaymentTermsBean;
import it.zero11.xroads.modules.rewix.api.model.ProductBean;
import it.zero11.xroads.modules.rewix.api.model.ProductImagesBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelLotBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelUpdateProductBean;
import it.zero11.xroads.modules.rewix.api.model.ProductRestrictionsBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagMetasBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagsBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTaxablesBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTranslationsBean;
import it.zero11.xroads.modules.rewix.api.model.UpdateDropshippingOrderStatusBean;
import it.zero11.xroads.modules.rewix.api.model.UserBean;
import it.zero11.xroads.modules.rewix.api.model.UserConsentsBean;
import it.zero11.xroads.modules.rewix.api.model.UserCreateBean;
import it.zero11.xroads.modules.rewix.api.model.UserListBean;
import it.zero11.xroads.modules.rewix.api.model.UserTradeAgentBean;
import it.zero11.xroads.modules.rewix.api.model.UserUpdateBean;
import it.zero11.xroads.modules.rewix.api.model.ValueWithKey;


public class RewixAPI {
	
	static {
		Calendar cal = Calendar.getInstance();
		cal.set(2018, 11, 12, 0, 0, 0);
		START_ORDER_DATE = cal;
		
		Calendar cal2 = Calendar.getInstance();
		cal2.set(2019, 1, 8, 0, 0, 0);
		START_ORDER_AUTH_DATE = cal2;
	}
	
	public static final Calendar START_ORDER_DATE;
	public static final Calendar START_ORDER_AUTH_DATE;
	
	private static final Logger log = Logger.getLogger(RewixAPI.class.getName());
	
	private static boolean debugHttpRequests = false;

	private String authHeader;
	private String baseUrl;
	public RewixAPI(String username, String password, String baseUrl) {
		this.authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
		this.baseUrl = baseUrl;
	}

	private String getAuthorizationHeader() {
		return authHeader;
	}
	
	private void checkResponseStatus(String method, Response response) throws RewixAPIException {		
		if (response.getStatus() >= 400) { 
			String errors = response.readEntity(String.class);
			log.log(Level.SEVERE, errors);
			if (response.getStatus() != 404 && response.getStatus() != 402 && response.getStatus() != 401)
				throw new RewixAPIException(response.getStatus(), errors);
			} 
	}
		
	private static Client restClient; 
	
	public static Client getRestClient()  {
		if (restClient == null){
			synchronized (RewixAPI.class) {
				if (restClient == null){
					try {
						ClientConfig configuration = new ClientConfig();
						configuration.property(ClientProperties.CONNECT_TIMEOUT, 10000);
						configuration.property(ClientProperties.READ_TIMEOUT, 600000);
						PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
						connectionManager.setDefaultMaxPerRoute(4);
						// Set soTimeout here to affect socketRead in the phase of ssl handshake. Note that
				        // the RequestConfig.setSocketTimeout will take effect only after the ssl handshake completed.
						connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(30000).build());
						configuration.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);
						configuration.connectorProvider(new ApacheConnectorProvider());
						
						Client client = ClientBuilder.newBuilder().withConfig(configuration).sslContext(SSLContext.getDefault()).register(JacksonJsonProvider.class).build();
						
						if (debugHttpRequests){
							try{
								client.register(new LoggingFeature(Logger.getLogger("it.zero11.rewix.sync"), Level.INFO, Verbosity.PAYLOAD_ANY, null));
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						
						restClient = client;
					} catch (NoSuchAlgorithmException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return restClient;
	}
	
	public UserBean getUserByEmail(String platformUid, String email) throws RewixAPIException{
		try {
			final Response response = getRestClient()
					.target(baseUrl)
					.path("/restful/user/email/" + platformUid + "/" + URLEncoder.encode(email, StandardCharsets.UTF_8.name()))
					.request()
					.header("Authorization", getAuthorizationHeader())
					.accept(MediaType.APPLICATION_XML)
					.get();
	
			checkResponseStatus("getUserByEmail", response);	
			final UserBean user = response.readEntity(UserBean.class);	
			return user;
		}catch (UnsupportedEncodingException e) {
			throw new RewixAPIException(-1, e.getMessage());
		}
	}
	
	public void updateUserMerchantTradeAgent(UserTradeAgentBean userBean, String merchantId, String tradeAgentUsername) throws RewixAPIException {		
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/user/merchant/" + merchantId + "/tradeagent/" + tradeAgentUsername)
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(userBean));

		checkResponseStatus("updateUserMerchantTradeAgent", response);
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (!p.getStatus()) {
			throw new RewixAPIException(200, p.getMessage());
		}
	}
	
	public OrderListBean getAllOrders(OrderFilterBean orderFilterBean) throws RewixAPIException {
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/orders/list")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(orderFilterBean));
		
		checkResponseStatus("getAllOrders", response);		
		final OrderListBean orders = response.readEntity(OrderListBean.class);	
		return orders;
	}
	
	public OrderBean getOrder(Long orderId) throws RewixAPIException{
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/orders/get/" + orderId)
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.get();

		checkResponseStatus("getOrder", response);	
		final OrderBean order = response.readEntity(OrderBean.class);	
		return order;
	}
	
	public Integer updateProduct(ProductBean product) throws RewixAPIException {
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/product/update")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(product));
		
		checkResponseStatus("updateProduct", response);
		
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (p.getStatus()) {
			return p.getIds().get(0);
		}else {
			throw new RewixAPIException(200, p.getMessage());
		}
	}
	
	public void updateImages(ProductImagesBean images) throws RewixAPIException, ProductNotFoundException{
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/product/images/update")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(images));

		checkResponseStatus("updateImages", response);
		
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (!p.getStatus()) {
			throw new RewixAPIException(200, p.getMessage());
		}
	}
	
	public Map<String, String> getTargetLocales() throws RewixAPIException {
		Map<String, String> langs = new HashMap<>();
		Response response = null;
		response = getRestClient()
				.target(baseUrl)
				.path("/restful/export/locales")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.get();
		checkResponseStatus("getLocales", response);
		final JAXBGenericWrapper<ValueWithKey> locales = response.readEntity(JAXBGenericWrapper.class);
		locales.getItems().forEach(value -> {
			langs.put(value.getKey(), value.getValue());
		});
	 
	return langs;
	}
	
	public JAXBGenericWrapper<ValueWithKey> getTargetTags() throws RewixAPIException {
		Response response = null;
		response = getRestClient()
				.target(baseUrl)
				.path("/restful/export/tags")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.get();
		checkResponseStatus("getLocales", response);
		final JAXBGenericWrapper<ValueWithKey> tags = response.readEntity(JAXBGenericWrapper.class);
		return tags;
	}
	
	public Integer updateProductVariant(ProductModelBean productModel) throws RewixAPIException{		
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/product/model/update")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(productModel));
		
		checkResponseStatus("createProductVariant", response);
		
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (p.getStatus()) {
			return p.getIds().get(0);
		}else {
			throw new RewixAPIException(200, p.getMessage());
		}
	}
	
	public void updateProductTagMetaTranslations(ProductTagMetasBean bean) throws RewixAPIException{		
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/product/tagmeta/update")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(bean));	
		checkResponseStatus("update Tag translations", response);	
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (!p.getStatus()) {
			throw new RewixAPIException(200, p.getMessage());
		}
	}

	public void updateProductModelLotBean(ProductModelLotBean productModelLotBean) throws RewixAPIException, ProductNotFoundException{
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/product/model/lot/update")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(productModelLotBean));

		checkResponseStatus("lotUpdate " + productModelLotBean.getStockModelId() + " - qty " + productModelLotBean.getAmount(), response);
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (!p.getStatus()) {
			if(p.getMessage().contains("Stock Product Id: ") &&  p.getMessage().contains(" not found.")) {
				//throw new ProductNotFoundException(productTagsBean.getStockProductId());
				Integer stockProductId = Integer.valueOf(p.getMessage().split(" ")[3]);
				throw new ProductNotFoundException(stockProductId);
			}else{
				throw new RewixAPIException(200, p.getMessage());
			}
		}
	}
	
	public void moveModelFromProduct(ProductModelUpdateProductBean bean) throws RewixAPIException {	
		final Response response = getRestClient()
		.target(baseUrl)
		.path("/restful/product/model/product/update")
		.request()
		.header("Authorization", getAuthorizationHeader())
		.accept(MediaType.APPLICATION_XML)
		.post(Entity.xml(bean));	
		checkResponseStatus("move model", response);
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (!p.getStatus()) {
			throw new RewixAPIException(200, p.getMessage());
		}
	}
	
	public void updateProductTaxables(ProductTaxablesBean productTaxablesBean) throws RewixAPIException, ProductNotFoundException{		
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/product/prices/update")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(productTaxablesBean));
		
		checkResponseStatus("priceUpdate", response);
		
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (!p.getStatus()) {
			if(p.getMessage().equals("Stock Product Id: " + productTaxablesBean.getStockProductId() +" not found.")) {
				throw new ProductNotFoundException(productTaxablesBean.getStockProductId());
			}else{
				throw new RewixAPIException(200, p.getMessage());
			}
		}	
	}
	
	public void updateProductTags(ProductTagsBean productTagsBean) throws RewixAPIException, ProductNotFoundException{		
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/product/tags/update")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(productTagsBean));		
		checkResponseStatus("tagUpdate", response);	
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (!p.getStatus()) {
			throw new RewixAPIException(200, p.getMessage());
		}
	}	


	public void updateOrderAttachment(OrderAttachmentsBean attachments) throws RewixAPIException {
		final Response response = getRestClient()
				.target(baseUrl)
				.path("/restful/orders/attachment")
				.request()
				.header("Authorization", getAuthorizationHeader())
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(attachments));	
		checkResponseStatus("update attachment", response);
		final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
		if (!p.getStatus()) {
			throw new RewixAPIException(200, p.getMessage());
		}
	}

public void updateOrderStatus(UpdateDropshippingOrderStatusBean bean) throws RewixAPIException {
	log.info("Updating rewix order " + bean);
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/orders/dropshipping/status")
			.request()
			.header("Content-Type", "application/xml")
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(bean));

	checkResponseStatus("updateOrderStatus", response);	
	//FIXME
	response.readEntity(OrderStatusInfo.class);
}

public Integer updateUserGroup(GroupBean groupBen) throws RewixAPIException, ProductNotFoundException{		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/group/update")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(groupBen));		
	checkResponseStatus("updateUserGroup", response);
	// FIXME to understand if this is correct !!
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (p.getStatus()) {
		return p.getIds().get(0);
	}else {
		throw new RewixAPIException(200, p.getMessage());
	}
}		

public ProductBean getProduct(Long productId) throws RewixAPIException{
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/product/id/" + productId)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.get();

	checkResponseStatus("getProduct", response);	
	final ProductBean productBean = response.readEntity(ProductBean.class);	
	return productBean;
}

public ProductBean getProduct(String sku) throws RewixAPIException{
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/product/sku/" + sku)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.get();

	checkResponseStatus("getProduct", response);	
	final ProductBean productBean = response.readEntity(ProductBean.class);	
	return productBean;
}

public ProductTagsBean getTags(Long productId) throws RewixAPIException{
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/product/tags/product/" + productId)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.get();

	checkResponseStatus("getTags", response);	
	final ProductTagsBean productTags = response.readEntity(ProductTagsBean.class);	
	return productTags;
}

public void updateAddress(String username, AddressBean adressBean) throws RewixAPIException{		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/address/" + username)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(adressBean));		
	checkResponseStatus("update Adress", response);
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (!p.getStatus()) {
		throw new RewixAPIException(200, p.getMessage());
	}
}	

public void updateUserHead(String username, UserUpdateBean userBean) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/user/" + username)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(userBean));		
	checkResponseStatus("update user head", response);
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (!p.getStatus()) {
		throw new RewixAPIException(200, p.getMessage());
	}
}

public void updateUserAnagrafica(String username, AnagraficaBean anagraficaBean) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/anagrafica/" + username)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(anagraficaBean));		
	checkResponseStatus("update user anagrafica", response);
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (!p.getStatus()) {
		throw new RewixAPIException(200, p.getMessage());
	}
}

public void updateUserConsents(String username, UserConsentsBean uerConsentsaBean) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/consent/update/" + username)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(uerConsentsaBean));		
	checkResponseStatus("update user consents", response);
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (!p.getStatus()) {
		throw new RewixAPIException(200, p.getMessage());
	}
}

public String createUser(UserCreateBean uerCreateBean) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/create")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(uerCreateBean));		
	checkResponseStatus("create user", response);
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (!p.getStatus()) {
		throw new RewixAPIException(200, p.getMessage());
	}else {
		return p.getMessage();
	}
}

public Integer getUserGroup(String platform, String name) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/group/get/" + platform + "/" + name)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.get();	
	checkResponseStatus("getUserGroup", response);
	final GroupBean group = response.readEntity(GroupBean.class);
	if(group == null)
		return null;
	return group.getId();
}

public void addUsersToGroup(UserListBean users, Integer group) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/group/id/" + group + "/add")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(users));		
	checkResponseStatus("addUsersToGroup", response);
}

public void removeUsersFromGroup(UserListBean users, Integer group) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/group/id/" + group + "/remove")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(users));		
	checkResponseStatus("removeUsersToGroup", response);
}

public void updateProductTranslations(ProductTranslationsBean productTranslationsBean) throws RewixAPIException, ProductNotFoundException{		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/product/translations/update")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(productTranslationsBean));

	checkResponseStatus("translations update", response);
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (!p.getStatus()) {
		throw new RewixAPIException(200, p.getMessage());
	}
}

public void updatePaymentTerm(PaymentTermBean paymentTermsBean) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/paymentterm/update")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(paymentTermsBean));

	checkResponseStatus("updatePaymentTermn", response);
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (!p.getStatus()) {
		throw new RewixAPIException(200, p.getMessage());
	}
}

public PaymentTermsBean getPaymentTerms(String name) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/paymentterm/list")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.get();	
	checkResponseStatus("getPaymentTerms", response);
	final PaymentTermsBean paymentTerms = response.readEntity(PaymentTermsBean.class);
	return paymentTerms;
}

public void addUsersToPaymentTerm(UserListBean users, Integer payment_term_id) throws RewixAPIException {
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/paymentterm/id/" + payment_term_id + "/add")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(users));		
	checkResponseStatus("addUserToPaymentTerm", response);
}

public void removeUsersFromPaymentTerm(UserListBean users, Integer payment_term_id) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/paymentterm/id/" + payment_term_id + "/remove")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(users));		
	checkResponseStatus("removeUsersFromPaymentTerm", response);
}

public Integer getPaymentTerm(String name) throws RewixAPIException, UnsupportedEncodingException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/paymentterm/get/" + name)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.get();	
	
	checkResponseStatus("getPaymentTerm", response);
	final PaymentTermBean paymentterm = response.readEntity(PaymentTermBean.class);
	if(paymentterm == null)
		return null;
	return paymentterm.getId();
}

public InputStream getCustomers(String startSyncDateTime) throws RewixAPIException {
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/export")
			.queryParam("since", startSyncDateTime != null ? startSyncDateTime : "")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.get();	
	checkResponseStatus("getCustomers", response);
	return response.readEntity(InputStream.class);
}

public void updateProductRestrictions(ProductRestrictionsBean productRestrictionsBean) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/product/restrictions/update")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(productRestrictionsBean));

	checkResponseStatus("updateProductRestrictions", response);
	final OperationResponseBean p = response.readEntity(OperationResponseBean.class);
	if (!p.getStatus()) {
		throw new RewixAPIException(200, p.getMessage());
	}
}

public InvoiceBean getInvoice(Integer id) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/billing/invoice/get/" + id)
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.get();	
	checkResponseStatus("getInvoice", response);
	final InvoiceBean invoiceBean = response.readEntity(InvoiceBean.class);
	return invoiceBean;
}

public InvoiceListBean getInvoiceList(InvoiceFilterBean filterBean) throws RewixAPIException {		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/billing/list")
			.request()
			.header("Authorization", getAuthorizationHeader())
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.xml(filterBean));	
	checkResponseStatus("getInvoiceList", response);
	final InvoiceListBean invoiceListBean = response.readEntity(InvoiceListBean.class);
	return invoiceListBean;
}

} 

