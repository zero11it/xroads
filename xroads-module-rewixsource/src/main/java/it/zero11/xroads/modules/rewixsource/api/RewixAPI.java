package it.zero11.xroads.modules.rewixsource.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.GZipEncoder;
import org.json.JSONObject;

import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.model.GhostEnvelope;
import it.zero11.xroads.modules.rewixsource.api.model.JAXBGenericWrapper;
import it.zero11.xroads.modules.rewixsource.api.model.LockModel;
import it.zero11.xroads.modules.rewixsource.api.model.LockRequestEnvelope;
import it.zero11.xroads.modules.rewixsource.api.model.LockResponse;
import it.zero11.xroads.modules.rewixsource.api.model.OperationResponseBean;
import it.zero11.xroads.modules.rewixsource.api.model.OrderStatusUpdate;
import it.zero11.xroads.modules.rewixsource.api.model.PageData;
import it.zero11.xroads.modules.rewixsource.api.model.UserBean;
import it.zero11.xroads.modules.rewixsource.api.model.UserCreateBean;
import it.zero11.xroads.modules.rewixsource.api.model.UserUpdateTradeAgentBean;
import it.zero11.xroads.modules.rewixsource.api.model.ValueWithKey;
import it.zero11.xroads.modules.rewixsource.api.model.XCarrier;
import it.zero11.xroads.utils.modules.core.utils.ClusterSettingsUtils;
import it.zero11.xroads.utils.modules.core.utils.LocalCache;
import it.zero11.xroads.utils.modules.core.utils.LocalCache.LocalCacheType;
import it.zero11.xroads.utils.modules.core.utils.LocalCache.TTL;

public class RewixAPI {
	private static Logger log = Logger.getLogger(RewixAPI.class.getName());
	
	public static String AUTHORIZATION = "Authorization";
	private static boolean debugHttpRequests = false;
	private static final JAXBContext jaxbContext;
	
	static {
		try {
			jaxbContext = JAXBContext.newInstance(PageData.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	private XRoadsRewixSourceModule xRoadsModule;
	private String authHeader;
	private String targetAuthHeader;
	private String endpoint;
	private String targetEndpoint;
	private String downloadCachePath;
	private String locale;

	
	public RewixAPI(XRoadsRewixSourceModule xRoadsModule) {
		this.xRoadsModule = xRoadsModule;
		this.authHeader = "Basic " + new String(Base64.getEncoder().encode(
				(xRoadsModule.getConfiguration().getApiUser() + ":" + xRoadsModule.getConfiguration().getApiPassword())
						.getBytes()));
		this.targetAuthHeader = "Basic " + new String(Base64.getEncoder().encode(
				(xRoadsModule.getConfiguration().getTargetApiUser() + ":" + xRoadsModule.getConfiguration().getTargetApiPassword())
				.getBytes()));
		this.endpoint = xRoadsModule.getConfiguration().getApiEndpoint();
		this.targetEndpoint = xRoadsModule.getConfiguration().getTargetApiEndpoint();
		this.downloadCachePath = xRoadsModule.getConfiguration().getDownloadCachePath();
		this.locale = xRoadsModule.getConfiguration().getLocale();
	}
	
	public PageData parseProducts(InputStream data) throws JAXBException{
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		PageData pageData = (PageData) unmarshaller.unmarshal(data);

		return pageData;
	}

	public Map<Integer, LockModel> getGrowingOrder() throws MalformedURLException, IOException, UnauthorizedException {
		final String url = endpoint +  "/restful/ghost/orders/dropshipping/locked/";
		Map<Integer, LockModel> lokedModels = new HashMap<>() ;
		Response response = null;
		try {
			WebTarget webTarget = getRestClient().target(url);
			response = webTarget.request()
					.header(AUTHORIZATION, authHeader)
					.accept(MediaType.APPLICATION_XML)
					.get();

			if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode())
				throw new UnauthorizedException("");
		} catch (RuntimeException | NoSuchAlgorithmException e) {
			if (e instanceof NotAuthorizedException){
				throw new NotAuthorizedException(((NotAuthorizedException) e).getResponse());
			}else{
				throw new ConnectException(e.getMessage());
			}
		}
		
		LockResponse responseLocks = response.readEntity(LockResponse.class);
		if(responseLocks.getModels() != null) {
			responseLocks.getModels().forEach(model -> {
				lokedModels.put(model.getStock_id(), model);
			});
		}
		return lokedModels;
	}

	public Map<Integer, String> getSupplierCarriers() throws NoSuchAlgorithmException, UnauthorizedException, IOException {
		Map<Integer, String> carriers = new HashMap<>();
		
		WebTarget webTarget = getRestClient().target(endpoint + "/restful/export/carriers");
		Response response = webTarget.request().header(AUTHORIZATION, authHeader)
				.accept(MediaType.APPLICATION_XML)
				.get();
		if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode())
			throw new UnauthorizedException("");
		if (response.getStatus() == Status.PRECONDITION_FAILED.getStatusCode())
			throw new RuntimeException(IOUtils.toString((InputStream)response.getEntity(), StandardCharsets.UTF_8.name()));
		List<XCarrier> locales = response.readEntity(new GenericType<List<XCarrier>>() {});
		locales.forEach(value -> {
			carriers.put(value.getId(), value.getName());
		});
		 
		return carriers;
	}

	public Map<String, String> getSupplierLocales() throws NoSuchAlgorithmException, UnauthorizedException, IOException {
		Map<String, String> langs = new HashMap<>();
		
		WebTarget webTarget = getRestClient().target(endpoint + "/restful/export/locales");
		Response response = webTarget.request().header(AUTHORIZATION, authHeader)
				.accept(MediaType.APPLICATION_XML)
				.get();
		if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode())
			throw new UnauthorizedException("");
		if (response.getStatus() == Status.PRECONDITION_FAILED.getStatusCode())
			throw new RuntimeException(IOUtils.toString((InputStream)response.getEntity(), StandardCharsets.UTF_8.name()));
		 JAXBGenericWrapper<ValueWithKey> locales = response.readEntity(JAXBGenericWrapper.class);
		 locales.getItems().forEach(value -> {
				langs.put(value.getKey(), value.getValue());
			});
		 
		return langs;
	}

	public Map<String, String> getTargetLocales() throws NoSuchAlgorithmException, UnauthorizedException, IOException {
		Map<String, String> langs = new HashMap<>();
		
		WebTarget webTarget = getRestClient().target(targetEndpoint + "/restful/export/locales");
		Response response = webTarget.request().header(AUTHORIZATION, targetAuthHeader)
				.accept(MediaType.APPLICATION_XML)
				.get();
		if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode())
			throw new UnauthorizedException("");
		if (response.getStatus() == Status.PRECONDITION_FAILED.getStatusCode())
			throw new RuntimeException(IOUtils.toString((InputStream)response.getEntity(), StandardCharsets.UTF_8.name()));
		 JAXBGenericWrapper<ValueWithKey> locales = response.readEntity(JAXBGenericWrapper.class);
		 locales.getItems().forEach(value -> {
				langs.put(value.getKey(), value.getValue());
			});
		 
		return langs;
	}

	public JAXBGenericWrapper<ValueWithKey> getSupplierTags() throws NoSuchAlgorithmException, UnauthorizedException, IOException {
		Response response = getRestClient()
				.target(endpoint + "/restful/export/tags")
				.request()
				.header(AUTHORIZATION, authHeader)
				.accept(MediaType.APPLICATION_XML)
				.get();
		if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode())
			throw new UnauthorizedException("");
		if (response.getStatus() == Status.PRECONDITION_FAILED.getStatusCode())
			throw new RuntimeException(IOUtils.toString((InputStream)response.getEntity(), StandardCharsets.UTF_8.name()));
		 JAXBGenericWrapper<ValueWithKey> tags = response.readEntity(JAXBGenericWrapper.class);
		return tags;
	}

	public JAXBGenericWrapper<ValueWithKey> getTargetTags() throws NoSuchAlgorithmException, UnauthorizedException, IOException {
		Response response = getRestClient()
				.target(targetEndpoint + "/restful/export/tags")
				.request()
				.header(AUTHORIZATION, targetAuthHeader)
				.accept(MediaType.APPLICATION_XML)
				.get();
		if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode())
			throw new UnauthorizedException("");
		if (response.getStatus() == Status.PRECONDITION_FAILED.getStatusCode())
			throw new RuntimeException(IOUtils.toString((InputStream)response.getEntity(), StandardCharsets.UTF_8.name()));
		 JAXBGenericWrapper<ValueWithKey> tags = response.readEntity(JAXBGenericWrapper.class);
		return tags;
	}

	public void modifyGrowingOrder(LockRequestEnvelope lockRequest) throws MalformedURLException, IOException, UnauthorizedException {
		final String url = endpoint +  "/restful/ghost/orders/sold";

		Response response = null;
		try {        		

			WebTarget webTarget = getRestClient().target(url);
			response = webTarget.request().header(AUTHORIZATION, authHeader)
					.accept(MediaType.APPLICATION_XML)                    
					//.post(Entity.xml(xml.toString()), String.class);
					.post(Entity.entity(lockRequest, MediaType.APPLICATION_XML));

			if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode())
				throw new UnauthorizedException("");
		} catch (RuntimeException | NoSuchAlgorithmException e) {
			throw new ConnectException(e.getMessage());
		}
	}
	
	public void sendDropshippingOrder(GhostEnvelope env) throws MalformedURLException, IOException, UnauthorizedException, DropshippingServiceException {
		final String url = endpoint +  "/restful/ghost/orders/0/dropshipping";

		Response response = null;
		try {			
			WebTarget webTarget = getRestClient().target(url);
			response = webTarget.request().header(AUTHORIZATION, authHeader)
					.accept(MediaType.APPLICATION_XML)                    
					.post(Entity.entity(env, MediaType.APPLICATION_XML));

			if (response.getStatus() == Status.PRECONDITION_FAILED.getStatusCode())
				throw new DropshippingServiceException(IOUtils.toString((InputStream)response.getEntity(), StandardCharsets.UTF_8.name()));
			if (response.getStatus() == Status.NOT_FOUND.getStatusCode())
				throw new DropshippingServiceException(IOUtils.toString((InputStream)response.getEntity(), StandardCharsets.UTF_8.name()));
			if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode())
				throw new UnauthorizedException("");
		} catch (RuntimeException | NoSuchAlgorithmException e) {
			throw new ConnectException(e.getMessage());
		}

	}

	public OperationResponseBean createUser(UserCreateBean userCreateBean) throws MalformedURLException, IOException, UnauthorizedException {
		final String url = endpoint +  "/restful/user/create";

		Response response = null;
		try {			
			WebTarget webTarget = getRestClient().target(url);
			response = webTarget.request()
					.header(AUTHORIZATION, authHeader)
					.accept(MediaType.APPLICATION_XML)                    
					.post(Entity.entity(userCreateBean, MediaType.APPLICATION_XML));

			if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode()) {
				throw new UnauthorizedException("");
			}else {
				return response.readEntity(OperationResponseBean.class);
			}
		} catch (RuntimeException | NoSuchAlgorithmException e) {
			throw new ConnectException(e.getMessage());
		}
	}

	public OperationResponseBean setUserTradeAgent(String username, UserUpdateTradeAgentBean userUpdateTradeAgentBean) throws MalformedURLException, IOException, UnauthorizedException {
		final String url = endpoint +  "/restful/user/tradeagent/" + username;

		Response response = null;
		try {			
			WebTarget webTarget = getRestClient().target(url);
			response = webTarget.request()
					.header(AUTHORIZATION, authHeader)
					.accept(MediaType.APPLICATION_JSON)                    
					.post(Entity.entity(userUpdateTradeAgentBean, MediaType.APPLICATION_JSON));

			if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode()) {
				throw new UnauthorizedException("");
			}else {
				return response.readEntity(OperationResponseBean.class);
			}
		} catch (RuntimeException | NoSuchAlgorithmException e) {
			throw new ConnectException(e.getMessage());
		}
	}

	public static class CustomAuthenticator extends Authenticator {
		private String username;
		private String password;

		public CustomAuthenticator(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		protected PasswordAuthentication getPasswordAuthentication() {  
			return new PasswordAuthentication(username, password.toCharArray());  
		}
	}

	public PageData getCatalogForPlatformAndParse() throws IOException, UnauthorizedException {
		PageData data = LocalCache.getInstance().getOrGenerateWeak(LocalCache.buildKey(LocalCacheType.REWIXCATALOG, xRoadsModule.getName(), "full"), TTL.LONG, ()->{
			PageData pageData;
			try (final InputStream stream = new FileInputStream(getCatalogForPlatform())) {
				pageData = parseProducts(stream);
			} catch (IOException | UnauthorizedException | ExpiredException | JAXBException e) {
				throw new RuntimeException(e);
			}
			return pageData;
		});
		if (data == null) {
			//Check if it was a permission issue
			getGrowingOrder();
			throw new IOException("Failed to download catalog");
		}else {
			return data;
		}
	}

	private File getCatalogForPlatform() throws IOException, UnauthorizedException, ExpiredException {
		String catalogFileName = downloadCachePath + "/" + xRoadsModule.getName() + "_" + locale + ".xml";

		if (catalogFileName != null && catalogFileName.startsWith("{")) {
			catalogFileName = new JSONObject(catalogFileName).optString(ClusterSettingsUtils.NODE_NAME);
		}
		
		if (catalogFileName == null) 
			return saveCatalogToFile(downloadCatalogForPlatform( null), catalogFileName);

		Path p = Paths.get(catalogFileName);
		if (!p.toFile().exists()) 
			return saveCatalogToFile(downloadCatalogForPlatform( null), catalogFileName);

		BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();

		long diff = (System.currentTimeMillis() - view.lastModifiedTime().toMillis());	    
		if (diff > (60L * 60L * 1000L))  //AN UPDATE IS NEEDED..
			return saveCatalogToFile(downloadCatalogForPlatform(null), catalogFileName);
		else
			return p.toFile(); 

	}	

	private static File saveCatalogToFile(InputStream response, String fileName) throws FileNotFoundException, IOException, UnauthorizedException {
		String downloadedFileName = fileName + ".partial";
		File downloadedFile = new File(downloadedFileName);
		if (downloadedFile.exists()) {
			downloadedFile.delete();
		}else {
			downloadedFile.getParentFile().mkdirs();
		}

		IOUtils.copy(response, new FileOutputStream(downloadedFile));
		
		Files.move(downloadedFile.toPath(), new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		return downloadedFile;
	}

	public InputStream downloadCatalogForPlatform(String since) throws MalformedURLException, IOException, UnauthorizedException, ExpiredException {
		final String url = endpoint +  "/restful/export/api/products.xml?v=TEAL&acceptedlocales=" + locale + 
				"&addtags=true" +
				(since != null ? "&since=" + URLEncoder.encode(since, "UTF-8") : "");

		InputStream response = null;
		try {
			WebTarget webTarget = getRestClient().target(url);
			Response r = webTarget.request()
					.header(AUTHORIZATION, authHeader)
					.get();

			if (r.getStatus() == Status.PRECONDITION_FAILED.getStatusCode()) 
				throw new ExpiredException("Incremental validity failed.");

			if (r.getStatus() == Status.UNAUTHORIZED.getStatusCode())
				throw new UnauthorizedException("Your account is not allowed to download products data. Please check your account.");

			if (r.getStatus() == 429)
				throw new ConnectException("Too many requests. Make sure you are not using the same account with other plugins and retry in 15 minutes.");

			if (r.getStatus() != Status.OK.getStatusCode())
				throw new IOException("Unexpected response from " + endpoint + " status " + r.getStatus() + " please retry in 15 minutes.");

			response = r.readEntity(InputStream.class);

		} catch (RuntimeException | NoSuchAlgorithmException e) {
			throw new IOException(e.getMessage());
		}

		return response;	   
	}

	public OrderStatusUpdate getOrderStatusByRef(String orderId) throws IOException, DropshippingServiceException, UnauthorizedException {
		final String url = endpoint + String.format("/restful/ghost/clientorders/clientkey/%s", orderId);

		Response r = null;
		try {
			WebTarget webTarget = getRestClient().target(url);
			r = webTarget.request().header(AUTHORIZATION, authHeader)
					.accept(MediaType.APPLICATION_XML)
					.get();

		} catch (NoSuchAlgorithmException e) {
			throw new DropshippingServiceException("Failed to initialize API: " + e.getMessage());
		}

		if (r.getStatus() == 200) {
			return r.readEntity(OrderStatusUpdate.class);
		} else if(r.getStatus() == 404) {
			throw new DropshippingServiceException("Not Found");
		} else {
			throw new DropshippingServiceException("Error connecting");
		}

	}

	public OrderStatusUpdate getOrderStatusByRewixOrderId(String orderId) throws IOException, DropshippingServiceException, UnauthorizedException {
		final String url = endpoint + String.format("/restful/ghost/clientorders/serverkey/%s", orderId);

		Response r = null;
		try {
			WebTarget webTarget = getRestClient().target(url);
			r = webTarget.request().header(AUTHORIZATION, authHeader)
					.accept(MediaType.APPLICATION_XML)
					.get();

		} catch (NoSuchAlgorithmException e) {
			throw new DropshippingServiceException("Failed to initialize API: " + e.getMessage());
		}

		if (r.getStatus() == 200) {
			return r.readEntity(OrderStatusUpdate.class);
		} else if(r.getStatus() == 404) {
			throw new DropshippingServiceException("Not Found");
		} else {
			throw new DropshippingServiceException("Error connecting");
		}

	}

	public UserBean getUser(String username) throws Exception {
		final String url = endpoint + String.format("/restful/user/user/%s", username);

		Response response = null;
		try {
			WebTarget webTarget = getRestClient().target(url);
			response = webTarget.request()
					.header(AUTHORIZATION,  authHeader)
					.accept(MediaType.APPLICATION_XML)
					.get();

		} catch (NoSuchAlgorithmException e) {
			throw new Exception("Failed to initialize API: " + e.getMessage());
		}

		if (response.getStatus() == 200) {
			return response.readEntity(UserBean.class);
		} else if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
			throw new UnauthorizedException("");
		}
		return null;
	}

	private static Client restClient;
	private static Client getRestClient() throws NoSuchAlgorithmException {
		if (restClient == null){
			synchronized (RewixAPI.class) {
				if (restClient == null){
					Client client = ClientBuilder.newBuilder().sslContext(SSLContext.getDefault()).register(GZipEncoder.class).register(EncodingFilter.class).build();

					if (debugHttpRequests){
						try{
							Class<?> clazz = Class.forName("org.glassfish.jersey.filter.LoggingFilter");
							Constructor<?> contructor = clazz.getConstructor(Logger.class, boolean.class);
							client.register(contructor.newInstance(Logger.getLogger("it.zero11.rewix.xroads"), true));
						}catch(Exception e){
							e.printStackTrace();
						}
					}

					restClient = client;
				}
			}
		}
		return restClient;
	}
}
