//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.01.28 alle 09:41:56 AM CET 
//


package it.zero11.xroads.modules.rewix.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "orderStatusInfo", propOrder = {
    "parsedDate",
    "orderId",
    "extRef",
    "status",
    "substatus",
    "lastUpdate",
    "customerId"
})
public class OrderStatusInfo {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar parsedDate;
    @XmlElement(name = "order_id")
    protected Integer orderId;
    @XmlElement(name = "ext_ref")
    protected String extRef;
    protected Integer status;
    protected Integer substatus;
    @XmlElement(name = "last_update")
    protected String lastUpdate;
    @XmlElement(name = "customer_id")
    protected String customerId;


    public XMLGregorianCalendar getParsedDate() {
        return parsedDate;
    }


    public void setParsedDate(XMLGregorianCalendar value) {
        this.parsedDate = value;
    }


    public Integer getOrderId() {
        return orderId;
    }


    public void setOrderId(Integer value) {
        this.orderId = value;
    }


    public String getExtRef() {
        return extRef;
    }


    public void setExtRef(String value) {
        this.extRef = value;
    }


    public Integer getStatus() {
        return status;
    }


    public void setStatus(Integer value) {
        this.status = value;
    }


    public Integer getSubstatus() {
        return substatus;
    }


    public void setSubstatus(Integer value) {
        this.substatus = value;
    }


    public String getLastUpdate() {
        return lastUpdate;
    }


    public void setLastUpdate(String value) {
        this.lastUpdate = value;
    }


    public String getCustomerId() {
        return customerId;
    }


    public void setCustomerId(String value) {
        this.customerId = value;
    }

}
