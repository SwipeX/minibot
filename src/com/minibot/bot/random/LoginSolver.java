package com.minibot.bot.random;

import com.minibot.api.action.tree.InputButtonAction;
import com.minibot.api.method.Game;
import com.minibot.api.method.Login;
import com.minibot.api.method.Mouse;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.bot.macro.Macro;

import java.awt.Color;
import java.awt.Graphics2D;

public class LoginSolver extends RandomEvent {

    private static WidgetComponent component;

    @Override
    public boolean validate() {
        return !Game.playing() && (Login.state() == Login.STATE_MAIN_MENU || Login.state() == Login.STATE_CREDENTIALS);
    }

    @Override
    public void run() {
        if (Login.state() == Login.STATE_MAIN_MENU) {
            Mouse.hop(Login.EXISTING_USER.x, Login.EXISTING_USER.y);
            Mouse.click(true);
            Time.sleep(() -> Login.state() == Login.STATE_CREDENTIALS, Random.nextInt(2500, 5000));
        }
        if (Login.state() == Login.STATE_CREDENTIALS) {
            Login.setUsername(Macro.username());
            Login.setPassword(Macro.password());
            Mouse.hop(Login.LOGIN.x, Login.LOGIN.y);
            Mouse.click(true);
            Time.sleep(() -> {
                component = Widgets.get(378, 6);
                return component != null && component.visible();
            }, Random.nextInt(7500, 10000));
            if (component != null && component.visible()) {
                RuneScape.processAction(new InputButtonAction(24772614));
                Time.sleep(Game::playing, Random.nextInt(5000, 7500));
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Login", 10, 300);
    }
}