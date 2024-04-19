package it.zero11.xroads.utils.modules.core.cron;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;
import it.zero11.xroads.utils.modules.core.utils.ClusterSettingsUtils;

@CronSchedule(hour={}, minute={0,5,10,15,20,25,30,35,40,45,50,55}, second={0}, onDeploy=true, force=true)
public class GenerateScheduleCron implements Runnable{

	private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

	@Override
	public void run() {
		Date scheduleFrom;
		if (CronScheduler.FORCE_CURRENT_NODE){
			scheduleFrom = CronDao.getInstance().getLastScheduled(ClusterSettingsUtils.INSTANCE_NAME).getScheduledTime();
		}else{
			scheduleFrom = CronDao.getInstance().getLastScheduled().getScheduledTime();
		}
		Date now = new Date();
		if (scheduleFrom.before(now)){
			scheduleFrom = now;
		}
		Date scheduleTo = new Date(now.getTime() + CronScheduler.SCHEDULE_AHEAD);

		for (XRoadsModule xRoadsModule : XRoadsCoreServiceBean.getInstance().getEnabledModules(true).values()) {
			for (Map.Entry<String, Class<? extends Runnable>> entry: xRoadsModule.getCrons().entrySet()){
				CronSchedule cronSchedule = entry.getValue().getAnnotation(CronSchedule.class);
				Date nextSchedule = getNextSchedule(cronSchedule, scheduleFrom, scheduleTo);
				while(nextSchedule != null){
					addSchedule(entry.getKey(), cronSchedule, nextSchedule);
					nextSchedule = getNextSchedule(cronSchedule, nextSchedule, scheduleTo);
				}
			}
		}

		CronDao.getInstance().cleanSchedule(CronScheduler.CLEAN_DELAY_SUCCESS, CronScheduler.CLEAN_DELAY_FAILED);
	}

	private void addSchedule(String name, CronSchedule cronSchedule, Date scheduledTime) {
		if (CronScheduler.FORCE_CURRENT_NODE){
			CronDao.getInstance().addSchedule(name, ClusterSettingsUtils.INSTANCE_NAME, scheduledTime, cronSchedule.force());
		}else{
			if (cronSchedule.allNodes()){
				List<String> currentNodes = CronDao.getInstance().getCurrentNodeList();
				for (String node:currentNodes){
					CronDao.getInstance().addSchedule(name, node, scheduledTime, cronSchedule.force());
				}
			}else{ 
				CronDao.getInstance().addSchedule(name, null, scheduledTime, cronSchedule.force());
			}
		}
	}

	private Date getNextSchedule(CronSchedule cronSchedule, Date scheduleFrom, Date scheduleTo) {
		ZonedDateTime calendarMaxValue = ZonedDateTime.ofInstant(scheduleTo.toInstant(), UTC_ZONE_ID);
		calendarMaxValue = calendarMaxValue.withNano(0);

		ZonedDateTime currentCalendar = ZonedDateTime.ofInstant(scheduleFrom.toInstant(), UTC_ZONE_ID); 
		currentCalendar = currentCalendar.withNano(0);
		currentCalendar = currentCalendar.plusSeconds(1L);

		while (true) {
			if(currentCalendar.isAfter(calendarMaxValue)) {
				return null;
			}
			
			{
				int sec = currentCalendar.getSecond();
				int origianlSec = sec;

				Integer nextSecond = getNextValue(cronSchedule.second(), sec, 59);
				if (nextSecond != null) {
					sec = nextSecond.intValue();
				} else {
					if (cronSchedule.second().length > 0) {
						sec = cronSchedule.second()[0];
					}else{
						sec = 0;
					}
				}
				if (sec < origianlSec) {
					currentCalendar = currentCalendar.withSecond(sec);
					currentCalendar = currentCalendar.plusMinutes(1L);
				}else if (sec > origianlSec) {
					currentCalendar = currentCalendar.withSecond(sec);
				}
			}

			{
				int min = currentCalendar.getMinute();
				int origianlMin = min;

				// get minute.................................................
				Integer nextMinute = getNextValue(cronSchedule.minute(), min, 59);
				if (nextMinute != null) {
					min = nextMinute.intValue();
				} else {
					if (cronSchedule.minute().length > 0) {
						min = cronSchedule.minute()[0];
					}else{
						min = 0;
					}
				}
				if (min < origianlMin) {
					currentCalendar = currentCalendar.withSecond(0);
					currentCalendar = currentCalendar.withMinute(min);
					currentCalendar = currentCalendar.plusHours(1L);
					continue;
				}else if (min > origianlMin) {
					currentCalendar = currentCalendar.withSecond(0);
					currentCalendar = currentCalendar.withMinute(min);
					continue;
				}
			}
			// get hour...................................................
			{
				int hour = currentCalendar.getHour();
				int originalHour = hour;

				Integer nextHour = getNextValue(cronSchedule.hour(), hour, 23);
				if (nextHour != null) {
					hour = nextHour.intValue();
				} else {
					if (cronSchedule.hour().length > 0) {
						hour = cronSchedule.hour()[0];
					}else{
						hour = 0;
					}
				}
				if (hour < originalHour) {
					currentCalendar = currentCalendar.withSecond(0);
					currentCalendar = currentCalendar.withMinute(0);
					currentCalendar = currentCalendar.withHour(hour);
					currentCalendar = currentCalendar.plusDays(1L);
					continue;
				}else if (hour > originalHour) {
					currentCalendar = currentCalendar.withSecond(0);
					currentCalendar = currentCalendar.withMinute(0);
					currentCalendar = currentCalendar.withHour(hour);
					continue;
				}
			}

			if(currentCalendar.isAfter(calendarMaxValue)) {
				return null;
			}else{
				return Date.from(currentCalendar.toInstant());
			}
		}
	}

	private Integer getNextValue(int[] values, int current, int maxValue) {
		if (values == null || values.length == 0){
			if (current <= maxValue){
				return Integer.valueOf(current);
			}else{
				return Integer.valueOf(0);
			}
		}else{
			for (int value:values){
				if (value >= current){
					return Integer.valueOf(value);
				}
			}
			return null;
		}
	}
}