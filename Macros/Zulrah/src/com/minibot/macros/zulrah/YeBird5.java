package com.minibot.macros.zulrah;

import com.minibot.api.method.Npcs;
import com.minibot.api.util.Renderable;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSNpcDefinition;

import java.awt.*;

/**
 * @author Tim Dekker
 * @since 7/28/15
 */
@Manifest(name = "YeBird5", author = "Swipe", version = "1.0.0", description = "Fuck you")
public class YeBird5 extends Macro implements Renderable {
    @Override
    public void run() {
        for (Npc npc : Npcs.loaded()) {
            if(npc.location().distance() > 5)
                continue;
            RSNpcDefinition def = npc.definition();
            if (def != null) {
                System.out.println("A: " + def.getName());
                def = def.transform();
                if (def != null) {
                    System.out.println("B: "                                    + def.getName());
                }
                System.out.println("-------------------------------------------------------------------------");
            } else {
                System.out.println("NULL @ " + npc.id() + " - " + npc.location());
            }
        }
    }

    @Override
    public void render(Graphics2D g) {

    }
}
