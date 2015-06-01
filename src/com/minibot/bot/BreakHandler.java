package com.minibot.bot;

/**
 * Created by tim on 5/29/15.
 */
public abstract class BreakHandler {

    private int index = 0;
    protected int[] lengths;
    protected long[] times;

    /**
     * Use this void to set up lengths and times
     */
    public abstract void init();

    public boolean activated() {
        long time = System.currentTimeMillis();
        return time >= current() && time <= end();
    }

    public long current() {
        return times[index];
    }

    public long end() {
        return times[index] + lengths[index];
    }

    public long elapsed() {
        return activated() ? System.currentTimeMillis() - current() : -1;
    }

    public long remaining() {
        return activated() ? end() - System.currentTimeMillis() : -1;
    }

}
