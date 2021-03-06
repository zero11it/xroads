package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Stock;
import it.zero11.xroads.ui.abstractview.AbstractEntityGridView;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;

@Route(value = "stocks", layout = XRoadsAdminLayout.class)
public class StockView extends AbstractEntityGridView<Stock>{

	private static final long serialVersionUID = 1L;

	public StockView() {
		super(Stock.class);
	}

	@Override
	public void addColumns(Grid<Stock> grid) {
		grid.addColumn(Stock::getSupplier).setHeader("Supplier");
		grid.addColumn(Stock::getAvailability)
		.setWidth("100px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Availability");
	}

}
