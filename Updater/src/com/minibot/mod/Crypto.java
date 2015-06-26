package com.minibot.mod;

/**
 * @author Tyler Sedlar
 */
public class Crypto {

	public static String encrypt(String text) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			builder.append((char) (i % 2 == 0 ? i > text.length() / 2 ? c * 'A' : c * 'B' : i < text.length() / 2 ? c * 'Z' : c * 'Y'));
		}
		return builder.toString();
	}

	public static String decrypt(String text) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			builder.append((char) (i % 2 == 0 ? i > text.length() / 2 ? c / 'A' : c / 'B' : i < text.length() / 2 ? c / 'Z' : c / 'Y'));
		}
		return builder.toString();
	}
}