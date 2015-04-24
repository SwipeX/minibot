package com.minibot.macros;

import com.minibot.api.Macro;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Players;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Widgets;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 4/24/2015
 */
public class PowerMiner extends Macro {

    @Override
    public void run() {
        if (Widgets.validate(15269890 >> 16)) {
            // Widgets.get(15269890 >> 16, 15269890 & 0xfff).processAction("Continue");
        } else if (Inventory.count() != 0) {
            for (final Item item : Inventory.items()) {
                Point p = item.point();
                if (p == null)
                    continue;
                item.processAction(ActionOpcodes.ITEM_ACTION_1, "Drop");
            }
        } else if (Players.local() != null && Players.local().animation() == -1) {
            Point p = Projection.groundToViewport(53 << 7, 49 << 7);
            if (p == null)
                return;
            RuneScape.processAction(53, 49, 3, 1294129333, "Mine", "Rocks", p.x, p.y);
        }
        Time.sleep(2000);
    }
}
