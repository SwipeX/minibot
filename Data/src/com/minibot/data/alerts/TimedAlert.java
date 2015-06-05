package com.minibot.data.alerts;

/**
 * Created by tim on 6/5/15.
 */
public abstract class TimedAlert implements Alert {
    long interval = 15 * 60 * 1000; // 15 MIN
    long lastTime;

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
