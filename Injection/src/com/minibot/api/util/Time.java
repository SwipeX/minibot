package com.minibot.api.util;

/**
 * @author Tyler Sedlar
 */
public class Time {

    public static boolean sleep(int millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    public static boolean sleep(int min, int max) {
        return sleep(Random.nextInt(min, max));
    }

}
