package com.minibot.macros;

import com.minibot.api.method.Inventory;
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
        }
        return false;
        /*
        ItemAction:[TableAddress(9764864,2)=<149#0#2> | ItemId=2554 | ItemIndex=2 | ActionIndex=3]
        Action<BUTTON_DIALOG>(id=30,args=[ 0 | 0 | 14352384 ])
         */
    }

    @Override
    public void run() {
        teleport();
        Time.sleep(5000);
    }
}
