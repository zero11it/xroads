package it.zero11.xroads.modules.rewix.api;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
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

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import it.zero11.xroads.modules.rewix.api.model.AddressBean;
import it.zero11.xroads.modules.rewix.api.model.AnagraficaBean;
import it.zero11.xroads.modules.rewix.api.model.GroupBean;
import it.zero11.xroads.modules.rewix.api.model.JAXBGenericWrapper;
import it.zero11.xroads.modules.rewix.api.model.OperationResponseBean;
import it.zero11.xroads.modules.rewix.api.model.OrderAttachmentsBean;
import it.zero11.xroads.modules.rewix.api.model.OrderBean;
import it.zero11.xroads.modules.rewix.api.model.OrderFilterBean;
import it.zero11.xroads.modules.rewix.api.model.OrderListBean;
import it.zero11.xroads.modules.rewix.api.model.OrderStatusInfo;
import it.zero11.xroads.modules.rewix.api.model.ProductBean;
import it.zero11.xroads.modules.rewix.api.model.ProductImagesBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelLotBean;
import it.zero11.xroads.modules.rewix.api.model.ProductModelUpdateProductBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagMetasBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTagsBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTaxablesBean;
import it.zero11.xroads.modules.rewix.api.model.ProductTranslationsBean;
import it.zero11.xroads.modules.rewix.api.model.UpdateDropshippingOrderStatusBean;
import it.zero11.xroads.modules.rewix.api.model.UserBean;
import it.zero11.xroads.modules.rewix.api.model.UserConsentsBean;
import it.zero11.xroads.modules.rewix.api.model.UserCreateBean;
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
						configuration.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);
						configuration.connectorProvider(new ApacheConnectorProvider());
						
						Client client = ClientBuilder.newBuilder().withConfig(configuration).sslContext(SSLContext.getDefault()).register(JacksonJsonProvider.class).build();
						
						if (debugHttpRequests){
							try{
								Class<?> clazz = Class.forName("org.glassfish.jersey.filter.LoggingFilter");
								Constructor<?> contructor = clazz.getConstructor(Logger.class, boolean.class);
								client.register(contructor.newInstance(Logger.getLogger("it.zero11.rewix.sync"), true));
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

public void updateAddress(String username, AddressBean adressBean) throws RewixAPIException, ProductNotFoundException{		
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

public void updateUserHead(String username, UserBean userBean) throws RewixAPIException, ProductNotFoundException{		
	final Response response = getRestClient()
			.target(baseUrl)
			.path("/restful/user/user/username" + username)
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

public void updateUserAnagrafica(String username, AnagraficaBean anagraficaBean) throws RewixAPIException, ProductNotFoundException{		
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

public void updateUserConsents(String username, UserConsentsBean uerConsentsaBean) throws RewixAPIException, ProductNotFoundException{		
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

public String createUser(UserCreateBean uerCreateBean) throws RewixAPIException, ProductNotFoundException{		
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

public Integer getUserGroup(String platform, String name) throws RewixAPIException, ProductNotFoundException{		
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

} 

