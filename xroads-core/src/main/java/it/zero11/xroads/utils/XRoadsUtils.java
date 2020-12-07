package it.zero11.xroads.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.AbstractProductGroupedEntity;
import it.zero11.xroads.model.Customdata;
import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.model.Price;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.Stock;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.sync.XRoadsJsonKeys;

public class XRoadsUtils {
	private static final Integer MODEL_VERSION = 1;

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public static final List<Class<? extends AbstractEntity>> ENTITIES_CLASSES = Collections.unmodifiableList(
			Arrays.asList(Product.class, Model.class, Price.class, Stock.class, Customer.class, Order.class));
	
	public static Product getProductInstance() {
		final Product product = new Product();
		product.setVersion(MODEL_VERSION);
		product.setDescriptions(OBJECT_MAPPER.createObjectNode());
		product.setExternalReferences(OBJECT_MAPPER.createObjectNode());
		ObjectNode tags = OBJECT_MAPPER.createObjectNode();
		tags.set("translations", OBJECT_MAPPER.createObjectNode());
		tags.set("urlkeys", OBJECT_MAPPER.createObjectNode());
		product.setTags(tags);
		
		ObjectNode blob = OBJECT_MAPPER.createObjectNode();
		blob.set("urls", OBJECT_MAPPER.createArrayNode());
		blob.set("binaries", OBJECT_MAPPER.createArrayNode());
		product.setImages(blob);
		
		product.setData(OBJECT_MAPPER.createObjectNode());
		product.setNames(OBJECT_MAPPER.createObjectNode());
		
		return product;
	}
	
	public static Model getModelInstance() {
		final Model model = new Model();		
		model.setExternalReferences(OBJECT_MAPPER.createObjectNode());
		model.setTags(OBJECT_MAPPER.createObjectNode());
		model.setOptions(OBJECT_MAPPER.createObjectNode());
		model.setData(OBJECT_MAPPER.createObjectNode());
		model.setVersion(MODEL_VERSION);
		
		return model;
	}
	
	public static Customdata getCustomdataInstance() {
		final Customdata customdata = new Customdata();
		customdata.setVersion(MODEL_VERSION);		
		customdata.setExternalReferences(OBJECT_MAPPER.createObjectNode());				
		customdata.setData(OBJECT_MAPPER.createObjectNode());
		customdata.setContent(OBJECT_MAPPER.createObjectNode());
		
		return customdata;
	}
	
	public static Price getPriceInstance() {
		final Price price = new Price();
		price.setData(OBJECT_MAPPER.createObjectNode());
		price.setExternalReferences(OBJECT_MAPPER.createObjectNode());
		price.setVersion(MODEL_VERSION);
		return price;
	}
	
	public static Order getOrderInstance() {
		final Order order = new Order();
		order.setInvoiceAddress(OBJECT_MAPPER.createObjectNode());
		order.setLineItems(OBJECT_MAPPER.createObjectNode());
		order.setTotals(OBJECT_MAPPER.createObjectNode());		
		order.setShippingAddress(OBJECT_MAPPER.createObjectNode());		
		order.setData(OBJECT_MAPPER.createObjectNode());
		order.setExternalReferences(OBJECT_MAPPER.createObjectNode());
		order.setVersion(MODEL_VERSION);
		return order;
	}
	
	public static Stock getStockInstance() {
		final Stock stock = new Stock();
		stock.setData(OBJECT_MAPPER.createObjectNode());
		stock.setExternalReferences(OBJECT_MAPPER.createObjectNode());
		stock.setVersion(MODEL_VERSION);
		return stock;
	}
	
	public static Customer getCustomerInstance() {
		final Customer customer = new Customer();
		customer.setExternalReferences(OBJECT_MAPPER.createObjectNode());
		customer.setData(OBJECT_MAPPER.createObjectNode());
		customer.setPhone(OBJECT_MAPPER.createObjectNode());
		customer.setAddresses(OBJECT_MAPPER.createObjectNode());
		customer.setGroups(OBJECT_MAPPER.createObjectNode());
		customer.setPaymentTerms(OBJECT_MAPPER.createObjectNode());
		customer.setVersion(MODEL_VERSION);
		
		return customer;
	}
	
	public static <T, R> R getEnsuringAllEquals(List<T> elements, Function<T, R> getter) {
		if (elements == null || elements.size() == 0) {
			return null;
		}
		R value = getter.apply(elements.get(0));
		for (T element : elements) {
			if (!Objects.equals(value, getter.apply(element))) {
				throw new IllegalArgumentException("Different values found in elements");
			}
		}
		return value;
	}	
	
	public static String getExternalReferenceLastError(AbstractEntity entity, XRoadsModule module) {
		ObjectNode value = (ObjectNode) entity.getExternalReferences().get(module.getName());
		return value.get(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR).asText();
	}
	
	public static String getExternalReferenceId(AbstractEntity entity, XRoadsModule module) {
		if (entity.getExternalReferences() == null)
			return null;
		
		JsonNode v = entity.getExternalReferences().get(module.getName());
		
		if (v == null)
			return null;
		else
			return v.path(XRoadsJsonKeys.EXTERNAL_REFERENCE_ID).asText(null);							
	}	
	
	public static Integer getExternalReferenceVersion(AbstractEntity entity, XRoadsModule module) {
		if (entity.getExternalReferences() == null)
			return null;
		
		JsonNode v = entity.getExternalReferences().get(module.getName());
		
		if (v == null)
			return null;
		else
			return v.path(XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION).asInt(-1);							
	}

	@SuppressWarnings("unchecked")
	public static boolean moduleHasConsumer(XRoadsModule xRoadsModule, Class<? extends AbstractEntity> entityClass) {
		 return xRoadsModule.getEntityConsumer(entityClass) != null ||
					(AbstractProductGroupedEntity.class.isAssignableFrom(entityClass) &&
							xRoadsModule.getEntityProductGroupedConsumer((Class<AbstractProductGroupedEntity>) entityClass) != null);
	}
}
