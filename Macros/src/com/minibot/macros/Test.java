package com.minibot.macros;

import com.minibot.api.method.Game;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.zulrah.ZulrahEnvironment;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test extends Macro implements Renderable{

    @Override
    public void atStart() {
    }

    private int[] varps;

    @Override
    public void run() {
        int[] current = Game.varps();
        if (varps != null) {
            for (int i = 0; i < current.length; i++) {
                if (varps[i] != current[i])
                    System.out.println(i + ": " + varps[i] + " -> " + current[i]);
            }
        }
        varps = current;
    }


    @Override
    public void render(Graphics2D g) {
        g.drawString("Test " + Game.varp(1021), 50, 50);
        g.drawString(Players.local().location().toString(), 50, 75);
    }
}