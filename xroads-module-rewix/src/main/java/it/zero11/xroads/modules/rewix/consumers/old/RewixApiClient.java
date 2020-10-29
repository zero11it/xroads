package it.zero11.xroads.modules.rewix.consumers.old;

public abstract class RewixApiClient {

	
	public static final String ORDER_PLATFORM_KEY = "platform";
	public static final String ORDER_ORIGIN_KEY = "origin";
	public static final String ORDER_TOTAL_FULLFILLMENT_KEY = "fullfillment";
	public static final String FULLFILLMENT_CONTEXT_KEY = "checkout";
	/*
	protected RewixConfig config;
	protected EcomService ecomService;
	protected Vertx vertx;
	protected WebClient webClient;
	
	public RewixApiClient(Vertx vertx, EcomService ecomService, WebClient webClient, RewixConfig config) {
		this.webClient = webClient;
		this.config = config;
		this.ecomService = ecomService;
		this.vertx = vertx;
	}

	public static String encodeAuthenticationHeader(String username, String password) throws UnsupportedEncodingException {
		return "Basic " + new String(Base64.getEncoder().encode((username+":"+password).getBytes()));		
	}
	
	public static String generatePassword(Integer nOfDigits) {
		PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
		        .useDigits(true)
		        .useLower(true)
		        .useUpper(true)
		        .build();
		return passwordGenerator.generate(nOfDigits);
	}
	
//	private static Locale getLocaleFromCountry (String country) {
//	    return COUNTRY_TO_LOCALE_MAP.get(country);
//	}
	
	public String encodeUrlKey(String translation) {
		return translation
				.toLowerCase()
				.replaceAll("[^A-Za-z0-9]", " ")
				.replace(" ", "-");
	}
	
	protected <R> R checkResponse(HttpResponse<Buffer> result, Class<R> clazz) throws RewixApiException {
		if (result.statusCode() >= 400) {
			String error = result.bodyAsString();
			throw new RewixApiException(null, error);
		}
		
		R response = result.bodyAsJson(clazz);	
		
		return response;
	}
	
	protected OperationResponseBean processResponse(HttpResponse<Buffer> result) throws RewixApiException {
		if (result.statusCode() >= 400) {
			String error = result.bodyAsString();
			throw new RewixApiException(null, error);
		}
		
		OperationResponseBean response = result.bodyAsJson(OperationResponseBean.class);	
		if (!response.getStatus()) {
			String error = response.getMessage();
			throw new RewixApiException(response, error);				
		}
		
		return response;
	}
	
	protected JsonObject processGetResponse(HttpResponse<JsonObject> result) throws RewixApiException {
		if (result.statusCode() >= 400) {
			String error = result.bodyAsString();
			throw new RewixApiException(null, error);
		}
		
		JsonObject response = result.body();
		return response;
	}
	
	public Future<Map<GroupSearchBean, Integer>> getGroupIds(Set<GroupSearchBean> searchBeans) {
		Future<Map<GroupSearchBean, Integer>> future = Future.future();
		
		@SuppressWarnings("rawtypes")
		List<Future> futures = new ArrayList<>();			
		for (GroupSearchBean bean : searchBeans) {
			futures.add(getGroup(bean.getPlatform(), bean.getName()));			
		}
		CompositeFuture.all(futures)		
		.setHandler(x -> {
			if (x.succeeded()) {				
				Map<GroupSearchBean, Integer> result = new HashMap<>();
				int i = 0;
				for (GroupSearchBean bean : searchBeans) {
					result.put(bean, (Integer) x.result().resultAt(i++));					 					
				}
				future.complete(result);
			} else
				future.fail(x.cause());						
		});
		
		return future;
	}
	

	protected Future<Void> addToGroupById(UserListBean users, Integer id) {
		Future<Void> future = Future.future();
		
		try {
			JsonObject jsonObject = new JsonObject(Json.encode(users));
			webClient				
				.post("/restful/user/group/id/" + id + "/add")
				//.timeout(Long.parseLong(ClientProperties.CONNECT_TIMEOUT))				
			    .putHeader("Authorization", encodeAuthenticationHeader(config.getUsername(), config.getPassword()))
			    .putHeader("Accept", "application/json")
			    .putHeader("content-type", "application/json; charset=utf-8")
				.sendJsonObject(jsonObject, r -> {
					if (r.succeeded()) {						
					    future.complete();													
					} else
						future.fail(r.cause());
				});
		} catch (Exception e) {
			future.fail(e);
		}
		
		return future;
	}

	protected RewixApiException toException(JsonObject json, Throwable e) {
		return new RewixApiException(new OperationResponseBean(), e.getMessage() + ": " + json.toString(), e);
	}
	
	protected RewixApiException toException(String message, Throwable e) {
		return new RewixApiException(new OperationResponseBean(), e.getMessage() + ": " + message, e);
	}
	*/
}
