package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Price;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.ui.abstractview.AbstractEntityGridView;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;

@Route(value = "prices", layout = XRoadsAdminLayout.class)
public class PriceView extends AbstractEntityGridView<Price>{

	private static final long serialVersionUID = 1L;

	public PriceView() {
		super(Price.class);
	}

	@Override
	public void addColumns(Grid<Price> grid) {
		grid.addColumn(Price::getBuyPrice).setHeader("Price");
		grid.addColumn(Price::getCountry).setHeader("Country");
	}

	@Override
	protected boolean enableforce(XRoadsModule module) {
		return true;
	}

}
