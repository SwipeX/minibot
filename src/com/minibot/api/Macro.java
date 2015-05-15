package com.minibot.api;

import com.minibot.Minibot;
import com.minibot.api.util.Time;

public abstract class Macro {

    Thread thread;

    public final void start() {
        thread = new Thread() {
            public void run() {
                while (!interrupted() && Minibot.instance().isMacroRunning()) {
                    run();
                    Time.sleep(20, 50);
                }
            }
        };
        thread.start();
    }

    public final void interrupt() {
        if (thread != null)
            thread.interrupt();
        thread = null;
    }

    public abstract void run();

}
