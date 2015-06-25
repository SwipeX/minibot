package com.minibot.macros;

import com.minibot.api.util.Renderable;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test extends Macro implements Renderable {

    @Override
    public void run() {
    }

    @Override
    public void render(Graphics2D g) {
    }
}
