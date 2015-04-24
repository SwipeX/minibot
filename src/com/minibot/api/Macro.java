package com.minibot.api;

import com.minibot.api.util.Time;

/**
 * @author Tyler Sedlar
 * @since 4/24/2015
 */
public abstract class Macro extends Thread {

    public abstract void run();

    @Override
    public final void start() {
        while (!isInterrupted()) {
            run();
            Time.sleep(50, 100);
        }
    }
}
