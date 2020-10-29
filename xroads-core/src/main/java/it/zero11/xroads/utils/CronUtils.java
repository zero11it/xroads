package it.zero11.xroads.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import it.zero11.xroads.cron.CronSchedule;

public class CronUtils {
	@SafeVarargs
	public static Map<String, Class<? extends Runnable>> buildCronMap(Class<? extends Runnable> ...crons) {
		Map<String, Class<? extends Runnable>> cronInstances = new HashMap<>();
		for (Class<? extends Runnable> cron : crons) {
			addCron(cronInstances, cron);
		}
		return Collections.unmodifiableMap(cronInstances);
	}
	
	private static void addCron(Map<String, Class<? extends Runnable>> cronInstances, Class<? extends Runnable> clazz){
		CronSchedule cronSchedule = clazz.getAnnotation(CronSchedule.class);
		if (cronSchedule == null)
			throw new RuntimeException(clazz.getSimpleName() + " is missing CronSchedule annotation.");
		if (cronSchedule.hour() == null || (cronSchedule.hour().length > 0 && !isSorted(cronSchedule.hour())))
			throw new RuntimeException(clazz.getSimpleName() + " invalid hour value.");
		if (cronSchedule.minute() == null || (cronSchedule.minute().length > 0 && !isSorted(cronSchedule.minute())))
			throw new RuntimeException(clazz.getSimpleName() + " invalid minute value.");
		if (cronSchedule.second() == null || (cronSchedule.second().length > 0 && !isSorted(cronSchedule.second())))
			throw new RuntimeException(clazz.getSimpleName() + " invalid second value.");

		Class<? extends Runnable> existing = cronInstances.put(clazz.getSimpleName(), clazz);
		if (existing != null)
			throw new RuntimeException(clazz.getSimpleName() + " duplicated name.");
	}

	private static boolean isSorted(int[] values) {
		int previous = values[0] - 1;
		for (int current : values){
			if (current <= previous){
				return false;
			}
			previous = current;
		}
		return true;
	}
}
