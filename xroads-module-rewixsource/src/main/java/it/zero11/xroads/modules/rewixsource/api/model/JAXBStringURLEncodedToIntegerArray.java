package it.zero11.xroads.modules.rewixsource.api.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JAXBStringURLEncodedToIntegerArray extends XmlAdapter<String, Integer[]>{
	@Override
	public Integer[] unmarshal(final String value) throws Exception{
		String[] values = value.split(" ");
		Integer[] decodedValues = new Integer[values.length];
		for (int i = 0; i < values.length; i++){
			decodedValues[i] = Integer.valueOf(values[i]);
		}
		return decodedValues;
	}

	@Override
	public String marshal(final Integer[] values) throws Exception{
		if (values == null) {
			return "";
		}else {
			String[] encodedValues = new String[values.length]; 
			for (int i = 0; i < values.length; i++){
				encodedValues[i] = values[i].toString();
			}
			return String.join(" ", encodedValues);
		}
	}
}