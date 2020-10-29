package it.zero11.xroads.utils.modules.core.cron;

import org.apache.log4j.Logger;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.model.Customer;
import it.zero11.xroads.model.CustomerRevision;
import it.zero11.xroads.model.Model;
import it.zero11.xroads.model.ModelRevision;
import it.zero11.xroads.model.Product;
import it.zero11.xroads.model.ProductRevision;
import it.zero11.xroads.utils.modules.core.dao.EntityDao;

@CronSchedule(hour={22}, minute={0}, second={0}, onDeploy=true, force=true)
public class CleanOldRevisionCron implements Runnable {
	private static final Logger log = Logger.getLogger(CleanOldRevisionCron.class);
	
	@Override
	public void run() {
		log.info("Start clean old revision");

		EntityDao.getInstance().cleanOldRevision(ProductRevision.class, Product.class);
		EntityDao.getInstance().cleanOldRevision(ModelRevision.class, Model.class);
		EntityDao.getInstance().cleanOldRevision(CustomerRevision.class, Customer.class);
		
		log.info("End clean old revision");
	}

}
