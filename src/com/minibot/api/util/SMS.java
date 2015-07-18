package com.minibot.api.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 5/31/2015
 */
public class SMS {

	public static final String CARRIER_TMOBILE = "tmomail.net";
	public static final String CARRIER_VIRGIN_MOBILE = "vmobl.com";
	public static final String CARRIER_SINGULAR = "cingularme.com";
	public static final String CARRIER_SPRINT = "messaging.sprintpcs.com";
	public static final String CARRIER_VERIZON = "vtext.com";
	public static final String CARRIER_NEXTEL = "messaging.nextel.com";
	public static final String CARRIER_ATT = "txt.att.net";
	public static final String CARRIER_US_CELLULAR = "email.uscc.net";
	public static final String CARRIER_SUNCOM = "tms.suncom.com";
	public static final String CARRIER_POWERTEL = "ptel.net";
	public static final String CARRIER_ALLTEL = "message.alltel.com";
	public static final String CARRIER_METRO_PCS = "mymetropcs.com";
	public static final String CARRIER_3RIVERS = "sms.3rivers.net";
	public static final String CARRIER_ACS_WIRELESS = "paging.acswireless.com";
	public static final String CARRIER_WIND_MOBILE = "txt.windmobile.ca";
	public static final String CARRIER_ORANGE = "orange.net";
	public static final String CARRIER_ORANGE_MUMBAI = "orangemail.co.in";
	public static final String CARRIER_ORANGE_NL = "sms.orange.nl";
	public static final String CARRIER_VODAFONE_UK = "vodafone.net";
	public static final Map<String, String> CARRIERS = new HashMap<>();

	static {
		Field[] fields = SMS.class.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.getName().startsWith("CARRIER_")) {
				try {
					CARRIERS.put(field.getName(), (String) field.get(null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final String AT = "@";
	private static final String BOT_SENDER_NAME = "Bot-Info";
	private static final String BOT_SENDER_EMAIL = "progress@bot.info";

	public static String send(String subject, String text, String senderName, String senderEmail,
	                          String phoneNumber, String carrier) {
		return EMail.send(subject, text, senderName, senderEmail, (phoneNumber + AT + carrier));
	}

	public static String sendBotInfo(String subject, String text, String phoneNumber, String carrier) {
		return send(subject, text, BOT_SENDER_NAME, BOT_SENDER_EMAIL, phoneNumber, carrier);
	}
}
