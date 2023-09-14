package it.zero11.xroads.modules.rewixsource.api.model;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JAXBStringURLEncodedToArray extends XmlAdapter<String, String[]>{
	@Override
	public String[] unmarshal(final String value) throws Exception{
		String[] values = value.split(" ");
		for (int i = 0; i < values.length; i++){
			values[i] = URLDecoder.decode(values[i], StandardCharsets.UTF_8.name());
		}
		return values;
	}

	@Override
	public String marshal(final String[] values) throws Exception{
		if (values != null) {
			String[] encodedValues = new String[values.length]; 
			for (int i = 0; i < values.length; i++){
				encodedValues[i] = URLEncoder.encode(values[i], StandardCharsets.UTF_8.name());
			}
			return String.join(" ", encodedValues);
		}else{
			return null;
		}
	}
}