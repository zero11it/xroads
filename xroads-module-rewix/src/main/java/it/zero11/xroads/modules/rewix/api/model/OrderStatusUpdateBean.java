//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.01.28 alle 09:41:56 AM CET 
//


package it.zero11.xroads.modules.rewix.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "orderStatusUpdateBean", propOrder = {
    "orderList"
})
public class OrderStatusUpdateBean {

    @XmlElement(name = "order_list")
    protected OrderStatusUpdateBean.OrderList orderList;


    public OrderStatusUpdateBean.OrderList getOrderList() {
        return orderList;
    }


    public void setOrderList(OrderStatusUpdateBean.OrderList value) {
        this.orderList = value;
    }



    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "order"
    })
    public static class OrderList {

        protected List<Object> order;

        public List<Object> getOrder() {
            if (order == null) {
                order = new ArrayList<Object>();
            }
            return this.order;
        }

    }

}
