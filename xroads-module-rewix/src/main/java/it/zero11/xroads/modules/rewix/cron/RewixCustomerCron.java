package it.zero11.xroads.modules.rewix.cron;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import it.zero11.xroads.cron.AbstractXRoadsCronRunnable;
import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.modules.rewix.XRoadsRewixModule;
import it.zero11.xroads.modules.rewix.api.RewixAPI;
import it.zero11.xroads.modules.rewix.api.RewixAPIException;
import it.zero11.xroads.modules.rewix.model.RewixParamType;
import it.zero11.xroads.modules.rewix.utils.RewixCustomerParser;

@CronSchedule(hour = {}, minute = {0, 15, 30, 45}, second = {0})
public class RewixCustomerCron  extends AbstractXRoadsCronRunnable<XRoadsRewixModule>{

	private static final Logger log = Logger.getLogger(RewixCustomerCron.class);
	private RewixAPI api; 

	@Override
	public void run() {
		if(xRoadsModule.getXRoadsCoreService().getParameterAsBoolean(xRoadsModule, RewixParamType.ENABLE_EXPORT_CUSTOMERS)) {
			api = new RewixAPI(xRoadsModule.getConfiguration().getUsername(), xRoadsModule.getConfiguration().getPassword(), xRoadsModule.getConfiguration().getEndpoint());
			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
			df.setTimeZone(tz);
			String startSyncDateTime = df.format(new Date(System.currentTimeMillis() - 600 * 1000));

			try(InputStream in = api.getCustomers(xRoadsModule.getXRoadsCoreService().getParameter(xRoadsModule, RewixParamType.LAST_CUSTOMERS_SWYNC))){
				XMLReader xr = XMLReaderFactory.createXMLReader();
				
				Map<String, Integer> merchantMap = xRoadsModule.getConfiguration().getMerchantMap();
				Map<String, String> reverseMerchantMap = new HashMap<String, String>();
				for(Map.Entry<String, Integer> entry : merchantMap.entrySet()){
					reverseMerchantMap.put(entry.getValue().toString(), entry.getKey());
				}
				
				RewixCustomerParser rewixCustomerParser = new RewixCustomerParser(xRoadsModule, reverseMerchantMap);
				xr.setContentHandler(rewixCustomerParser);
				xr.setErrorHandler(rewixCustomerParser);
				xr.parse(new InputSource(new BufferedInputStream(in)));
			} catch (RewixAPIException | IOException | SAXException e) {
				throw new RuntimeException(e);
			}
			xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixParamType.LAST_CUSTOMERS_SWYNC, startSyncDateTime);
			log.info("End import Customers");
		}
	}

}
