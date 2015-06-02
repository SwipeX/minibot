package com.minibot.bot.macro;

import com.minibot.Minibot;
import com.minibot.api.method.Game;
import com.minibot.api.method.Login;
import com.minibot.api.method.Mouse;
import com.minibot.api.util.Time;
import com.minibot.bot.breaks.BreakHandler;

public abstract class Macro {

    private Thread thread;
    private String username;
    private String password;

    public final void start() {
        Macro macro = this;
        thread = new Thread() {
            public void run() {
                username = Minibot.instance().client().getUsername();
                password = Minibot.instance().client().getPassword();
                while (!isInterrupted() && Minibot.instance().isMacroRunning()) {
                    macro.run();
                    Time.sleep(20, 50);
                    checkLogin();
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

    private void checkLogin() {
        BreakHandler handler = Minibot.instance().breakHandler();
        if (handler != null && handler.activated()) //if no handler/no break, let this continue
            return;//break is current active
        if (!Game.isLoggedIn()) {
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
