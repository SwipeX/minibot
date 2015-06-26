package com.minibot.api.util;

/**
 * @author Tyler Sedlar
 * @since 6/19/2015
 */
public class Time {

    private static final String ZERO = "0";
    private static final String COLON = ":";

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

    /**
     * @param condition - sleep until this condition is validated.
     * @param threshold - the amount of time to sleep between conditional checks.
     * @param timeout - sleep until the timeout if condition is not met.
     * @return - true if sleep is broken before timeout.
     */
    public static boolean sleep(Condition condition, int threshold, long timeout) {
        if (condition == null)
            return false;
        long start = Time.millis();
        while (Time.millis() - start < timeout) {
            if (condition.validate())
                return true;
            Time.sleep(threshold);
        }
        return false;
    }

    public static boolean sleep(Condition condition, long timeout) {
        return sleep(condition, 50, timeout);
    }

    public static long toMillis(long nanos) {
        return nanos / 1_000_000;
    }

    public static long toNanos(long millis) {
        return millis * 1_000_000;
    }

    public static long millis() {
        return toMillis(System.nanoTime());
    }

    public static String format(long time) {
        StringBuilder t = new StringBuilder(11);
        long totalSeconds = time / 1000;
        long totalMinutes = totalSeconds / 60;
        long totalHours = totalMinutes / 60;
        long totalDays = totalHours / 24;
        int seconds = (int) totalSeconds % 60;
        int minutes = (int) totalMinutes % 60;
        int hours = (int) totalHours % 24;
        int days = (int) totalDays;
        if (days > 0) {
            if (days < 10)
                t.append(ZERO);
            t.append(days);
            t.append(COLON);
        }
        if (hours < 10)
            t.append(ZERO);
        t.append(hours);
        t.append(COLON);
        if (minutes < 10)
            t.append(ZERO);
        t.append(minutes);
        t.append(COLON);
        if (seconds < 10)
            t.append(ZERO);
        t.append(seconds);
        return t.toString();
    }

    public static int hourly(long elapsed, int actions) {
        return elapsed > 0 ? (int) (actions * 3600000D / elapsed) : 0;
    }
}