package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.tree.Action;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.*;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSObjectDefinition;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 6/19/2015
 */
@Manifest(name = "Man Killer", author = "Tyler", version = "1.0.0", description = "Kills men and loots herbs")
public class ManKiller extends Macro implements Renderable {

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private static final Map<String, Integer> LOOT_PRICES = new HashMap<>();
    private static final Tile BANK_BOOTH = new Tile(3096, 3493, 0);
    private static final Tile DOOR = new Tile(3101, 3509, 0);

    private int looted;
    private int profit;
    private int startExp;

    @Override
    public void atStart() {
        LOOT_PRICES.put("Grimy harralander", 425);
        LOOT_PRICES.put("Grimy ranarr weed", 6285);
        LOOT_PRICES.put("Grimy toadflax", 1785);
        LOOT_PRICES.put("Grimy irit leaf", 831);
        LOOT_PRICES.put("Grimy avantoe", 2210);
        LOOT_PRICES.put("Grimy kwuarm", 2397);
        LOOT_PRICES.put("Grimy snapdragon", 6501);
        LOOT_PRICES.put("Grimy cadantine", 1412);
        LOOT_PRICES.put("Grimy lantadyme", 1302);
        LOOT_PRICES.put("Grimy dwarf weed", 1991);
        LOOT_PRICES.put("Grimy torstol", 6970);
        startExp = Game.totalExperience();
    }

    private boolean openBank() {
        GameObject booth = Objects.topAt(BANK_BOOTH);
        if (booth != null) {
            booth.processAction("Bank");
            return Time.sleep(Bank::viewing, 20000);
        }
        return false;
    }

    private boolean doorLocked(GameObject object) {
        if (object == null)
            return false;
        RSObjectDefinition def = object.definition();
        if (def == null)
            return false;
        String[] actions = def.getActions();
        return actions != null && Action.indexOf(actions, "Open") != -1;
    }

    private boolean locked() {
        return doorLocked(Objects.topAt(DOOR));
    }

    private boolean openDoors() {
        GameObject door = Objects.topAt(DOOR);
        if (door != null) {
            if (doorLocked(door)) {
                door.processAction("Open");
                Time.sleep(2000, 2500);
            }
        }
        return true;
    }

    private boolean attack() {
        Character target = Players.local().target();
        if (target == null || (target.maxHealth() > 0 && target.health() <= 0)) {
            Npc npc = Npcs.nearestByFilter(n -> {
                Character npcTarget = n.target();
                if (npcTarget != null)
                    return npcTarget.equals(Players.local());
                if (n.health() <= 0 && n.maxHealth() > 0)
                    return false;
                String name = n.name();
                return name != null && name.equals("Man");
            });
            if (npc != null) {
                npc.processAction("Attack");
                if (Time.sleep(() -> {
                    Character playerTarget = Players.local().target();
                    return playerTarget != null && playerTarget.maxHealth() > 0;
                }, 5000)) {
                    Time.sleep(600, 800);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean loot() {
        GroundItem loot = Ground.nearestByFilter(i -> {
            String name = i.name();
            return name != null && LOOT_PRICES.containsKey(name);
        });
        if (loot != null) {
            String name = loot.name();
            int count = Inventory.count();
            loot.processAction("Take");
            if (Time.sleep(() -> Inventory.count() != count, 10000)) {
                looted++;
                profit += LOOT_PRICES.get(name);
            }
        }
        return false;
    }

    private boolean prepareInventory() {
        if (Inventory.count() > 0) {
            WidgetComponent component = Widgets.get(12, 27);
            if (component != null) {
                component.processAction("Deposit inventory");
                Time.sleep(300, 500);
            } else {
                return false;
            }
        }
        WidgetComponent component = Widgets.get(12, 3);
        if (component != null) {
            component = component.children()[11];
            component.processAction("Close");
            return Time.sleep(() -> !Bank.viewing(), 3000);
        }
        return true;
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (Inventory.full()) {
            if (Bank.viewing()) {
                prepareInventory();
            } else {
                if (BANK_BOOTH.distance() > 10 && locked()) {
                    openDoors();
                } else {
                    openBank();
                }
            }
        } else {
            if (DOOR.distance() > 10) {
                Walking.walkTo(DOOR);
                Time.sleep(600, 800);
            } else {
                if (locked()) {
                    openDoors();
                } else {
                    attack();
                    loot();
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fLooted = ValueFormat.format(looted, COMMA_FORMAT);
        g.drawString("Looted: " + fLooted + " (" + hourly(looted) + "/HR)", 13, yOff += 15);
        String fProfit = ValueFormat.format(profit, COMMA_FORMAT);
        String fProfitHr = ValueFormat.format(hourly(profit), THOUSAND_FORMAT);
        g.drawString("Profit: " + fProfit + " (" + fProfitHr + "/HR)", 13, yOff += 15);
        int exp = Game.totalExperience() - startExp;
        String fExp = ValueFormat.format(exp, COMMA_FORMAT);
        String fExpHr = ValueFormat.format(hourly(exp), THOUSAND_FORMAT);
        g.drawString("Experience: " + fExp + " (" + fExpHr + "/HR)", 13, yOff + 15);
    }
}