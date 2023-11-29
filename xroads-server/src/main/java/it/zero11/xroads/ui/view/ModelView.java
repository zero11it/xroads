package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Model;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.ui.abstractview.AbstractEntityGridView;
import it.zero11.xroads.ui.layout.XRoadsShowInMenuBar;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;

@Route(value = "models", layout = XRoadsAdminLayout.class)
@XRoadsShowInMenuBar(name = "Models", icon = {VaadinIcon.COPY}, order = 30)
public class ModelView extends AbstractEntityGridView<Model> {

	private static final long serialVersionUID = 1L;

	public ModelView() {
		super(Model.class);
	}

	@Override
	public void addColumns(Grid<Model> grid) {
		grid.addColumn(Model::getSku).setHeader("Sku");
	}

	@Override
	protected boolean enableforce(XRoadsModule module) {
		return true;
	}


}
