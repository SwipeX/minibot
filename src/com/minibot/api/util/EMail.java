package com.minibot.api.util;

import com.minibot.util.io.Internet;

/**
 * @author Tyler Sedlar
 * @since 5/29/2015
 */
public class EMail {

	private static final String URL = "http://sedlar.me/mail";
	private static final String EQUALS = "=";
	private static final String ARG_SUBJECT = "subject";
	private static final String ARG_TEXT = "text";
	private static final String ARG_NAME = "name";
	private static final String ARG_SENDER = "sender";
	private static final String ARG_RECEIVER = "receiver";
	private static final String BOT_SENDER_NAME = "Bot-Info";
	private static final String BOT_SENDER_EMAIL = "progress@bot.info";

	public static String send(String subject, String text, String senderName, String senderEmail, String receiverEmail) {
		return Internet.post(
				URL,
				ARG_SUBJECT + EQUALS + subject,
				ARG_TEXT + EQUALS + text,
				ARG_NAME + EQUALS + senderName,
				ARG_SENDER + EQUALS + senderEmail,
				ARG_RECEIVER + EQUALS + receiverEmail
		);
	}

	public static String sendBotInfo(String subject, String text, String receiverEmail) {
		return send(subject, text, BOT_SENDER_NAME, BOT_SENDER_EMAIL, receiverEmail);
	}
}
