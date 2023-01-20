package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Product;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.ui.abstractview.AbstractEntityGridView;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;

@Route(value = "products", layout = XRoadsAdminLayout.class)
public class ProductView  extends AbstractEntityGridView<Product>{

	private static final long serialVersionUID = 1L;

	public ProductView() {
		super(Product.class);
	}

	@Override
	public void addColumns(Grid<Product> grid) {
		grid.addColumn(Product::getSku).setHeader("Sku");
	}

	@Override
	protected boolean enableforce(XRoadsModule module) {
		if ((!module.getName().equals("rewix") || (module instanceof XRoadsRewixModule
				&& ((XRoadsRewixModule) module).getConfiguration().isEnableFullRewixProductUpdate()))) {
			return true;
		}
		return false;
	}

}
