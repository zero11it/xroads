package it.zero11.xroads.ui.view;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.ui.layout.XRoadsAdminLayout;
import it.zero11.xroads.utils.XRoadsAsyncUtils;
import it.zero11.xroads.utils.XRoadsUtils;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;
import it.zero11.xroads.utils.modules.core.model.EntityStatus;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;

@Route(value = "dashboard", layout = XRoadsAdminLayout.class)
public class DashboardView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private HorizontalLayout topBar;
	private Grid<EntityStatus> grid;
	private Deque<Map<Class<?>, EntityStatus>> historyData = new LinkedList<>();
	
	public DashboardView() {	
		setSizeFull();
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);

		topBar = new HorizontalLayout();
		topBar.setWidth("100%");
		topBar.setAlignItems(Alignment.BASELINE);
		add(topBar);

		grid = new Grid<>();
		grid.addColumn(e -> e.getModule()).setHeader("Module");
		grid.addColumn(e -> e.getEntityClass().getSimpleName()).setHeader("Entity");
		grid.addColumn(e -> String.format("%d (%.2f %%)", e.getSyncronized(), 100 * e.getSyncronizedPercentage())).setHeader("Synced");
		grid.addColumn(e -> String.format("%d (%.2f %%)", e.getNewQueued(), 100 * e.getNewQueuedPercentage())).setHeader("New queued");
		grid.addColumn(e -> String.format("%d (%.2f %%)", e.getUpdateQueued(), 100 * e.getUpdateQueuedPercentage())).setHeader("Update queued");
		grid.addColumn(e -> String.format("%d (%.2f %%)", e.getSyncError(), 100 * e.getSyncErrorPercentage())).setHeader("Sync errors");
		grid.addColumn(e -> {
			EntityStatus historyStatus = historyData.getFirst().get(e.getEntityClass());
			if (historyStatus != null) {
				if (historyStatus.getSyncronized().equals(e.getSyncronized())) {
					if (e.getUpdateQueued() + e.getNewQueued() + e.getSyncError() == 0L) {
						return "OK";
					}else {
						return "∞";
					}
				}else {
					//FIXME: we should also compute retries for errors
					long remaining = e.getNewQueued() + e.getUpdateQueued() + e.getSyncError();
					if (remaining > 0) {
						long delta = e.getSyncronized() - historyStatus.getSyncronized();
						if (delta > 0) {
							Duration time = Duration.between(historyStatus.getReferenceTime(), e.getReferenceTime());
							
							Duration eta = time.multipliedBy(remaining).dividedBy(delta);
							return DurationFormatUtils.formatDurationWords(eta.getSeconds() * 1000L, true, false);
						}else {
							return "∞";
						}
					}else {
						return "OK";
					}
				}
			}else {
				return "N/A";
			}
			
		}).setHeader("ETA");
		
		add(grid);
		
		XRoadsAsyncUtils.getInstance().submit(this::refreshData);
	}
	
	private void refreshData() {
		getUI().ifPresent((ui) -> {
			LinkedHashMap<Class<?>, EntityStatus> data = new LinkedHashMap<>();
			for(XRoadsModule xRoadsModule : XRoadsCoreServiceBean.getInstance().getEnabledModules(false).values()) {
				for (Class<? extends AbstractEntity> entityClass: XRoadsUtils.ENTITIES_CLASSES) {
					if (XRoadsUtils.moduleHasConsumer(xRoadsModule, entityClass)) {
						EntityStatus status = EntityDao.getInstance().getStatuses(entityClass, xRoadsModule);
						status.setModule(xRoadsModule.getName());
						data.put(entityClass,status);
					}
				} 
			}
			historyData.add(data);
			if (historyData.size() > 10) {
				historyData.removeFirst();
			}
			
			ui.access(()->{
				grid.setItems(data.values());
				XRoadsAsyncUtils.getInstance().schedule(this::refreshData, 10, TimeUnit.SECONDS);
			});
		});
	}
	
}
