package it.zero11.xroads.modules.rewix.utils;

public class Constants {

	public static final String REWIX_ENDPOINT = "rewix.endpoint";
	public static final String REWIX_USERNAME = "rewix.username";
	public static final String REWIX_PASSWORD = "rewix.password";

	public static final String CROSSROADS_ECOM_ENDPOINT = "crossroads.ecom.endpoint";

	public static final int ORDER_PENDING=0;
	public static final int ORDER_MONEYWAITING=1;

	public static final int ORDER_TODISPATCH=2;
	public static final int ORDER_DISPATCHED=3;
		
	public static final int ORDER_BOOKED = 5;
	public static final int ORDER_NOT_AUTHORIZED = 5001;
	public static final int ORDER_AUTHORIZED = 5002;
	public static final int ORDER_DS_GROWING = 5003;
	public static final int ORDER_AUTHORIZED_PHOTO = 5004;
	
	
	
	public static final int ORDER_DROPSHIPPING = 6;

	
	
	public static final int ORDER_MONEYERROR=1000;
	
	public static final int ORDER_CANCELED=2000;
	
	public static final int ORDER_EXCEPTION=2001;
	public static final int ORDER_VERIFY_FAILED=2002;
	
	public static final int ORDER_REFUND=2003;
	public static final int ORDER_REJECTED=2005;
	
	public static final int ORDER_WALLET_PAYMENT_SCHEDULED=4000;
	
	//Logistics
	public static final int ORDER_WORKING_ON=3001;
	public static final int ORDER_READY=3002;
	public static final int ORDER_NOT_DISPATCHABLE=3003;
	public static final int ORDER_MISSINGITEMS=3004;
	public static final int ORDER_DISPATCHNOTALLOWED=3005;

	public static final int ORIGIN_FRONTEND = 0;
	public static final int ORIGIN_API = 1;
	public static final int ORIGIN_ADMIN = 2;
	public static final int ORIGIN_SUBSCRIPTION = 3;
	
	//User
	public static final int USER_STATUS_PENDING=1;
	public static final int USER_STATUS_ENABLED=2;
	public static final int USER_STATUS_DISABLED=3;
	public static final int USER_STATUS_WAITING_LIST=4;
	public static final int USER_STATUS_MISSINGPROFILE=5;
	public static final int USER_STATUS_MISSINGPROFILE_NOT_VALIDATED=6;
	public static final int USER_STATUS_MISSINGPROFILE_CAPTCHA_ONLY=7;
	public static final int USER_STATUS_MISSINGPROFILE_WAITING_EMAIL_CONFIRM=8;
	
}
