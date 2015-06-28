package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.TableAction;
import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.RuneScape;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Graphics2D;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Tzhaar Chaos Seller", author = "Tyler", version = "1.0.0", description = "Sells chaos runes")
public class TzhaarChaosSeller extends Macro implements Renderable {

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Item item = Inventory.first(i -> i.name().equals("Chaos rune"));
        if (item != null) {
            RuneScape.processAction(new TableAction(ActionOpcodes.TABLE_ACTION_3, item.id(), item.index(), 19726336));
            Time.sleep(400, 500);
        }
    }

    @Override
    public void render(Graphics2D g) {
    }
}