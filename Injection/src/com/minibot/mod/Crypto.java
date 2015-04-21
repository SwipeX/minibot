package com.minibot.mod;

/**
 * @author Tyler Sedlar
 */
public class Crypto {

	public static String decrypt(String string) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			builder.append((char) (i % 2 == 0 ? i > string.length() / 2 ? c / 'A' : c / 'B' :
                    i < string.length() / 2 ? c / 'Z' : c / 'Y'));
		}
		return builder.toString();
	}
}
