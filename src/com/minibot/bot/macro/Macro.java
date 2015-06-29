package com.minibot.bot.macro;

import com.minibot.Minibot;
import com.minibot.api.method.Game;
import com.minibot.api.method.Login;
import com.minibot.api.method.Mouse;
import com.minibot.api.util.Time;
import com.minibot.bot.breaks.BreakHandler;
import com.minibot.bot.random.RandomEvent;
import com.minibot.ui.MacroSelector;

public abstract class Macro {

    private Thread thread;
    private String username;
    private String password;
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
                    checkLogin();
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

    private void checkLogin() {
        BreakHandler handler = Minibot.instance().breakHandler();
        if (handler != null && handler.activated()) //if no handler/no break, let this continue
            return;//break is current active
        if (!Game.playing()) {
            if (Login.state() == Login.STATE_MAIN_MENU) {
                Mouse.hop(Login.EXISTING_USER.x, Login.EXISTING_USER.y);
                Mouse.click(true);
                Time.sleep(600, 700);
            } else if (Login.state() == Login.STATE_CREDENTIALS) {
                Login.setUsername(username);
                Login.setPassword(password);
                Mouse.hop(Login.LOGIN.x, Login.LOGIN.y);
                Mouse.click(true);
                Time.sleep(600, 700);
            }
        }
    }
}