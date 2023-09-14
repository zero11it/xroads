package it.zero11.xroads.modules.rewixsource.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import it.zero11.xroads.modules.rewixsource.api.model.JAXBGenericWrapper;
import it.zero11.xroads.modules.rewixsource.api.model.ValueWithKey;

public class RewixTagUtils {

	public static Map<String, String> getTagsMap(JAXBGenericWrapper<ValueWithKey> tagsBean) {
        return tagsBean.getItems().stream()
                .collect(Collectors.toMap(
                        ValueWithKey::getKey, ValueWithKey::getValue,
                        (v1, v2) -> v1, LinkedHashMap<String, String>::new
                ));
	}
}
