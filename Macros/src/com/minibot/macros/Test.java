package com.minibot.macros;

import com.minibot.api.method.ItemTables;
import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.zulrah.ZulrahEnvironment;
import com.minibot.macros.zulrah.ZulrahMode;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test extends Macro implements Renderable {

    private static Player local;

    @Override
    public void atStart() {
    }

    @Override
    public void run() {
        local = Players.local();
        Npc zulrah = ZulrahEnvironment.findZulrah();
        if (zulrah != null) {
            for (ZulrahMode zm : ZulrahMode.values()) {
                if (zulrah.id() == zm.id) {
                    zm.activate();
                }
            }
        }
    }


    @Override
    public void render(Graphics2D g) {
        int y = 0;
        ItemTables.Entry[] entries = ItemTables.getEquipment();
        for (ItemTables.Entry entry : entries)
            g.drawString(""+entry.id(), 100, 100 + (y += 15));
//        g.drawString("Test " + Game.varp(1021), 50, 50);
        g.drawString(local != null ? Players.local().location().toString() : "null", 50, 75);
    }
}