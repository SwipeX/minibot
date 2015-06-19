package com.minibot.macros;

import com.minibot.api.action.tree.DialogButtonAction;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.RuneScape;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

/**
 * @author Tyler Sedlar
 * @since 6/19/2015
 */
@Manifest(name = "Nightshade", author = "Tyler", version = "1.0.0", description = "Collects nightshade")
public class Nightshade extends Macro {

    private boolean teleport() {
        Item ring = Inventory.first(i -> {
            String name = i.name();
            return name != null && name.contains("Ring of duel");
        });
        if (ring != null) {
            ring.processAction("Rub");
            Time.sleep(900, 1100);
            RuneScape.processAction(new DialogButtonAction(14352384, 2), "", "");
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        teleport();
        Time.sleep(5000);
    }
}
