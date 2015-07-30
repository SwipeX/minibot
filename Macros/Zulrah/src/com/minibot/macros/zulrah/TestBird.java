package com.minibot.macros.zulrah;

import com.minibot.api.util.Renderable;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.zulrah.action.Trident;

import java.awt.*;

/**
 * @author Tim Dekker
 * @since 7/28/15
 */
@Manifest(name = "Test Bird", author = "Swipe", version = "1.0.0", description = "Fuck you")
public class TestBird extends Macro implements Renderable {

    @Override
    public void atStart() {
        Trident.setToggled(true);
    }

    @Override
    public void run() {
        Trident.act();
    }

    @Override
    public void render(Graphics2D g) {

    }
}
