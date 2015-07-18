package com.minibot.macros;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tim Dekker
 * @since 7/16/15
 */
@Manifest(name = "Altar Runner", author = "Swipe", version = "1.0.0", description = "No banking/entering")
public class AltarRunner extends Macro implements Renderable {

    private static final Tile bank = new Tile(2613, 3092);
    private static final Tile portal = new Tile(2544, 3096);

    @Override
    public void run() {
        if (Bank.viewing()) return;
        Item bone = Inventory.first(i -> i.name().contains("one"));
        GameObject alter = Objects.nearestByName("Altar");
        if (alter != null) {
            if (bone != null) {
                bone.processAction("Use");
                Time.sleep(200);
                alter.processAction(ActionOpcodes.ITEM_ON_OBJECT, "Altar", 61, 51);
                Time.sleep(800);
            } else {
                GameObject portal = Objects.nearestByName("Portal");
                portal.processAction(ActionOpcodes.OBJECT_ACTION_0, "Enter", 51, 51);
                Time.sleep(1200);
            }
        }
        if (bone != null) {
            if (bank.distance() < 6) {
                while (portal.distance() > 2) {
                    Walking.walkTo(portal);
                    Time.sleep(1200, 1400);
                }
            }
        } else {
            if (portal.distance() < 6) {
                while (bank.distance() > 2) {
                    Walking.walkTo(bank);
                    Time.sleep(1200, 1400);
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.drawString(Players.local().location().toString(), 100, 100);
    }
}