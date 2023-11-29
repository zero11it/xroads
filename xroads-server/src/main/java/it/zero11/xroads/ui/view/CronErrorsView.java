package it.zero11.xroads.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.Cron;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.ui.layout.XRoadsShowInMenuBar;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;

@Route(value = "errors", layout = XRoadsAdminLayout.class)
@XRoadsShowInMenuBar(name = "Cron Errors", icon = {VaadinIcon.CLOSE}, order = 90)
public class CronErrorsView   extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private Grid<Cron> errorGrid;
	ListDataProvider<Cron> dataProvider;
	List<Cron> cronErrorList;
	public CronErrorsView() {
		setSizeFull();	
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		errorGrid = new Grid<>();
		cronErrorList = new ArrayList<Cron>();
		for (XRoadsModule xroadsModule : XRoadsCoreServiceBean.getInstance().getEnabledModules(false).values()) {
			Set<String> cronNames = xroadsModule.getCrons().keySet();
			for(String cronName : cronNames) {
				cronErrorList.addAll(CronDao.getInstance().getErrors(cronName, 100));
			}
		}
		
		dataProvider = DataProvider.ofCollection(cronErrorList);
		errorGrid.setDataProvider(dataProvider);
		
		errorGrid.addColumn(cron -> cron.getName())
		.setWidth("200px")
		.setAutoWidth(false)
		.setFlexGrow(0)
		.setHeader("Name");
		errorGrid.setItemDetailsRenderer(TemplateRenderer.<Cron>of(
		        "<div class='custom-details' style='border: 1px solid gray; padding: 10px; width: 100%; box-sizing: border-box;'>"
		                + "<div><b>[[item.error]]</b></div>"
		                + "</div>")
		        .withProperty("error", Cron::getError)
		        // This is now how we open the details
		        .withEventHandler("handleClick", cron -> {
		        	errorGrid.getDataProvider().refreshItem(cron);
		        }));
		errorGrid.addColumn(cron -> cron.getError()).setHeader("Error");
		
		add(errorGrid);
		
	}	
	
}
