package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Order;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.ui.abstractview.AbstractEntityGridView;
import it.zero11.xroads.ui.layout.XRoadsShowInMenuBar;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;

@Route(value = "orders", layout = XRoadsAdminLayout.class)
@XRoadsShowInMenuBar(name = "Orders", icon = {VaadinIcon.CART}, order = 60)
public class OrderView extends AbstractEntityGridView<Order>{

	private static final long serialVersionUID = 1L;

	public OrderView() {
		super(Order.class);
	}

	@Override
	public void addColumns(Grid<Order> grid) {
		grid.addColumn(Order::getStatus).setHeader("Order Status");
		grid.addColumn(Order::getCustomerEmail).setHeader("Customer Email");
	}

	@Override
	protected boolean enableforce(XRoadsModule module) {
		return true;
	}

}
