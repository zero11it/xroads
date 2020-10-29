import javax.naming.NamingException;

import it.zero11.xroads.utils.modules.core.utils.ReportEmailTemplateRenderer;
import it.zero11.xroads.utils.modules.core.utils.ReportEmailTemplateRenderer.EmailReport;
import it.zero11.xroads.utils.modules.core.utils.SMTPUtils;

public class SendMail {

	public static void main(String[] args) throws NamingException {
		JNDIUtils.setupJNDI();
		
		EmailReport report = ReportEmailTemplateRenderer.buildReport();
		
		SMTPUtils.sendMessage( new String[]{"d.ferri@zero11.it"}, report.getSubject(), report.getBody());
		
	}
}
