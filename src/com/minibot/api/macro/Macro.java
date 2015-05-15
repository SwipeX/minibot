package com.minibot.api.macro;

import com.minibot.Minibot;
import com.minibot.api.method.Game;
import com.minibot.api.method.Login;
import com.minibot.api.method.Mouse;
import com.minibot.api.util.Time;

public abstract class Macro {

    Thread thread;
    private String username;
    private String password;

    public final void start() {
        thread = new Thread() {
            public void run() {
                username = Minibot.instance().client().getUsername();
                password = Minibot.instance().client().getPassword();
                while (!interrupted() && Minibot.instance().isMacroRunning()) {
                    run();
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

    protected final void checkLogin() {
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