package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Order;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.ui.abstractview.AbstractEntityGridView;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;

@Route(value = "orders", layout = XRoadsAdminLayout.class)
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

}
