package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.CloseButtonAction;
import com.minibot.api.action.tree.ObjectAction;
import com.minibot.api.action.tree.TableAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/28/2015
 */
@Manifest(name = "Nature crafter", author = "Tyler", version = "1.0.0", description = "Crafts natures")
public class NatureCrafter extends Macro implements Renderable {

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private static final Tile STORE = new Tile(2766, 3120, 0);
    private static final Tile NEAR_RUINS = new Tile(2867, 3021, 0);
    private static final Tile RUINS = new Tile(2868, 3019, 0);
    private static final Tile NEAR_ALTAR = new Tile(2400, 4835, 0);
    private static final Tile ALTAR = new Tile(2400, 4841, 0);

    private static final int NOTED_PURE_ESSENCE = 7937;
    private static final Filter<Item> NOTED_ESSENCE_FILTER = (i -> {
        String name = i.name();
        return name != null && name.equals("Pure essence") && i.amount() > 1;
    });

    private static final Filter<Item> PURE_ESSENCE_FILTER = (i -> {
        String name = i.name();
        return name != null && name.equals("Pure essence") && i.amount() == 1;
    });

    private static final int STORE_WIDGET = 300, STORE_ITEMS_COMPONENT = 75;
    private static final int STORE_FULL_COUNT = 40;

    private static final int EXP_EACH = 9, PROFIT_EACH = 250;

    private int crafted = 0;

    private Npc storeOwner() {
        return Npcs.nearestByName("Jiminua");
    }

    private boolean validateStore() {
        WidgetComponent comp = Widgets.get(STORE_WIDGET, STORE_ITEMS_COMPONENT);
        return comp != null && comp.visible() && storeCount() > 0;
    }

    private int[] storeIds() {
        WidgetComponent comp = Widgets.get(STORE_WIDGET, STORE_ITEMS_COMPONENT);
        return comp != null ? comp.itemIds() : null;
    }

    private int storeCount() {
        int count = 0;
        int[] itemIds = storeIds();
        if (itemIds != null) {
            for (int itemId : itemIds) {
                if (itemId != 0)
                    count++;
            }
        }
        return count;
    }

    private boolean sell(int opcode, int itemId) {
        Item item = Inventory.first(NOTED_ESSENCE_FILTER);
        if (item != null) {
            RuneScape.processAction(new TableAction(opcode, itemId, item.index(), 19726336));
            return true;
        }
        return false;
    }

    private boolean purchase(int opcode, int itemId) {
        int[] itemIds = storeIds();
        if (itemIds == null)
            return false;
        int itemIndex = -1;
        for (int i = 0; i < itemIds.length; i++) {
            if (itemIds[i] == itemId) {
                itemIndex = i;
                break;
            }
        }
        if (itemIndex != -1) {
            RuneScape.processAction(new TableAction(opcode, itemId - 1, itemIndex, 19660875));
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (Inventory.items(PURE_ESSENCE_FILTER).size() == 0) {
            if (NEAR_ALTAR.distance() < 10) {
                    GameObject portal = Objects.nearestByName("Portal");
                    if (portal != null) {
                        portal.processAction("Use");
                        Time.sleep(() -> NEAR_RUINS.distance() < 5, 7000);
                    }
            } else {
                if (STORE.distance() > 6 && storeOwner() == null) {
                    if (Game.energy() >= 20 && !Game.runEnabled()) {
                        Game.setRun(true);
                        Time.sleep(200, 400);
                    }
                    Walking.walkTo(STORE);
                    Time.sleep(400, 600);
                } else {
                    if (validateStore()) {
                        if (storeCount() < STORE_FULL_COUNT) {
                            sell(ActionOpcodes.TABLE_ACTION_3, NOTED_PURE_ESSENCE);
                            Time.sleep(250, 300);
                            sell(ActionOpcodes.TABLE_ACTION_3, NOTED_PURE_ESSENCE);
                            Time.sleep(250, 300);
                            sell(ActionOpcodes.TABLE_ACTION_2, NOTED_PURE_ESSENCE);
                            Time.sleep(250, 300);
                            purchase(ActionOpcodes.TABLE_ACTION_3, NOTED_PURE_ESSENCE);
                            Time.sleep(250, 300);
                            purchase(ActionOpcodes.TABLE_ACTION_3, NOTED_PURE_ESSENCE);
                            Time.sleep(250, 300);
                            purchase(ActionOpcodes.TABLE_ACTION_2, NOTED_PURE_ESSENCE);
                            Time.sleep(250, 300);
                            RuneScape.processAction(new CloseButtonAction(19660891));
                            Time.sleep(() -> !validateStore(), 5000);
                        }
                    } else {
                        Npc owner = storeOwner();
                        if (owner != null) {
                            owner.processAction("Trade");
                            Time.sleep(this::validateStore, 5000);
                        }
                    }
                }
            }
        } else {
            if (NEAR_ALTAR.distance() < 10) {
                int essCount = Inventory.items(PURE_ESSENCE_FILTER).size();
                if (essCount > 0) {
                    GameObject altar = Objects.topAt(ALTAR);
                    if (altar != null) {
                        int preCount = Inventory.items().size();
                        Tile derived = altar.location().derive(-1, -1);
                        RuneScape.processAction(new ObjectAction(ActionOpcodes.OBJECT_ACTION_0, altar.uid(),
                                derived.localX(), derived.localY()));
                        if (Time.sleep(() -> Inventory.items().size() != preCount, 5000)) {
                            crafted += essCount;
                            Time.sleep(2700, 2900);
                        }
                    }
                }
            } else {
                if (NEAR_RUINS.distance() > 5) {
                    if (Game.energy() >= 20 && !Game.runEnabled()) {
                        Game.setRun(true);
                        Time.sleep(200, 400);
                    }
                    Walking.walkTo(NEAR_RUINS);
                    Time.sleep(400, 600);
                } else {
                    GameObject ruins = Objects.topAt(RUINS);
                    if (ruins != null) {
                        Tile derived = ruins.location().derive(-1, -1);
                        RuneScape.processAction(new ObjectAction(ActionOpcodes.OBJECT_ACTION_0, ruins.uid(),
                                derived.localX(), derived.localY()));
                        Time.sleep(() -> NEAR_ALTAR.distance() < 5, 5000);
                    }
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.GREEN);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fCrafted = ValueFormat.format(crafted, COMMA_FORMAT);
        String fCraftedHr = ValueFormat.format(hourly(crafted), COMMA_FORMAT);
        g.drawString("Crafted: " + fCrafted + " (" + fCraftedHr + "/HR)", 13, yOff += 15);
        int profit = (crafted * PROFIT_EACH);
        String fProfit = ValueFormat.format(profit, COMMA_FORMAT);
        String fProfitHr = ValueFormat.format(hourly(profit), THOUSAND_FORMAT);
        g.drawString("Profit: " + fProfit + " (" + fProfitHr + "/HR)", 13, yOff += 15);
        int exp = (crafted * EXP_EACH);
        String fExp = ValueFormat.format(exp, COMMA_FORMAT);
        String fExpHr = ValueFormat.format(hourly(exp), THOUSAND_FORMAT);
        g.drawString("Experience: " + fExp + " (" + fExpHr + "/HR)", 13, yOff + 15);
    }
}
