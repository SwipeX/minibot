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
 * - Magic prayer is being cunt.
 * - Prayer is not being toggled while running.
 * - Melee dodging is not added.
 * - Jad stage is being cuntwheel because magic prayer is being cunt.
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
            changed = true;
            lastEvent = event;
            phase.advance();
            System.out.println("Advancing on: " + event.previousId + " -> " + event.id);
            System.out.println(" ^ " + event.previousTile + " -> " + event.tile);
        }
    };

    @Override
    public void atStart() {
        listener.start();
    }

    @Override
    public void run() {
        Npc zulrah = getMonster();
        Prayer.setZulrahPrayers();
        Potions.drink();
        Gear.equip();
        Food.eat();
        if (zulrah != null) {
            listener.setNpc(zulrah);
            if (origin == null) {
                SnakeType.RANGE.setId(zulrah.id());
                SnakeType.MELEE.setId(zulrah.id() + 1);
                SnakeType.MAGIC.setId(zulrah.id() + 2);
                origin = zulrah.location();
            }
            if (lastAnim == -1 && zulrah.animation() != -1) {
                attackCounter++;
            }
            if (changed) {
                attackCounter = 0;
                previous.add(lastEvent.previousId);
                if (!phase.isConfirmed()) {
                    Phase potential = Phase.determine(previous, zulrah.id());
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
                Stage current = phase.getCurrent();
                if (current != null) {
                    Tile currentTile = current.getTile();
                    if (currentTile != null && currentTile.equals(Players.local().location())) {
                        if (current.getSnakeType() == SnakeType.MELEE) {
                            int sum = zulrah.getOrientation() + Players.local().getOrientation();
                            if (Math.abs(2048 - sum) <= 80) {
                                System.out.println("FUCKING RUN MARTY");
                                Tile dest;
                                if (current == Stage.MELEE_EAST) {
                                    dest = current.getTile().derive(-2, 1);
                                } else {
                                    dest = current.getTile().derive(0, 2);
                                }
                                Walking.walkTo(dest);
                                Time.sleep(3500, 4000);
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
                        Prayer.setZulrahPrayers();
                    }
                }
            }
            lastAnim = zulrah.animation();
        } else {
            //it could be loot time!
            //or we might need to get to zulrah first
            //do we need to bank? before we do, call Potions.reset!
            //are we dead?
            //i am batman
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
