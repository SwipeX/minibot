package com.minibot.macros;

import com.minibot.api.macro.Macro;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;

/**
 * @author Tyler Sedlar
 * @since 4/24/2015
 */
public class CowKiller extends Macro {

    @Override
    public void run() {
        if (Players.local() != null && Players.local().targetIndex() == -1) {
            Npc npc = Npcs.nearest(n -> {
                if (n.maxHealth() > 0 && n.health() <= 0)
                    return false;
                String name = n.name();
                return name != null && n.targetIndex() == -1 && (name.equals("Cow") || name.equals("Cow calf"));
            });
            if (npc == null)
                return;
            npc.processAction("Attack");
        }
        Time.sleep(2000);
    }
}