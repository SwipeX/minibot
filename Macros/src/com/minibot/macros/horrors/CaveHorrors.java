package com.minibot.macros.horrors;

import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.bot.macro.Macro;
import com.minibot.macros.horrors.util.Lootables;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 7/10/2015
 */
public class CaveHorrors extends Macro implements Renderable {

    private int profit = 0;

    @Override
    public void atStart() {
        Lootables.initRareDropTable();
        Lootables.initCaveHorrors();
    }

    private boolean underground() {
        return Game.plane() == 1;
    }

    private Npc find() {
        Npc current = Npcs.nearestByFilter(n -> {
            if (n.dead())
                return false;
            String name = n.name();
            if (name != null && name.equals("Cave horror")) {
                if (n.targetIsLocalPlayer())
                    return true;
            }
            return false;
        });
        return current != null ? current : Npcs.nearestByFilter(n -> {
            if (n.dead())
                return false;
            String name = n.name();
            return name != null && name.equals("Cave horror") && n.targetIndex() == -1;
        });
    }

    private boolean attack() {
        Npc npc = find();
        return npc != null && npc.attack();
    }

    @Override
    public void run() {
        int loot = Lootables.loot();
        if (loot != -1) {
            profit += loot;
        } else {
            Player player = Players.local();
            if (player != null) {
                int health = player.healthPercent();
                if (health != -1 && health < 35) {
                    Item food = Inventory.firstFood();
                    if (food != null) {
                        food.processAction("Eat");
                        Time.sleep(() -> player.healthPercent() != health, 2000);
                    }
                } else {
                    if (Inventory.foodCount() > 1) {
                        if (underground()) {
                            if (!player.interacting())
                                attack();
                        } else {
                            // run yo ass to cave
                        }
                    } else {
                        if (underground()) {
                            // run yo ass out
                        } else {
                            // run yo ass to bank
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fProfit = ValueFormat.format(profit, ValueFormat.COMMAS);
        String fProfitHr = ValueFormat.format(hourly(profit), ValueFormat.COMMAS);
        g.drawString("Profit: " + fProfit + " (" + fProfitHr + "/HR)", 13, yOff);
    }
}