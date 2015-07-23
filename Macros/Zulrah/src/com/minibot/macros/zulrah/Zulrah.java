package com.minibot.macros.zulrah;

import com.minibot.Minibot;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.zulrah.action.Food;
import com.minibot.macros.zulrah.action.Gear;
import com.minibot.macros.zulrah.action.Potions;
import com.minibot.macros.zulrah.action.Prayer;
import com.minibot.macros.zulrah.listener.ProjectileEvent;
import com.minibot.macros.zulrah.listener.ProjectileListener;
import com.minibot.macros.zulrah.listener.ZulrahEvent;
import com.minibot.macros.zulrah.listener.ZulrahListener;
import com.minibot.macros.zulrah.phase.Phase;
import com.minibot.macros.zulrah.phase.SnakeType;
import com.minibot.macros.zulrah.phase.Stage;
import com.minibot.macros.zulrah.util.Paint;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Tim Dekker
 * @since 7/14/15
 * <p>
 * TODO:
 * - Phase identification is good, need to check tile difference in ZulrahListener
 * - After a phase is complete there's a 'spraying' stage, and then it returns to initial.
 */
@Manifest(name = "Zulrah", author = "Tyler/Tim", version = "1.0.0", description = "Kills Zulrah")
public class Zulrah extends Macro implements Renderable {

    public static final int PROJECTILE_CLOUD = 1045;
    public static final int PROJECTILE_SPERM = 1047;
    public static final int PROJECTILE_SNAKELING = 1230;
    public static final int PROJECTILE_RANGE = 1044;
    public static final int PROJECTILE_MAGE = 1046;

    private static final ArrayList<Integer> previous = new ArrayList<>();
    private static Phase phase = Phase.PHASE_1;
    private static Tile origin = null;
    public static int projectileType = -1;
    private boolean changed = false;
    private ZulrahEvent lastEvent;
    private long lastRan = -1;

    private final ZulrahListener zulrahListener = new ZulrahListener() {
        public void onChange(ZulrahEvent event) {
            int stage = phase.advance();
            changed = true;
            lastEvent = event;
            System.out.println("Advancing on: " + event.previousId + " -> " + (event.id + "/" + event.npc.id()));
            System.out.println(" ^ " + event.previousTile + " -> " + event.tile);
            System.out.println(" ^ " + (stage - 1) + " -> " + stage);
        }
    };

    private final ProjectileListener projectileListener = new ProjectileListener() {
        public void onProjectileLoaded(ProjectileEvent evt) {
            if (evt.id == PROJECTILE_RANGE || evt.id == PROJECTILE_MAGE) {
                projectileType = evt.id;
            }
        }
    };

    @Override
    public void atStart() {
        zulrahListener.start();
        projectileListener.start();
    }

    private void handleStats() {
        Prayer.setZulrahPrayers();
        Potions.drink();
        Gear.equip();
        Food.eat();
    }

    @Override
    public void run() {
        Minibot.instance().setVerbose(false);
        Npc zulrah = getMonster();
        zulrahListener.setNpc(zulrah);
        handleStats();
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
                    Phase potential = Phase.determine(previous, lastEvent.id);//zulrah.id());
                    if (potential != null) {
                        phase = potential;
                        phase.setIndex(previous.size()); // do we actually need this?
                        phase.confirm();
                        System.out.println(phase.name() + " is quite dank (Confirmed)");
                    }
                }
                changed = false;
            }
            if (phase != null) {
                Stage current = phase.getCurrent();
                if (current != null) {
                    if (current.getSnakeType() != SnakeType.MELEE) {
                        lastRan = -1;
                    }
                    Tile currentTile = current.getTile();
                    if (currentTile != null && currentTile.equals(Players.local().location())) {
                        if (current.getSnakeType() == SnakeType.MELEE) {
                            int sum = zulrah.getOrientation() + Players.local().getOrientation();
                            if (sum == 1583 || sum == 2048) {
                                System.out.println("FUCKING RUN MARTY");
                                Tile dest;
                                if (current == Stage.MELEE_EAST) {
                                    dest = current.getTile().derive(2, -1);
                                } else {
                                    dest = current.getTile().derive(0, 2);
                                }
                                for (int i = 0; i < 2; i++) {
                                    Walking.walkTo(dest);
                                    Time.sleep(100, 200);
                                }
                                lastRan = Time.millis();
                            }
                        }
                        Character target = Players.local().target();
                        if (target == null || !target.name().equals("Zulrah")) {
                            if (lastRan == -1 || Time.millis() - lastRan > Random.nextInt(4200, 4600)) {
                                zulrah.processAction("Attack");
                                Time.sleep(100, 200);
                            }
                        }
                    } else {
                        if (lastRan == -1 || Time.millis() - lastRan > Random.nextInt(4200, 4600)) {
                            Walking.walkTo(current.getTile());
                            Time.sleep(100, 200);
                            handleStats();
                        }
                    }
                }
            }
        } else {
            if (origin != null && origin.distance() < 10) {
                System.out.println("LOOT THAT SHIT MARTY");
            } else {
                origin = null;
                Potions.reset();
                Phase.reset();
                previous.clear();
                // check if lumbridge/falador death spot, check message listener for dead or not, etc.
                // you need to go to a bank regardless
            }
        }
        Minibot.instance().client().resetMouseIdleTime();
    }

    @Override
    public void render(Graphics2D g) {
        Paint.paint(g);
        g.drawString(Players.local().getOrientation() + "", 300, 300);
    }

    public static ArrayList<Integer> getPrevious() {
        return previous;
    }

    public static Phase getPhase() {
        return phase;
    }

    public static Tile getOrigin() {
        return origin;
    }

    public static Npc getMonster() {
        return Npcs.nearestByName("Zulrah");
    }
}
