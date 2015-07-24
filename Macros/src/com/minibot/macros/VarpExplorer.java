package com.minibot.macros;

import com.minibot.api.method.Game;
import com.minibot.api.util.Renderable;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Graphics2D;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Varp Explorer", author = "Tyler", version = "1.0.0", description = "Locate changing varps")
public class VarpExplorer extends Macro implements Renderable {

    private int[] cached = null;

    @Override
    public void run() {
        int[] varps = Game.varps();
        if (cached != null) {
            for (int i = 0; i < varps.length; i++) {
                int varp = varps[i], cachedVarp = cached[i];
                if (varp != cachedVarp) {
                    System.out.println(i + ": " + cachedVarp + " -> " + varp);
                }
            }
        }
        cached = varps;
    }


    @Override
    public void render(Graphics2D g) {
    }
}