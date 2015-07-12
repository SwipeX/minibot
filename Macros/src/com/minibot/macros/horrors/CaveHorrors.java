package com.minibot.macros.horrors;

import com.minibot.api.method.*;
import com.minibot.api.method.web.TilePath;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.*;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.horrors.util.Lootables;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Tyler Sedlar
 * @since 7/10/2015
 */
@Manifest(name = "Cave Horrors", author = "Tyler", version = "1.0.0", description = "Kills cave horrors")
public class CaveHorrors extends Macro implements Renderable {

    private static final Tile BANK = new Tile(3680, 2982, 0);
    private static final Tile CAVE = new Tile(3749, 2973, 0);
    private static final Tile UNDERGROUND_CAVE = new Tile(3748, 9373, 0);

    private static final TilePath BANK_TO_CAVE = new TilePath(
            new Tile(3679, 3009, 0),
            new Tile(3702, 3008, 0),
            new Tile(3726, 3006, 0),
            new Tile(3757, 3003, 0),
            new Tile(3762, 2989, 0),
            new Tile(3755, 2981, 0),
            new Tile(3749, 2973, 0)
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

    private static final Area KILL_ZONE = new Area(new Tile(3717, 9346, 0), new Tile(3769, 9390, 0));

    private int profit;
    private int foodId = -1;

    private String status = "";
    private boolean startedFlick;

    @Override
    public void atStart() {
        Lootables.setArea(KILL_ZONE);
        Lootables.initRareDropTable();
        Lootables.initCaveHorrors();
    }

    private boolean underground() {
        Player player = Players.local();
        return player != null && player.y() > 9000;
    }

    private Npc findBat() {
        return Npcs.nearestByFilter(n -> {
            String name = n.name();
            return name != null && name.equals("Albino bat") && n.targetIsLocalPlayer();
        });
    }

    private Npc find() {
        Npc current = Npcs.nearestByFilter(n -> {
            if (n.dead() || !KILL_ZONE.contains(n.location())) {
                return false;
            }
            String name = n.name();
            if (name != null && name.equals("Cave horror")) {
                if (n.targetIsLocalPlayer()) {
                    return true;
                }
            }
            return false;
        });
        return current != null ? current : Npcs.nearestByFilter(n -> {
            if (n.dead() || !KILL_ZONE.contains(n.location())) {
                return false;
            }
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
        WidgetComponent protect = Widgets.get(271, 18);
        if (Lootables.valid()) {
            startedFlick = false;
            if (protect != null) {
                protect.processAction("Deactivate");
            }
            int loot = Lootables.loot();
            if (loot != -1) {
                profit += loot;
            }
        } else {
            Player player = Players.local();
            if (player != null) {
                if (Game.energy() >= 20) {
                    Game.setRun(true);
                }
                int health = player.healthPercent();
                if (health != -1 && health < 35 && !Bank.viewing()) {
                    Item food = Inventory.firstFood();
                    if (food != null) {
                        if (protect != null) {
                            protect.processAction("Deactivate");
                        }
                        status = "Eating";
                        foodId = food.id();
                        food.processAction("Eat");
                        Time.sleep(() -> player.healthPercent() != health, 2000);
                    }
                }
                if (Inventory.foodCount() > 1) {
                    if (underground()) {
                        if (!KILL_ZONE.contains(player)) {
                            Walking.walkTo(UNDERGROUND_CAVE);
                            Time.sleep(800, 1000);
                        } else {
                            status = "Attacking";
                            Npc bat = findBat();
                            if (bat != null) {
                                if (protect != null) {
                                    protect.processAction("Deactivate");
                                }
                                Walking.walkTo(UNDERGROUND_CAVE);
                                Time.sleep(800, 1000);
                            } else {
                                if (player.interacting()) {
                                    if (protect != null) {
                                        protect.processAction("Activate");
                                        Time.sleep(570, 620);
                                        if (!startedFlick) {
                                            Time.sleep(80, 120);
                                            startedFlick = true;
                                        }
                                        protect.processAction("Deactivate");
                                        Time.sleep(60, 80);
                                    }
                                } else {
                                    startedFlick = false;
                                    if (protect != null) {
                                        protect.processAction("Deactivate");
                                    }
                                    if (attack()) {
                                        Time.sleep(1500, 2000);
                                    }
                                }
                            }
                        }
                    } else {
                        if (CAVE.distance() > 5) {
                            status = "Walking to cave";
                            BANK_TO_CAVE.step();
//                            WebPath.build(CAVE).step(Path.Option.TOGGLE_RUN);
                        } else {
                            status = "Entering cave";
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
                            status = "Walking to cave exit";
                            Walking.walkTo(UNDERGROUND_CAVE);
                            Time.sleep(800, 1000);
                        } else {
                            status = "Exiting cave";
                            GameObject cave = Objects.nearestByName("Cave");
                            if (cave != null) {
                                cave.processAction("Exit");
                                Time.sleep(() -> !underground(), 5000);
                            }
                        }
                    } else {
                        if (BANK.distance() > 5) {
                            status = "Walking to bank";
                            CAVE_TO_BANK.step();
//                            WebPath.build(BANK).step(Path.Option.TOGGLE_RUN);
                        } else {
                            if (Bank.viewing()) {
                                if (Inventory.count() == 1) {
                                    status = "Withdrawing food";
                                    Item food = Bank.first(i -> i.id() == foodId);
                                    if (foodId != -1 && food != null) {
                                        food.processAction("Withdraw-10");
                                        Time.sleep(300, 500);
                                        food.processAction("Withdraw-5");
                                        Time.sleep(300, 500);
                                    } else {
                                        status = "Out of food";
                                        System.out.println("Out of food");
                                        interrupt();
                                    }
                                } else {
                                    status = "Depositing loot";
                                    Bank.depositAllExcept(i -> {
                                        String name = i.name();
                                        return name != null && name.equals("Sapphire lantern");
                                    });
                                }
                            } else {
                                status = "Opening bank";
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
        g.drawString("Profit: " + fProfit + " (" + fProfitHr + "/HR)", 13, yOff += 15);
        g.drawString("Status: " + status, 13, yOff + 15);
    }
}
