package it.zero11.xroads.modules.rewix.api.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;


@XmlRootElement(name="wrapper")
@XmlSeeAlso(ValueWithKey.class)
public class JAXBGenericWrapper<T> {
	private Collection<T> items = new ArrayList<T>();
	
	public JAXBGenericWrapper() {}
	
	public JAXBGenericWrapper(Collection<T> items) {
		this.items = items;
	}

	public void setItems(Collection<T> items) {
		this.items = items;
	}

	@XmlAnyElement(lax=true)
    public Collection<T> getItems() {
        return this.items;
    }
	
}
