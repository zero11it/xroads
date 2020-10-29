package it.zero11.xroads.modules.rewix.consumers.old;

public class RewixOrderApiClient extends RewixApiClient {
	/*

	public Future<Void> updateOrderStatus(UpdateDropshippingOrderStatusBean bean) {
		log.info("Updating rewix order " + bean);
		Future<Void> future = Future.future();
		
		try {
			JsonObject jsonObject = new JsonObject(Json.encode(bean));
			webClient				
				.put("/restful/orders/dropshipping/status")
				//.timeout(Long.parseLong(ClientProperties.CONNECT_TIMEOUT))				
			    .putHeader("Authorization", encodeAuthenticationHeader(config.getUsername(), config.getPassword()))
			    .putHeader("Accept", "application/json")
			    .putHeader("content-type", "application/json; charset=utf-8")
				.sendJsonObject(jsonObject, r -> {
					if (r.succeeded()) {
						HttpResponse<Buffer> res = r.result();
	
						try {
							checkResponse(res, OrderStatusInfo.class);
								
							future.complete();							
						} catch (RewixApiException e) {
							future.fail(e);
						}	
					} else
						future.fail(r.cause());
				});
		} catch (Exception e) {
			future.fail(e);			
		}			
		
		return future;
	}
	
	public Future<Void> updateOrderAttachment(Orders order) {
		log.debug("updateOrderAttachment for order " + order.getSourceId());
		Future<Void> future = Future.future();
		
		List<JsonObject> attachmentList = new ArrayList<>();
		for (int i = 0; i < order.getData().getJsonArray(XRoadsJsonKeys.REWIX_ORDER_TRACKING_KEY).size(); i++) {
			attachmentList.add(order.getData().getJsonArray(XRoadsJsonKeys.REWIX_ORDER_TRACKING_KEY).getJsonObject(i));
		}			
		if (attachmentList.size() > 0) {
			OrderAttachmentsBean attachments = new OrderAttachmentsBean();
			attachments.setOrderId(Integer.parseInt(order.getSourceId()));
			attachments.setOrderAttachments(new ArrayList<>());
						
			for (JsonObject attachmentPath : attachmentList) {
				OrderAttachmentBean bean = new OrderAttachmentBean();
				bean.setName(attachmentPath.getString("name"));
				
				InputStream stream = null;
				try {
					stream = (new URL( attachmentPath.getString("url"))).openStream();
					byte[] b = IOUtils.toByteArray(stream);
					bean.setData(b);
					attachments.getOrderAttachments().add(bean);	
				} catch (Exception e) {
					log.error(e.getMessage());					
				} finally {
					try { if (stream != null) stream.close(); } catch (IOException e) {}
				}
												
			}							
			
			if (attachments.getOrderAttachments().size() > 0)
				try {
					JsonObject jsonObject = new JsonObject(Json.encode(attachments));
					webClient				
						.post("/restful/orders/attachment")
						//.timeout(Long.parseLong(ClientProperties.CONNECT_TIMEOUT))				
					    .putHeader("Authorization", encodeAuthenticationHeader(config.getUsername(), config.getPassword()))
					    .putHeader("Accept", "application/json")
					    .putHeader("content-type", "application/json; charset=utf-8")				    
						.sendJsonObject(jsonObject, r -> {
							if (r.succeeded()) {
								HttpResponse<Buffer> res = r.result();
	
							    try {
									OperationResponseBean response = processResponse(res);		
									log.debug("updateOrderAttachment api call response: " + order.getSourceId() + " --> " + response.getMessage());		
									
									future.complete();
								} catch (RewixApiException e) {
									future.fail(toException(jsonObject, e));
								}
							} else
								future.fail(toException(jsonObject, r.cause()));
						});
				} catch (Exception e) {
					future.fail(e);
				}
			else 
				future.complete();
		} else
			future.complete();
		
		return future;
	}
	*/
}
