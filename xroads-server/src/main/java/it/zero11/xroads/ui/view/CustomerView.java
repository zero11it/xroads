package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Customer;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.ui.abstractview.AbstractEntityGridView;
import it.zero11.xroads.ui.layout.XRoadsShowInMenuBar;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;

@Route(value = "customers", layout = XRoadsAdminLayout.class)
@XRoadsShowInMenuBar(name = "Customers", icon = {VaadinIcon.USERS}, order = 40)
public class CustomerView extends AbstractEntityGridView<Customer>{

	private static final long serialVersionUID = 1L;

	public CustomerView() {
		super(Customer.class);
	}

	@Override
	public void addColumns(Grid<Customer> grid) {
		grid.addColumn(Customer::getEmail).setHeader("Email");
		grid.addColumn(Customer::getCompany).setHeader("Sku");
	}

	@Override
	protected boolean enableforce(XRoadsModule module) {
		return true;
	}

}
