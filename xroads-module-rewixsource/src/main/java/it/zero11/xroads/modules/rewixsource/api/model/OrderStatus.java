package it.zero11.xroads.modules.rewixsource.api.model;

public class OrderStatus {
	public static final int FAILED_ON_TARGET = -1;
	public static final int PENDING = 0;	
	public static final int MONEY_WAITING = 1;
	public static final int TO_DISPATCH = 2;
	public static final int DISPATCHED = 3;
	public static final int BOOKED = 5;
	public static final int CANCELLED = 2000;
	public static final int VERIFY_FAILED = 2002;
	public static final int WORKING_ON = 3001;
	public static final int READY = 3002;
	public static final int DROPSHIPPER_GROWING = 5003;
}
