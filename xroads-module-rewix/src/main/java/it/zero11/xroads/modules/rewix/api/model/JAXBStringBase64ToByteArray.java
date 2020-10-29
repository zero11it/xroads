package it.zero11.xroads.modules.rewix.api.model;

import java.util.Base64;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JAXBStringBase64ToByteArray extends XmlAdapter<String, byte[]>{
    @Override
    public String marshal(byte[] data) throws Exception {
    	if (data == null){
    		return null;
    	}else{
    		return Base64.getEncoder().encodeToString(data);
    	}
    }

    @Override
    public byte[] unmarshal(String data) throws Exception {
    	if (data == null){
    		return null;
    	}else{
    		return Base64.getDecoder().decode(data);
    	}
    }
}