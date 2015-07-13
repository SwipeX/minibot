package com.minibot.macros.zulrah;

import com.minibot.api.action.tree.Action;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.macros.zulrah.util.MultiNameItemFilter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "Zulrah Killer", author = "Tyler/Jacob", version = "1.0.0", description = "Kills Zulrah")
public class ZulrahKiller extends Macro implements Renderable, ChatboxListener {

    private static final MultiNameItemFilter FOOD = new MultiNameItemFilter("Monkfish", "Shark", "Manta ray", "Sea turtle", "Tuna potato", "Dark crab");
    private static final MultiNameItemFilter VENOM = new MultiNameItemFilter("Anti-venom+");
    private static final MultiNameItemFilter PRAYER = new MultiNameItemFilter("Prayer potion", "Super restore");
    private static final MultiNameItemFilter TELEPORT = new MultiNameItemFilter("Zul-andra teleport");
    private static final MultiNameItemFilter CAMELOT = new MultiNameItemFilter("Camelot teleport");
    private static final MultiNameItemFilter RECOIL = new MultiNameItemFilter("Ring of recoil");

    private static final Tile BANK = new Tile(2726, 3491, 0);

    private static String status = "N/A";
    private static ZulrahMode mode;
    private static ZulrahDirection direction;
    private static boolean died;
    private static boolean collected;
    private static boolean reset;
    private static boolean ring;

    private static long venomTimer = -1;

    private static boolean setup() {
        return Inventory.containsAll(FOOD, VENOM, PRAYER, TELEPORT, CAMELOT, RECOIL);
    }

    private static boolean prepareInventory() {
        if (Inventory.full()) {
            return true;
        }
        if (Inventory.first(VENOM) == null) {
            Item venom = Bank.first(VENOM);
            if (venom != null) {
                venom.processAction("Withdraw-1");
                Time.sleep(200, 300);
            }
        }
        if (Inventory.first(PRAYER) == null) {
            Item prayer = Bank.first(PRAYER);
            if (prayer != null) {
                prayer.processAction("Withdraw-1");
                Time.sleep(200, 300);
                prayer.processAction("Withdraw-1");
                Time.sleep(200, 300);
            }
        }
        if (Inventory.first(TELEPORT) == null) {
            Item teleport = Bank.first(TELEPORT);
            if (teleport != null) {
                teleport.processAction("Withdraw-1");
                Time.sleep(200, 300);
                teleport.processAction("Withdraw-1");
                Time.sleep(200, 300);
            }
        }
        if (Inventory.first(CAMELOT) == null) {
            Item camelot = Bank.first(CAMELOT);
            if (camelot != null) {
                camelot.processAction("Withdraw-1");
                Time.sleep(200, 300);
                camelot.processAction("Withdraw-1");
                Time.sleep(200, 300);
            }
        }
        if (Inventory.first(RECOIL) == null) {
            Item recoil = Bank.first(RECOIL);
            if (recoil != null) {
                recoil.processAction("Withdraw-1");
                Time.sleep(200, 300);
                recoil.processAction("Withdraw-1");
                Time.sleep(200, 300);
            }
        }
        Item food = Bank.first(FOOD);
        if (food != null) {
            food.processAction("Withdraw-All");
            Time.sleep(200, 300);
        }
        return true;
    }

    private static boolean equipAll() { // test for recoil case
        AtomicReference<String> last = new AtomicReference<>("null");
        Inventory.apply(i -> {
            RSItemDefinition definition = i.definition();
            return definition != null && Action.indexOf(definition.getActions(), "Equip") >= 0 && !last.get().equals("Ring of recoil");
        }, i -> {
            i.processAction("Equip");
            last.set(i.name());
            Time.sleep(400, 700);
        });
        return true;
    }

    private static boolean teleportCamelot() {
        Item tab = Inventory.first(CAMELOT);
        if (tab != null) {
            tab.processAction("Break");
            return Time.sleep(() -> ZulrahEnvironment.findZulrah() == null, 10000);
        }
        return false;
    }

    private static boolean teleportZulrah() {
        Item scroll = Inventory.first(TELEPORT);
        if (scroll != null) {
            scroll.processAction("Teleport");
            return Time.sleep(() -> ZulrahEnvironment.findCollector() != null, 10000);
        }
        return false;
    }

    @Override
    public void run() {
        Npc zulrah = ZulrahEnvironment.findZulrah();
        if (Widgets.viewingContinue()) {
            Widgets.processContinue();
            Time.sleep(400, 600);
        }
        if (zulrah != null) {
            if (!reset) {
                ZulrahEnvironment.setSource(zulrah.location());
                died = false;
                collected = false;
                reset = true;
            }
            int id = zulrah.id();
            for (ZulrahMode zm : ZulrahMode.values()) {
                if (id == zm.id) {
                    mode = zm;
                    status = "Protect " + mode.toString().toLowerCase();
                    mode.activate();
                }
            }
            direction = ZulrahEnvironment.findZulrahDirection();
            if (venomTimer == -1 || venomTimer - Time.millis() <= 20000) { // handle anti-venom
                Item av = Inventory.first(VENOM);
                if (av != null) {
                    av.processAction("Drink");
                    venomTimer = Time.millis() + 300000;
                    Time.sleep(550, 750);
                }
            }
            // TODO: handle running to different location based on 'mode' here
            Player local = Players.local();
            if (local != null) {
                if (Game.levels()[Skills.PRAYER] < 10) { // handles prayer
                    Item pot = Inventory.first(PRAYER);
                    if (pot != null) {
                        pot.processAction("Drink");
                        Time.sleep(550, 750);
                    } else {
                        if (teleportCamelot()) {
                            Time.sleep(() -> ZulrahEnvironment.findZulrah() == null, 10000);
                        }
                    }
                }
                if (local.health() <= 41) { // handle eating
                    int eat = (local.maxHealth() - local.health()) / 20;
                    for (int i = 0; i < eat; i++) {
                        Item food = Inventory.firstFood();
                        if (food != null) {
                            food.processAction("Eat");
                            Time.sleep(550, 750);
                        } else {
                            if (teleportCamelot()) {
                                Time.sleep(() -> ZulrahEnvironment.findZulrah() == null, 10000);
                            }
                            break; // you're screwed, get out
                        }
                    }
                }
                if (ring) {
                    Item r = Inventory.first(RECOIL);
                    if (r != null) {
                        r.processAction("Equip");
                        Time.sleep(400, 600);
                        ring = false;
                    }
                }
                if (!local.interacting()) {
                    zulrah.processAction("Attack");
                    Time.sleep(300, 500);
                }
            }
        } else {
            mode = null;
            for (ZulrahMode mode : ZulrahMode.values()) {
                mode.deactivate();
            }
            if (ZulrahEnvironment.atCamp()) {
                if (died) {
                    if (!collected) {
                        status = "Collecting items";
                        collected = ZulrahEnvironment.collect();
                    } else {
                        if (equipAll()) {
                            teleportCamelot();
                            died = false;
                        }
                    }
                } else {
                    status = "Boarding boat";
                    ZulrahEnvironment.boardBoat();
                }
            } else {
                if (/*setup()*/ true) {
                    if (Bank.viewing()) {
                        status = "Closing bank";
                        Bank.close();
                    } else {
                        status = "Teleporting to Zulrah";
                        teleportZulrah();
                    }
                } else {
                    if (BANK.distance() > 5) {
                        status = "Traveling to bank";
                        Walking.walkTo(BANK);
                        Time.sleep(600, 800);
                    } else {
                        if (Bank.viewing()) {
                            status = "Preparing inventory";
                            prepareInventory();
                        } else {
                            status = "Opening bank";
                            Bank.openBooth();
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
        g.drawString("Status: " + status, 13, yOff += 15);
        g.drawString("Mode: " + (mode != null ? mode : "N/A"), 13, yOff += 15);
        g.drawString("Died: " + died, 13, yOff += 15);
        g.drawString("Direction: " + (direction != null ? direction : "N/A"), 13, yOff + 15);
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message.equals("Oh dear, you are dead!")) {
            died = true;
            reset = false;
        } else if (message.contains("recoil has been")) {
            ring = true;
        }
    }
}