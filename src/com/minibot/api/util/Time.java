package com.minibot.api.util;

/**
 * @author Tyler Sedlar & Timothy Dekker
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

    /**
     * @param condition - sleep until this condition is validated.
     * @param timeout - sleep until the timeout if condition is not met.
     * @return - true if sleep is broken before timeout.
     */
    public static boolean sleep(Condition condition, long timeout) {
        long start = System.currentTimeMillis();
        if (condition == null) return false;
        while (!condition.validate()) {
            if (System.currentTimeMillis() - start >= timeout)
                return true;
        }
        return false;
    }

}
