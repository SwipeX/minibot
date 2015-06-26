package com.minibot.macros;

import com.minibot.api.method.Ground;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test2", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test2 extends Macro implements Renderable {

    private Tile tile;

    @Override
    public void run() {
        GameObject object = Objects.nearestByName("Flowers");
        if (object != null)
            System.out.println("NIGGERED");
    }

    @Override
    public void render(Graphics2D g) {
    }
}