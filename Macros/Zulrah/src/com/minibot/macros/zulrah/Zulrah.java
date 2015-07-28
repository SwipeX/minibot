package com.minibot.macros.zulrah;

import com.minibot.Minibot;
import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.zulrah.action.*;
import com.minibot.macros.zulrah.listener.ProjectileEvent;
import com.minibot.macros.zulrah.listener.ProjectileListener;
import com.minibot.macros.zulrah.listener.ZulrahEvent;
import com.minibot.macros.zulrah.listener.ZulrahListener;
import com.minibot.macros.zulrah.phase.Phase;
import com.minibot.macros.zulrah.phase.SnakeType;
import com.minibot.macros.zulrah.phase.Stage;
import com.minibot.macros.zulrah.util.Paint;
import com.minibot.macros.zulrah.util.Price;

import java.awt.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author Tim Dekker
 * @since 7/14/15
 * <p>
 * TODO:
 * - fix bug where eating walks
 * - death walk
 * - prayer disables super fast and gets hit once (range stage)
 * - better tiles for getting away from venom clouds
 */
@Manifest(name = "Zulrah", author = "Tyler/Tim/Jacob", version = "1.0.0", description = "Kills Zulrah")
public class Zulrah extends Macro implements Renderable, ChatboxListener {

    public static final int PROJECTILE_CLOUD = 1045;
    public static final int PROJECTILE_SPERM = 1047;
    public static final int PROJECTILE_SNAKELING = 1230;
    public static final int PROJECTILE_RANGED = 1044;
    public static final int PROJECTILE_MAGE = 1046;
    public static final int ZUL_TELEPORT = 12938;
    public static final int AVAS_ACCUMULATOR = 10499;

    public static final List<Integer> lootIds = new ArrayList<>(45);
    private static final List<Integer> previous = new ArrayList<>();
    private static Phase phase = Phase.PHASE_1;
    private static Tile origin, dodge;
    public static int projectileType = -1;
    private static boolean changed, walkedOrigin, dead;
    private static ZulrahEvent lastEvent;
    private static long lastRan = -1, lastAttack = -1;
    private static int total;
    private static int kills, deaths;

    private final ZulrahListener zulrahListener = new ZulrahListener() {
        public void onChange(ZulrahEvent event) {
            if (!dead) {
                int stage = phase.advance();
                changed = true;
                lastEvent = event;
                System.out.println("Advancing on: " + event.previousId + " -> " + (event.id + "/" + event.npc.id()));
                System.out.println(" ^ " + event.previousTile + " -> " + event.tile);
                System.out.println(" ^ " + (stage - 1) + " -> " + stage);
            }
        }
    };

    private final ProjectileListener projectileListener = new ProjectileListener() {
        public void onProjectileLoaded(ProjectileEvent evt) {
            if (evt.id == PROJECTILE_RANGED || evt.id == PROJECTILE_MAGE) {
                projectileType = evt.id;
            }
        }
    };


    @Override
    public void atStart() {
        zulrahListener.start();
        projectileListener.start();
        Gear.setup();
    }

    private void handleStats() {
        Prayer.setZulrahPrayers();
        Potions.drink();
        Gear.equip();
        Food.eat();
    }

    private void handleSetup() {
        Camp.act();
        Teleport.handle();
        ClanWars.handle();
    }

    private boolean attack(Npc zulrah) {
        Character target = Players.local().target();
        if (target == null || !target.name().equals("Zulrah")) {
            if ((lastRan == -1 || Time.millis() - lastRan > Random.nextInt(800, 1200)) &&
                    Time.millis() - lastAttack > Random.nextInt(300, 350)) {
                zulrah.processAction("Attack");
                Time.sleep(100, 200);
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        //Minibot.instance().setVerbose(false);
        if (dead) {
            DeathWalk.handle();
            return;
        }
        Npc zulrah = monster();
        zulrahListener.setNpc(zulrah);
        if (origin != null) {
            if (!walkedOrigin) {
                Tile initial = Stage.INITIAL.getTile();
                assert initial != null;
                Walking.walkTo(initial);
                walkedOrigin = initial.distance() < 3;
                Time.sleep(100, 200);
                if (!walkedOrigin) {
                    return;
                }
            } else {
                handleStats();
            }
        }
        handleSetup();
        handleDialogs();
        if (zulrah != null) {
            if (origin == null) {
                SnakeType.RANGE.setId(zulrah.id());
                SnakeType.MELEE.setId(zulrah.id() + 1);
                SnakeType.MAGIC.setId(zulrah.id() + 2);
                origin = zulrah.location();
            }
            if (changed) {
                if (phase.index() > 0) {
                    previous.add(lastEvent.previousId);
                }
                if (!phase.isConfirmed()) {
                    Phase potential = Phase.determine(previous, lastEvent.id); //zulrah.id());
                    if (potential != null) {
                        phase = potential;
                        phase.setIndex(previous.size());
                        phase.confirm();
                        System.out.println(phase.name() + " is quite dank (Confirmed)");
                    }
                }
                changed = false;
            }
            if (phase != null) {
                Stage current = phase.current();
                if (current != null) {
                    if (current.getSnakeType() != SnakeType.MELEE) {
                        lastRan = -1;
                        dodge = null;
                    }
                    Tile currentTile = current.getTile();
                    if (currentTile != null && currentTile.equals(Players.local().location())) {
                        if (current.getSnakeType() == SnakeType.MELEE) {
                            int sum = zulrah.getOrientation() + Players.local().getOrientation();
                            if (sum == 1583 || sum == 2048) {
                                System.out.println("FUCKING RUN MARTY");
                                if (current == Stage.MELEE_EAST) {
                                    dodge = current.getTile().derive(2, -1);
                                } else {
                                    dodge = current.getTile().derive(0, 2);
                                }
                                for (int i = 0; i < 2; i++) {
                                    Walking.walkTo(dodge);
                                    Time.sleep(100, 200);
                                }
                                lastRan = Time.millis();
                            }
                        }
                        if (attack(zulrah)) {
                            lastAttack = Time.millis();
                        }
                    } else {
                        if (lastRan == -1 || Time.millis() - lastRan > Random.nextInt(4200, 4600)) {
                            Walking.walkTo(current.getTile());
                            Time.sleep(100, 200);
                            handleStats();
                        } else if (current.getSnakeType() == SnakeType.MELEE && dodge != null &&
                                dodge.exactDistance() < 1D) {
                            attack(zulrah);
                        }
                    }
                }
            }
        } else {
            if (origin != null && origin.distance() < 10) {
                Deque<GroundItem> items = Ground.loaded(20);
                if (!items.isEmpty()) {
                    for (GroundItem item : items) {
                        if (item.id() != ZUL_TELEPORT && !lootIds.contains(item.id())) {
                            lootIds.add(item.id());
                        }
                        int price = Price.getPrice(item.id());
                        System.out.println(item.name() + " (" + item.stackSize() + ") x " + price + " = " + (price * item.stackSize()));
                        total += (item.stackSize() * price);
                    }
                    System.out.println("Total kill estimated @ " + total + " gp");
                    kills++;
                    items.forEach(GroundItem::take);
                }
            } else {
                fullyReset();
                // check if lumbridge/falador death spot, check message listener for dead or not, etc.
                // you need to go to a bank regardless
            }
        }
        Minibot.instance().client().resetMouseIdleTime();
    }

    private void handleDialogs() {
        if (Widgets.viewingContinue()) {
            Widgets.processContinue();
            Time.sleep(400, 600);
        }
    }

    @Override
    public void render(Graphics2D g) {
        Paint.debug(g);
        Paint.paint(this, g);
    }

    public static List<Integer> previous() {
        return previous;
    }

    public static Phase phase() {
        return phase;
    }

    public static Tile origin() {
        return origin;
    }

    public static Npc monster() {
        return Npcs.nearestByName("Zulrah");
    }

    public static void resetPhase() {
        phase = Phase.PHASE_1;
    }

    public static void fullyReset() {
        origin = null;
        walkedOrigin = false;
        dodge = null;
        Potions.reset();
        Phase.reset();
        previous.clear();
        resetPhase();
        Prayer.deactivateAll();
    }

    public int total() {
        return total;
    }

    public static boolean isDead() {
        return dead;
    }

    public static void setDead(boolean dead) {
        Zulrah.dead = dead;
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message != null && message.equals("Oh dear, you are dead!")) {
            dead = true;
            deaths++;
        }
    }

    public static int kills() {
        return kills;
    }

    public static int deaths() {
        return deaths;
    }
}