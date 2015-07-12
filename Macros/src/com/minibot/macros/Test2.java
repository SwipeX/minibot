package com.minibot.macros;

import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.zulrah.ZulrahEnvironment;
import com.minibot.macros.zulrah.ZulrahMode;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test2", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test2 extends Macro implements Renderable{

    @Override
    public void atStart() {
    }

    @Override
    public void run() {
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
        g.drawString(Players.local().location().toString(), 50, 75);
    }
}