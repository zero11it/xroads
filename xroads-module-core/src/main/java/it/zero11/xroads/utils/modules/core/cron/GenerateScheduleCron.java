package it.zero11.xroads.utils.modules.core.cron;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import it.zero11.xroads.cron.CronSchedule;
import it.zero11.xroads.modules.XRoadsModule;
import it.zero11.xroads.utils.modules.core.dao.CronDao;
import it.zero11.xroads.utils.modules.core.sync.XRoadsCoreServiceBean;
import it.zero11.xroads.utils.modules.core.utils.ClusterSettingsUtils;

@CronSchedule(hour={}, minute={0,5,10,15,20,25,30,35,40,45,50,55}, second={0}, onDeploy=true, force=true)
public class GenerateScheduleCron implements Runnable{
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

		for (XRoadsModule xRoadsModule : XRoadsCoreServiceBean.getInstance().getEnabledModules(true)) {
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
		Calendar calendarMaxValue = new GregorianCalendar(); 
		calendarMaxValue.setTime(scheduleTo);
		calendarMaxValue.set(Calendar.MILLISECOND, 0);

		Calendar currentCalendar = new GregorianCalendar(); 
		currentCalendar.setTime(scheduleFrom);
		currentCalendar.set(Calendar.MILLISECOND, 0);
		currentCalendar.add(Calendar.SECOND, 1);

		while (true) {
			if(currentCalendar.after(calendarMaxValue)) {
				return null;
			}
			
			int t = 0;

			{
				int sec = currentCalendar.get(Calendar.SECOND);

				Integer nextSecond = getNextValue(cronSchedule.second(), sec, 59);
				if (nextSecond != null) {
					sec = nextSecond.intValue();
				} else {
					if (cronSchedule.second().length > 0) {
						sec = cronSchedule.second()[0];
					}else{
						sec = 0;
					}
					currentCalendar.set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE) + 1);
				}
				currentCalendar.set(Calendar.SECOND, sec);
			}

			{
				int min = currentCalendar.get(Calendar.MINUTE);
				int hr = currentCalendar.get(Calendar.HOUR_OF_DAY);
				t = -1;

				// get minute.................................................
				Integer nextMinute = getNextValue(cronSchedule.minute(), min, 59);
				if (nextMinute != null) {
					t = min;
					min = nextMinute.intValue();
				} else {
					if (cronSchedule.minute().length > 0) {
						min = cronSchedule.minute()[0];
					}else{
						min = 0;
					}
					hr++;
				}
				if (min != t) {
					currentCalendar.set(Calendar.SECOND, 0);
					currentCalendar.set(Calendar.MINUTE, min);
					setCalendarHour(currentCalendar, hr);
					continue;
				}
				currentCalendar.set(Calendar.MINUTE, min);
			}
			// get hour...................................................
			{
				int hr = currentCalendar.get(Calendar.HOUR_OF_DAY);
				int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
				t = -1;

				Integer nextHour = getNextValue(cronSchedule.hour(), hr, 23);
				if (nextHour != null) {
					t = hr;
					hr = nextHour.intValue();
				} else {
					if (cronSchedule.hour().length > 0) {
						hr = cronSchedule.hour()[0];
					}else{
						hr = 0;
					}
					day++;
				}
				if (hr != t) {
					currentCalendar.set(Calendar.SECOND, 0);
					currentCalendar.set(Calendar.MINUTE, 0);
					currentCalendar.set(Calendar.DAY_OF_MONTH, day);
					setCalendarHour(currentCalendar, hr);
					continue;
				}
				currentCalendar.set(Calendar.HOUR_OF_DAY, hr);
			}

			if(currentCalendar.after(calendarMaxValue)) {
				return null;
			}else{
				return currentCalendar.getTime();
			}
		}
	}

	private void setCalendarHour(Calendar cal, int hour) {
		cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
		if (cal.get(java.util.Calendar.HOUR_OF_DAY) != hour && hour != 24) {
			cal.set(java.util.Calendar.HOUR_OF_DAY, hour + 1);
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