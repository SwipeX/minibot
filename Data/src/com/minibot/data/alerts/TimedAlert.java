package com.minibot.data.alerts;

/**
 * @author Tim Dekker
 * @since 6/5/15
 */
public abstract class TimedAlert implements Alert {

    protected long interval = 15 * 60 * 1000; // 15 MIN
    private final long lastTime;

    public TimedAlert(long interval) {
        this.interval = interval;
        lastTime = System.currentTimeMillis();
    }

    @Override
    public boolean validate() {
        return System.currentTimeMillis() - lastTime >= interval;
    }

    @Override
    public abstract void run();
}