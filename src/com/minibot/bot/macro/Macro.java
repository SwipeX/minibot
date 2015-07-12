package com.minibot.bot.macro;

import com.minibot.Minibot;
import com.minibot.api.util.Time;
import com.minibot.bot.random.RandomEvent;
import com.minibot.ui.MacroSelector;

public abstract class Macro {

    private Thread thread;
    private static String username;
    private static String password;
    private long start;

    public final void start() {
        Macro macro = this;
        thread = new Thread() {
            @Override
            public void run() {
                username = Minibot.instance().client().getUsername();
                password = Minibot.instance().client().getPassword();
                atStart();
                main: while (!isInterrupted() && Minibot.instance().isMacroRunning()) {
                    for (RandomEvent random : RandomEvent.SOLVERS) {
                        if (random.validate()) {
                            random.setSolving(true);
                            random.run();
                            continue main;
                        } else {
                            random.setSolving(false);
                        }
                    }
                    macro.run();
                    Time.sleep(20, 50);
                }
            }
        };
        thread.start();
        start = Time.millis();
    }

    public void atStart() {}

    public long runtime() {
        return Time.millis() - start;
    }

    public int hourly(int i) {
        return Time.hourly(runtime(), i);
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void interrupt() {
        MacroSelector.halt();
    }

    public abstract void run();

    public static String username() {
        return username;
    }

    public static String password() {
        return password;
    }
}