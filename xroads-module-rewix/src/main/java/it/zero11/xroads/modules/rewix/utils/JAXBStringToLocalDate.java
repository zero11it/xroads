package it.zero11.xroads.modules.rewix.utils;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JAXBStringToLocalDate extends XmlAdapter<String, LocalDate>{
	@Override
	public LocalDate unmarshal(final String value) throws Exception{
		return LocalDate.parse(value);
	}

	@Override
	public String marshal(final LocalDate value) throws Exception{
		if (value == null) {
			return "";
		}else {
			return value.toString();
		}
	}
}