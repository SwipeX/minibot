package com.minibot.bot.macro;

import com.minibot.Minibot;
import com.minibot.api.util.EMail;
import com.minibot.api.util.FileParser;
import com.minibot.api.util.SMS;
import com.minibot.api.util.Time;
import com.minibot.bot.input.MouseDriver;
import com.minibot.bot.random.RandomEvent;
import com.minibot.ui.MacroSelector;
import com.minibot.util.Configuration;

import java.util.List;

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
                MouseDriver driver = MouseDriver.getInstance();
                driver.alive = true;
                driver.randomMouse();
                username = Minibot.instance().client().getUsername();
                password = Minibot.instance().client().getPassword();
                atStart();
                main:
                while (!isInterrupted() && Minibot.instance().macroRunning()) {
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

    public void atStart() {
    }

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

    public final void email(String subject, String text) {
        List<String> lines = FileParser.lines(Configuration.CACHE + "email.ini");
        if (lines != null) {
            String email = lines.get(0);
            EMail.sendBotInfo(subject, text, email);
        }
    }

    public final void sms(String subject, String text) {
        List<String> lines = FileParser.lines(Configuration.CACHE + "sms.ini");
        if (lines != null) {
            String number = lines.get(0);
            String carrierName = SMS.CARRIERS.get(lines.get(1));
            SMS.sendBotInfo(subject, text, number, carrierName);
        }
    }

    public final void addRuntimeCallback(long everyMillis, Runnable callback) {
        new Thread(() -> {
            while (thread != null && !thread.isInterrupted()) {
                Time.sleep((int) everyMillis);
                try {
                    if (thread != null && !thread.isInterrupted()) {
                        callback.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}