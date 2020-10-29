package it.zero11.xroads.utils.modules.core.cron;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.utils.modules.core.utils.ReportEmailTemplateRenderer;
import it.zero11.xroads.utils.modules.core.utils.SMTPUtils;
import it.zero11.xroads.utils.modules.core.utils.ReportEmailTemplateRenderer.EmailReport;

@CronSchedule(hour={}, minute={0,30}, second={0}, onDeploy=false)
public class EmailReportCron implements Runnable {
	@Override
	public void run() {
		EmailReport report = ReportEmailTemplateRenderer.buildReport();
		SMTPUtils.sendMessage(
				ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.WARNING_NOTIFICATION_EMAILS).split(","),
				report.getSubject(),
				report.getBody());
	}
}
