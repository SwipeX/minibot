package com.minibot.macros.zulrah;

import com.minibot.Minibot;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.method.Walking;
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
import com.minibot.macros.zulrah.phase.Phase;
import com.minibot.macros.zulrah.phase.SnakeType;
import com.minibot.macros.zulrah.phase.Stage;
import com.minibot.macros.zulrah.util.Debug;
import com.minibot.macros.zulrah.util.ZulrahEvent;
import com.minibot.macros.zulrah.util.ZulrahListener;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Tim Dekker
 * @since 7/14/15
 * <p>
 * TODO:
 * - Jad phase is fairly good, but is what's messing us up most.
 * - Melee phase could use a BIT of work dodging.
 * - Phase detection for phase 1 confirmed is DEFINITELY wrong.
 *     - http://i.imgur.com/1jNazRS.png
 *     - http://i.imgur.com/g0E7lMf.png
 *     - https://gist.github.com/c9aba9a83c2c5bfe4cf5
 */
@Manifest(name = "Zulrah", author = "Tyler/Tim", version = "1.0.0", description = "Kills Zulrah")
public class Zulrah extends Macro implements Renderable {

    private static ArrayList<Integer> previous = new ArrayList<>();
    private static Phase phase = Phase.PHASE_1;
    private static Tile origin = null;
    public static int attackCounter = 0;
    private boolean changed = false;
    private ZulrahEvent lastEvent;
    private int lastAnim = -1;

    private final ZulrahListener listener = new ZulrahListener() {
        public void onChange(ZulrahEvent event) {
            int stage = phase.advance();
            changed = (stage != 0);
            lastEvent = event;
            if (!changed) {
                previous.clear();
            }
            System.out.println("Advancing on: " + event.previousId + " -> " + (event.id + "/" + event.npc.id()));
            System.out.println(" ^ " + event.previousTile + " -> " + event.tile);
            if (stage == 0) {
                System.out.println(" ^ reset");
            } else {
                System.out.println(" ^ " + (stage - 1) + " -> " + stage);
            }
        }
    };

    @Override
    public void atStart() {
        listener.start();
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
        listener.setNpc(zulrah);
        handleStats();
        if (zulrah != null) {
            if (origin == null) {
                SnakeType.RANGE.setId(zulrah.id());
                SnakeType.MELEE.setId(zulrah.id() + 1);
                SnakeType.MAGIC.setId(zulrah.id() + 2);
                origin = zulrah.location();
            }
            if (lastAnim != -1 && zulrah.animation() == -1) {//grab him when he is idling instead of attacking
                attackCounter++;
            }
            if (changed) {
                attackCounter = 0;
                previous.add(lastEvent.previousId);
                if (!phase.isConfirmed()) {
                    Phase potential = Phase.determine(previous, zulrah.id());
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
                                Walking.walkTo(dest);
                                Time.sleep(5250, 5500);
                            }
                        }
                        Character target = Players.local().target();
                        if (target == null || !target.name().equals("Zulrah")) {
                            zulrah.processAction("Attack");
                            Time.sleep(100, 200);
                        }
                    } else {
                        Walking.walkTo(current.getTile());
                        Time.sleep(100, 200);
                        handleStats();
                    }
                }
            }
            lastAnim = zulrah.animation();
        } else {
            if (origin != null && origin.distance() < 10) {
                System.out.println("LOOT THAT SHIT MARTY");
            } else {
                origin = null;
                Potions.reset();
                Phase.reset();
                // check if lumbridge/falador death spot, check message listener for dead or not, etc.
                // you need to go to a bank regardless
            }
        }
        Minibot.instance().client().resetMouseIdleTime();
    }

    @Override
    public void render(Graphics2D g) {
        Debug.paint(g);
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
