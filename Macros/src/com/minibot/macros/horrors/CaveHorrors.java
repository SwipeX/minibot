package com.minibot.macros.horrors;

import com.minibot.api.method.*;
import com.minibot.api.method.web.TilePath;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.horrors.util.Lootables;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 7/10/2015
 */
@Manifest(name = "Cave Horrors", author = "Tyler", version = "1.0.0", description = "Kills cave horrors")
public class CaveHorrors extends Macro implements Renderable {

    private static final Tile BANK = new Tile(3680, 2982, 0);
    private static final Tile CAVE = new Tile(3760, 2973, 0);
    private static final Tile UNDERGROUND_CAVE = new Tile(3748, 9373, 0);

    private static final TilePath BANK_TO_CAVE = new TilePath(
            new Tile(3679, 3009, 0),
            new Tile(3702, 3008, 0),
            new Tile(3726, 3006, 0),
            new Tile(3757, 3003, 0),
            new Tile(3762, 2989, 0),
            new Tile(3755, 2981, 0),
            new Tile(3760, 2973, 0)
    );

    private static final TilePath CAVE_TO_BANK = new TilePath(
            new Tile(3755, 2981, 0),
            new Tile(3762, 2989, 0),
            new Tile(3757, 3003, 0),
            new Tile(3726, 3006, 0),
            new Tile(3702, 3008, 0),
            new Tile(3679, 3009, 0),
            new Tile(3680, 2982, 0)
    );

    private int profit = 0;
    private int foodId = -1;

    @Override
    public void atStart() {
        Lootables.initRareDropTable();
        Lootables.initCaveHorrors();
    }

    private boolean underground() {
        Player player = Players.local();
        return player != null && player.y() > 9000;
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
                if (health != -1 && health < 35 && !Bank.viewing()) {
                    Item food = Inventory.firstFood();
                    if (food != null) {
                        foodId = food.id();
                        food.processAction("Eat");
                        Time.sleep(() -> player.healthPercent() != health, 2000);
                    }
                }
                if (Inventory.foodCount() > 1) {
                    if (underground()) {
                        attack();
                    } else {
                        if (CAVE.distance() > 5) {
                            if (Game.energy() >= 20)
                                Game.setRun(true);
                            BANK_TO_CAVE.step();
//                            WebPath.build(CAVE).step(Path.Option.TOGGLE_RUN);
                        } else {
                            BANK_TO_CAVE.reset();
                            GameObject cave = Objects.nearestByName("Cave entrance");
                            if (cave != null) {
                                cave.processAction("Enter");
                                if (Time.sleep(Widgets::viewingContinue, 2500)) {
                                    Widgets.processContinue();
                                    if (Time.sleep(Widgets::viewingDialog, 2500)) {
                                        Widgets.processDialogOption(0);
                                        Time.sleep(this::underground, 5000);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (underground()) {
                        if (UNDERGROUND_CAVE.distance() > 5) {
                            Walking.walkTo(UNDERGROUND_CAVE);
                        } else {
                            GameObject cave = Objects.nearestByName("Cave");
                            if (cave != null) {
                                cave.processAction("Exit");
                                Time.sleep(() -> !underground(), 5000);
                            }
                        }
                    } else {
                        if (BANK.distance() > 5) {if (Game.energy() >= 20)
                            Game.setRun(true);
                            CAVE_TO_BANK.step();
//                            WebPath.build(BANK).step(Path.Option.TOGGLE_RUN);
                        } else {
                            CAVE_TO_BANK.reset();
                            if (Bank.viewing()) {
                                if (Inventory.count() == 1) {
                                    Item food = Bank.first(i -> i.id() == foodId);
                                    if (foodId != -1 && food != null) {
                                        food.processAction("Withdraw-10");
                                        Time.sleep(300, 500);
                                        food.processAction("Withdraw-5");
                                        Time.sleep(300, 500);
                                    } else {
                                        System.out.println("Out of food");
                                        interrupt();
                                    }
                                } else {
                                    Bank.depositAllExcept(i -> {
                                        String name = i.name();
                                        return name != null && name.equals("Sapphire lantern");
                                    });
                                }
                            } else {
                                Bank.openBooth();
                            }
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
        g.drawString("Profit: " + fProfit + " (" + fProfitHr + "/HR)", 13, yOff + 15);
    }
}
