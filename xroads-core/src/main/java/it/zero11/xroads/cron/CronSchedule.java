package it.zero11.xroads.cron;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CronSchedule{
	int[] second();
	int[] minute();
	int[] hour();
	boolean force() default false;
	boolean allNodes() default false;
	boolean onDeploy() default false;
}