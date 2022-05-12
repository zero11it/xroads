package it.zero11.xroads.ui.view;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Invoice;
import it.zero11.xroads.ui.abstractview.AbstractEntityGridView;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;

@Route(value = "invoices", layout = XRoadsAdminLayout.class)
public class InvoiceView extends AbstractEntityGridView<Invoice>{

	private static final long serialVersionUID = 1L;

	public InvoiceView() {
		super(Invoice.class);
	}

	@Override
	public void addColumns(Grid<Invoice> grid) {
		grid.addColumn(Invoice::getInvoiceNumber).setHeader("Invoice Number");
		grid.addColumn(Invoice::getDocumentType).setHeader("Document Type");
	}

}