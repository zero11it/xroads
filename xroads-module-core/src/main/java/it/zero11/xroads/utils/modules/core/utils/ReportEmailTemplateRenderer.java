package it.zero11.xroads.utils.modules.core.utils;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

import it.zero11.xroads.model.AbstractEntity;
import it.zero11.xroads.model.ModuleOrder;
import it.zero11.xroads.model.ModuleStatus;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.sync.XRoadsJsonKeys;
import it.zero11.xroads.utils.XRoadsUtils;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.EntityStatus;
import it.zero11.xroads.utils.modules.core.model.ParamType;
import it.zero11.xroads.utils.modules.core.model.WrapFilter;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;

public class ReportEmailTemplateRenderer {
	private static final String HEAD = 
			"<!doctype html>\n" + 
					"<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" + 
					"\n" + 
					"<head>\n" + 
					"  <title> </title>\n" + 
					"  <!--[if !mso]><!-- -->\n" + 
					"  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" + 
					"  <!--<![endif]-->\n" + 
					"  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" + 
					"  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + 
					"  <style type=\"text/css\">\n" + 
					"    #outlook a {\n" + 
					"      padding: 0;\n" + 
					"    }\n" + 
					"\n" + 
					"    body {\n" + 
					"      margin: 0;\n" + 
					"      padding: 0;\n" + 
					"      -webkit-text-size-adjust: 100%%;\n" + 
					"      -ms-text-size-adjust: 100%%;\n" + 
					"    }\n" + 
					"\n" + 
					"    table,\n" + 
					"    td {\n" + 
					"      border-collapse: collapse;\n" + 
					"      mso-table-lspace: 0pt;\n" + 
					"      mso-table-rspace: 0pt;\n" + 
					"    }\n" + 
					"\n" + 
					"    img {\n" + 
					"      border: 0;\n" + 
					"      height: auto;\n" + 
					"      line-height: 100%%;\n" + 
					"      outline: none;\n" + 
					"      text-decoration: none;\n" + 
					"      -ms-interpolation-mode: bicubic;\n" + 
					"    }\n" + 
					"\n" + 
					"    p {\n" + 
					"      display: block;\n" + 
					"      margin: 13px 0;\n" + 
					"    }\n" + 
					"  </style>\n" + 
					"  <!--[if mso]>\n" + 
					"        <xml>\n" + 
					"        <o:OfficeDocumentSettings>\n" + 
					"          <o:AllowPNG/>\n" + 
					"          <o:PixelsPerInch>96</o:PixelsPerInch>\n" + 
					"        </o:OfficeDocumentSettings>\n" + 
					"        </xml>\n" + 
					"        <![endif]-->\n" + 
					"  <!--[if lte mso 11]>\n" + 
					"        <style type=\"text/css\">\n" + 
					"          .mj-outlook-group-fix { width:100%% !important; }\n" + 
					"        </style>\n" + 
					"        <![endif]-->\n" + 
					"  <!--[if !mso]><!-->\n" + 
					"  <link href=\"https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700\" rel=\"stylesheet\" type=\"text/css\">\n" + 
					"  <style type=\"text/css\">\n" + 
					"    @import url(https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700);\n" + 
					"  </style>\n" + 
					"  <!--<![endif]-->\n" + 
					"  <style type=\"text/css\">\n" + 
					"    @media only screen and (min-width:480px) {\n" + 
					"      .mj-column-per-100 {\n" + 
					"        width: 100%% !important;\n" + 
					"        max-width: 100%%;\n" + 
					"      }\n" + 
					"    }\n" + 
					"  </style>\n" + 
					"  <style type=\"text/css\">\n" + 
					"    @media only screen and (max-width:480px) {\n" + 
					"      table.mj-full-width-mobile {\n" + 
					"        width: 100%% !important;\n" + 
					"      }\n" + 
					"      td.mj-full-width-mobile {\n" + 
					"        width: auto !important;\n" + 
					"      }\n" + 
					"    }\n" + 
					"  </style>\n" + 
					"  <style type=\"text/css\">\n" + 
					"    @media (max-width:480px) {\n" + 
					"      .hidden {\n" + 
					"        display: none;\n" + 
					"      }\n" + 
					"    }\n" + 
					"  </style>\n" + 
					"</head>\n" + 
					"\n" + 
					"<body>\n" + 
					"  <div style=\"\">\n" + 
					"    <!--[if mso | IE]>\n" + 
					"      <table\n" + 
					"         align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\"\n" + 
					"      >\n" + 
					"        <tr>\n" + 
					"          <td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\">\n" + 
					"      <![endif]-->\n" + 
					"    <div style=\"margin:0px auto;max-width:800px;\">\n" + 
					"      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%%;\">\n" + 
					"        <tbody>\n" + 
					"          <tr>\n" + 
					"            <td style=\"direction:ltr;font-size:0px;padding:20px 0;text-align:center;\">\n" + 
					"              <!--[if mso | IE]>\n" + 
					"                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" + 
					"                \n" + 
					"        <tr>\n" + 
					"      \n" + 
					"            <td\n" + 
					"               class=\"\" style=\"vertical-align:top;width:600px;\"\n" + 
					"            >\n" + 
					"          <![endif]-->\n" + 
					"              <div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%%;\">\n" + 
					"                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%%\">\n" + 
					"                  <tr>\n" + 
					"                    <td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" + 
					"                      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px;\">\n" + 
					"                        <tbody>\n" + 
					"                          <tr>\n" + 
					"                            <td style=\"width:300px;\"> <img alt=\"XRoads\" height=\"auto\" src=\"https://ftp.dev.zero11.net/xroads/logo-xroads-esteso.png\" style=\"border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%%;font-size:13px;\" width=\"300\" /> </td>\n" + 
					"                          </tr>\n" + 
					"                        </tbody>\n" + 
					"                      </table>\n" + 
					"                    </td>\n" + 
					"                  </tr>\n";

	private static final String STATUS_HEAD = 
			"                  <tr>\n" + 
					"                    <td style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" + 
					"                      <p style=\"border-top:solid 4px #05457E;font-size:1;margin:0px auto;width:100%%;\"> </p>\n" + 
					"                      <!--[if mso | IE]>\n" + 
					"        <table\n" + 
					"           align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-top:solid 4px #05457E;font-size:1;margin:0px auto;width:550px;\" role=\"presentation\" width=\"550px\"\n" + 
					"        >\n" + 
					"          <tr>\n" + 
					"            <td style=\"height:0;line-height:0;\">\n" + 
					"              &nbsp;\n" + 
					"            </td>\n" + 
					"          </tr>\n" + 
					"        </table>\n" + 
					"      <![endif]-->\n" + 
					"                    </td>\n" + 
					"                  </tr>\n" + 
					"                  <tr>\n" + 
					"                    <td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" + 
					"                      <div style=\"font-family:helvetica;font-size:20px;line-height:1;text-align:left;color:#05457E;\">%s Module Status</div>\n" + 
					"                    </td>\n" + 
					"                  </tr>\n" + 
					"                  <tr>\n" + 
					"                    <td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" + 
					"                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\" border=\"0\" style=\"color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:13px;line-height:22px;table-layout:auto;width:100%%;border:none;\">\n" + 
					"                        <tr style=\"border-bottom:1px solid #ecedee;text-align:left;padding:15px 0;\">\n" + 
					"                          <th style=\"padding: 0 5px 0 0;\">Component</th>\n" + 
					"                          <th style=\"padding: 0 5px;\">OK</th>\n" + 
					"                          <th style=\"padding: 0 5px;\">New</th>\n" + 
					"                          <th style=\"padding: 0 5px;\">Updates</th>\n" + 
					"                          <th style=\"padding: 0 0 0 5px;\">Errors</th>\n" + 
					"                        </tr>\n";
	private static final String STATUS_ROW = 
			"                        <tr>\n" + 
					"                          <td style=\"padding: 0 5px 0 0;\">%s</td>\n" + 
					"                          <td style=\"padding: 0 5px;\">%d <span class=\"hidden\">(%.2f %%)</span></td>\n" + 
					"                          <td style=\"padding: 0 5px;\">%d <span class=\"hidden\">(%.2f %%)</span></td>\n" + 
					"                          <td style=\"padding: 0 5px;\">%d <span class=\"hidden\">(%.2f %%)</span></td>\n" + 
					"                          <td style=\"padding: 0 0 0 5px;\">%d <span class=\"hidden\">(%.2f %%)</span></td>\n" + 
					"                        </tr>\n";
	private static final String STATUS_TAIL = 
			"                      </table>\n" + 
					"                    </td>\n" + 
					"                  </tr>\n";

	private static final String LASTERROR_HEAD = 
			"                  <tr>\n" + 
					"                    <td style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" + 
					"                      <p style=\"border-top:solid 4px #05457E;font-size:1;margin:0px auto;width:100%%;\"> </p>\n" + 
					"                      <!--[if mso | IE]>\n" + 
					"        <table\n" + 
					"           align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-top:solid 4px #05457E;font-size:1;margin:0px auto;width:550px;\" role=\"presentation\" width=\"550px\"\n" + 
					"        >\n" + 
					"          <tr>\n" + 
					"            <td style=\"height:0;line-height:0;\">\n" + 
					"              &nbsp;\n" + 
					"            </td>\n" + 
					"          </tr>\n" + 
					"        </table>\n" + 
					"      <![endif]-->\n" + 
					"                    </td>\n" + 
					"                  </tr>\n" +
					"                  <tr>\n" + 
					"                    <td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" + 
					"                      <div style=\"font-family:helvetica;font-size:20px;line-height:1;text-align:left;color:#05457E;\">%s Module Last Errors</div>\n" + 
					"                    </td>\n" + 
					"                  </tr>\n" + 
					"                  <tr>\n" + 
					"                    <td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" + 
					"                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\" border=\"0\" style=\"color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:13px;line-height:22px;table-layout:auto;width:100%%;border:none;\">\n" + 
					"                        <tr style=\"border-bottom:1px solid #ecedee;text-align:left;padding:15px 0;\">\n" + 
					"                          <th style=\"padding: 0 5px 0 0;\">Component</th>\n" + 
					"                          <th style=\"padding: 0 5px 0 0;\">Source Id</th>\n" + 
					"                          <th style=\"padding: 0 5px;\">Date</th>\n" + 
					"                          <th style=\"padding: 0 0 0 5px;\">Error message</th>\n" + 
					"                        </tr>\n";
	private static final String LASTERROR_ROW =
			"                        <tr>\n" + 
					"                          <td style=\"padding: 0 5px 0 0;\">%s</td>\n" + 
					"                          <td style=\"padding: 0 5px 0 0;\">%s</td>\n" + 
					"                          <td style=\"padding: 0 5px;\">%s</td>\n" + 
					"                          <td style=\"padding: 0 0 0 5px;\">%s</td>\n" + 
					"                        </tr>\n";
	private static final String LASTERROR_TAIL =
			"                      </table>\n" + 
					"                    </td>\n" + 
					"                  </tr>\n";

	private static final String TAIL = 
			"                </table>\n" + 
					"              </div>\n" + 
					"              <!--[if mso | IE]>\n" + 
					"            </td>\n" + 
					"          \n" + 
					"        </tr>\n" + 
					"      \n" + 
					"                  </table>\n" + 
					"                <![endif]-->\n" + 
					"            </td>\n" + 
					"          </tr>\n" + 
					"        </tbody>\n" + 
					"      </table>\n" + 
					"    </div>\n" + 
					"    <!--[if mso | IE]>\n" + 
					"          </td>\n" + 
					"        </tr>\n" + 
					"      </table>\n" + 
					"      <![endif]-->\n" + 
					"  </div>\n" + 
					"</body>\n" + 
					"\n" + 
					"</html>";

	public static class EmailReport{
		private StringBuilder buffer = new StringBuilder();
		private Long inProgress = 0L;
		private Long errors = 0L; 
		private Long cronErrors = 0L;

		public String getSubject() {
			StringBuilder bufferSubject = new StringBuilder();
			bufferSubject.append(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.NAME) + " - ");
			if (inProgress == 0L && errors == 0L) {
				bufferSubject.append("Sync Completed");
			}else if (errors == 0L) {
				bufferSubject.append("Sync In Progress (Remainging: ");
				bufferSubject.append(inProgress);
				bufferSubject.append(")");
			}else if (inProgress == 0L) {
				bufferSubject.append("Sync Failed (Errors: ");
				bufferSubject.append(errors);
				bufferSubject.append(")");
			}else {
				bufferSubject.append("Sync In Progress (Remainging: ");
				bufferSubject.append(inProgress);
				bufferSubject.append(", Errors: ");
				bufferSubject.append(errors);
				bufferSubject.append(")");
			}

			bufferSubject.append(" - ");

			if(cronErrors == 0L) {
				bufferSubject.append("All crons without error");
			}else {
				bufferSubject.append("Cron Failed (Errors: ");
				bufferSubject.append(cronErrors);
				bufferSubject.append(")");
			}

			return bufferSubject.toString();
		}

		public String getBody() {
			return buffer.toString();
		}
	}

	public static EmailReport buildReport() {
		EmailReport emailReport = new EmailReport(); 
		emailReport.buffer.append(String.format(HEAD));

		XRoadsCoreServiceBean.getInstance().getEnabledModules(false).values().forEach(module -> {
			buildStatusReport(emailReport, module);
		});
		XRoadsCoreServiceBean.getInstance().getEnabledModules(false).values().forEach(module -> {
			buildErrorReport(emailReport, module, 50);
		});

		XRoadsCoreServiceBean.getInstance().getEnabledModules(false).values().forEach(module -> {
			buildErrorCronReport(emailReport, module, 50);
		});

		emailReport.buffer.append(String.format(TAIL));
		return emailReport;
	}

	private static void buildStatusReport(EmailReport emailReport, XRoadsModule xRoadsModule) {		
		emailReport.buffer.append(String.format(STATUS_HEAD, xRoadsModule.getName()));
		for (Class<? extends AbstractEntity> entityClass : XRoadsUtils.ENTITIES_CLASSES) {
			if (XRoadsUtils.moduleHasConsumer(xRoadsModule, entityClass)) {
				EntityStatus status = EntityDao.getInstance().getStatuses(entityClass, xRoadsModule);
				emailReport.inProgress += status.getNewQueued() + status.getUpdateQueued();
				emailReport.errors += status.getSyncError();
				emailReport.buffer.append(String.format(STATUS_ROW, entityClass.getSimpleName(),
						status.getSyncronized(), 100 * status.getSyncronizedPercentage(),
						status.getNewQueued(), 100 * status.getNewQueuedPercentage(),
						status.getUpdateQueued(), 100 * status.getUpdateQueuedPercentage(),
						status.getSyncError(), 100 * status.getSyncErrorPercentage()));
			}
		}
		emailReport.buffer.append(String.format(STATUS_TAIL));
	}

	private static void buildErrorReport(EmailReport emailReport, XRoadsModule xRoadsModule, Integer limit) {
		SimpleDateFormat dateToStringFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		emailReport.buffer.append(String.format(LASTERROR_HEAD, xRoadsModule.getName()));
		for (Class<? extends AbstractEntity> entityClass : XRoadsUtils.ENTITIES_CLASSES) {
			if (XRoadsUtils.moduleHasConsumer(xRoadsModule, entityClass)) {
				EntityDao.getInstance().getEntities(entityClass, null, limit, new WrapFilter(ModuleStatus.SYNC_ERRORS), ModuleOrder.LAST_ERROR_DATE, xRoadsModule).forEach(item -> {
					emailReport.buffer.append(String.format(LASTERROR_ROW, entityClass.getSimpleName(),
							item.getSourceId(),
							dateToStringFormatter.format(new Date(OffsetDateTime.parse(item.getExternalReferences().path(xRoadsModule.getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR_DATE).asText()).toInstant().toEpochMilli())),
							item.getExternalReferences().path(xRoadsModule.getName()).path(XRoadsJsonKeys.EXTERNAL_REFERENCE_LAST_ERROR)
							));			
				});
			}
		}
		emailReport.buffer.append(String.format(LASTERROR_TAIL));
	}

	private static void buildErrorCronReport(EmailReport emailReport, XRoadsModule xRoadsModule, Integer limit) {
		emailReport.buffer.append(String.format(LASTERROR_HEAD, xRoadsModule.getName() + " Cron"));
		SimpleDateFormat dateToStringFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
		for (String cronName: xRoadsModule.getCrons().keySet()){
			CronDao.getInstance().getErrors(cronName, 50).forEach(cron -> {					
				emailReport.cronErrors += 1;
				emailReport.buffer.append(String.format(LASTERROR_ROW, cronName,
						"-",
						dateToStringFormatter.format(cron.getExecutionTime()),
						cron.getError().contains("<br />") ? cron.getError().split("<br />")[0] : cron.getError()
						));
			});
		}
		emailReport.buffer.append(String.format(LASTERROR_TAIL));
	}

}
