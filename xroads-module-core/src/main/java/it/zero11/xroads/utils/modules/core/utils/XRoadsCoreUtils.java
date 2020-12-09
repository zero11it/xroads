package it.zero11.xroads.utils.modules.core.utils;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.Order;
import it.zero11.xroads.model.Price;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.Stock;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.StackTraceUtil;
import it.zero11.xroads.utils.XRoadsUtils;

public class XRoadsCoreUtils {
	@SafeVarargs
	private static <T> boolean changedMethods(T o1, T o2, Function<T, Object> ... methods){
		for (Function<T, Object> method : methods) {
			Object x1 = method.apply(o1);
			Object x2 = method.apply(o2);
			if (!Objects.equals(x1, x2)) {
				return true;
			}
		}		
		return false;
	}
	
	@SafeVarargs
	private static <T> boolean changedMethodsArray(T o1, T o2, Function<T, Object[]> ... methods){
		for (Function<T, Object[]> method : methods) {
			Object[] x1 = method.apply(o1);
			Object[] x2 = method.apply(o2);
			if (!Arrays.equals(x1, x2)) {
				return true;
			}
		}		
		return false;
	}
	
	public static boolean productHasChanged(Product p1, Product p2) {
		return changedMethods(p1, p2, Product::getBrand, Product::getCost, Product::getData, Product::getDescriptions, Product::getEan, 
				Product::getImages, Product::getName, Product::getNames, Product::getSku, Product::getSourceId, Product::getSupplier,
				Product::getTags, Product::getVirtual, Product::getWeight, Product::getOnline);		
	}
	
	public static boolean modelHasChanged(Model m1, Model m2){
		return changedMethods(m1, m2, Model::getAvailability, Model::getData, Model::getEan, Model::getName, Model::getOptions, 
				Model::getProductSourceId, Model::getSku, Model::getSourceId, Model::getTags) || changedMethodsArray(m1, m2, Model::getAdditionalBarcode);				
	}
	
	public static boolean priceHasChanged(Price p1, Price p2){
		return changedMethods(p1, p2, Price::getBuyPrice, Price::getCountry, Price::getCustomerSourceId, Price::getData, Price::getDiscountedPrice, 
				Price::getListingGroup, Price::getMinQuantity, Price::getProductSourceId, Price::getRetailPrice, Price::getSellPrice, Price::getSuggestedPrice);			
	}
	
	public static boolean stockHasChanged(Stock p1, Stock p2){
		return changedMethods(p1, p2, Stock::getAvailability, Stock::getData, Stock::getModelSourceId, 
				Stock::getSourceId, Stock::getSupplier, Stock::getWarehouse);				
	}
	
	public static boolean orderkHasChanged(Order p1, Order p2){		
		//FIXME what are the necessary parameters?
		return changedMethods(p1, p2, Order::getCurrency, Order::getDispatchTotal, Order::getShippingAddress, 
				Order::getData, Order::getSourceId, Order::getTotal, Order::getStatus, Order::getCustomerSourceId, Order::getCustomerEmail);
	}
	
	public static boolean customerHasChanged(Customer p1, Customer p2){
		return changedMethods(p1, p2, Customer::getAddresses, Customer::getCompany, Customer::getData, Customer::getDateOfBirth, Customer::getEmail, 
				Customer::getFirstname, Customer::getFiscalCode, Customer::getGroups, Customer::getLanguageCode, Customer::getLastname, Customer::getPhone,
				Customer::getSourceId, Customer::getUsername, Customer::getVatNumber, Customer::getPaymentTerms);		
	}

	public static boolean setExternalReferenceMarkForRetryInAllModules(AbstractEntity entity) {
		boolean changed = false;
		for (Iterator<JsonNode> it = entity.getExternalReferences().elements(); it.hasNext();) {
			ObjectNode value = (ObjectNode) it.next();
			changed |= value.remove(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR) != null;
			changed |= value.remove(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE) != null;
			changed |= value.remove(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_STACK_TRACE) != null;
		}
		if (changed) {
			//Resetting to allow hibernate to detect the change
			entity.setExternalReferences(entity.getExternalReferences());
		}
		return changed;
	}
	
	public static void setExternalReference(AbstractEntity entity, String moduleName, String id, int version) {
		ObjectNode value = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		value.put(XRoadsJsonKeys.EXTERNAL_REFERENCE_ID, id);
		value.put(XRoadsJsonKeys.EXTERNAL_REFERENCE_VERSION, version);
		((ObjectNode)entity.getExternalReferences()).set(moduleName, value);
	}
	
	public static void setExternalReferenceLastError(AbstractEntity entity, String moduleName, Exception e) {
		ObjectNode value = (ObjectNode) entity.getExternalReferences().get(moduleName);
		
		if (value == null) {
			value = XRoadsUtils.OBJECT_MAPPER.createObjectNode();
		}
		value.put(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR, e.getMessage());
		value.put(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE, OffsetDateTime.now().toString());
		value.put(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_STACK_TRACE, StackTraceUtil.getStackTrace(e));
			
		((ObjectNode)entity.getExternalReferences()).set(moduleName, value);
	}
	
}
