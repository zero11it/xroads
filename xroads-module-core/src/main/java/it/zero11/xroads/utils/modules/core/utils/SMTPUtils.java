package it.zero11.xroads.utils.modules.core.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;

public class SMTPUtils {

	public static void sendMessage(String recipients[], String subject, String message){
		try {
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.SMTP_HOST));
			props.put("mail.smtp.auth", "true");

			Session mailSession = Session.getDefaultInstance(props, new SMTPAuthenticator());

			Transport transport = mailSession.getTransport();

			MimeMessage mimeMessage = new MimeMessage(mailSession);
			mimeMessage.setSubject(subject);
			mimeMessage.setContent(message, "text/html; charset=UTF-8");
			mimeMessage.setFrom(new InternetAddress(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.SMTP_FROM)));
			
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i].trim());
			}

			mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);
			
			transport.connect();
			transport.sendMessage(mimeMessage,mimeMessage.getRecipients(Message.RecipientType.TO));
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public static void sendEmail(String[] to, IParamType subject, IParamType template, String language, Map<String, Object> templateVariables) {
		try {
			final String body = ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, template);
			final String subjectText = ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, subject);
			
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
			cfg.setDefaultEncoding("UTF-8");
			cfg.setLocale(new Locale(language));
			cfg.setClassForTemplateLoading(SMTPUtils.class, "/");
						
			Template bodyTemplate = new Template("body", new StringReader(body), cfg);
			Template subjectTemplate = new Template("subject", new StringReader(subjectText), cfg);			
			
			StringWriter subjectWriter = new StringWriter();
			subjectTemplate.process(templateVariables, subjectWriter);
			
			StringWriter contentWriter = new StringWriter();
			bodyTemplate.process(templateVariables, contentWriter);
			      
			sendMessage(to, subjectWriter.toString(), contentWriter.toString());
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
		}   
	}

	private static class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.SMTP_USER), ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.SMTP_PASSWORD));
		}
	}
}
