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
import com.minibot.macros.zulrah.util.Capture;
import com.minibot.macros.zulrah.util.Debug;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
@Manifest(name = "Zulrah", author = "Tyler/Tim", version = "1.0.0", description = "Kills Zulrah")
public class Zulrah extends Macro implements Renderable {
    private static Capture capture = new Capture();
    private static ArrayList<Integer> previous = new ArrayList<>();
    private static Phase phase = Phase.PHASE_1;
    private static Tile origin = null;
    public static int attackCounter = 0;
    private static final int ATTACK = 5069; //no melee, but idc

    @Override
    public void run() {
        Npc zulrah = getMonster();
        //maybe these could be grouped into Task(ish) framework
        Prayer.setPrayers();
        Potions.drink();
        Gear.equip();
        Food.eat();

        //main logic
        if (zulrah != null) {
            if (origin == null) {
                SnakeType.RANGE.setId(zulrah.id());
                SnakeType.MELEE.setId(zulrah.id() + 1);
                SnakeType.MAGIC.setId(zulrah.id() + 2);
                origin = zulrah.location();
            }
            if (capture.getPreviousAnimation() == -1 && zulrah.animation() == ATTACK) {
                attackCounter++;
            }
            if (capture.getPreviousId() != zulrah.id() ||
                    !capture.getPreviousLocation().equals(zulrah.location())) {
                System.out.println(String.format("Boss changed %s %s -> %s %s", capture.getPreviousId(),
                        capture.getPreviousLocation(), zulrah.id(), zulrah.location()));
                attackCounter = 0;
                if (capture.getPreviousId() != -1) {
                    previous.add(capture.getPreviousId());
                }
                if (!phase.isConfirmed()) {
                    Phase potential = Phase.determine(previous, zulrah.id());
                    if (potential != null) {
                        phase = potential;
                        phase.setIndex(previous.size());
                        phase.confirm();
                        System.out.println(phase.name() + " is quite dank (Confirmed)");
                    } else {
                        if (capture.getPreviousId() != -1)
                            phase.advance();
                    }
                } else {
                    phase.advance();
                    if (phase.getCurrent() != Stage.JAD_EAST && phase.getCurrent() != Stage.JAD_WEST) {
                        if (phase.getCurrent().getSnakeType().id() != zulrah.id()) {
                            previous.remove(previous.size() - 1);
                            phase.unconfirm();
                            System.out.println("incorrect advancement of phase / broken phase");
                        }
                    }
                }
            }
            if (phase != null) {
                Stage current = phase.getCurrent();
                if (current != null) {
                    if (current.getTile().equals(Players.local().location())) {
                        if (phase.getCurrent().getSnakeType() == SnakeType.MELEE) {
                            if (zulrah.getOrientation() == 1281) {
                                System.out.println("FUCKING RUN MARTY");
                            }
                        }
                        Character target = Players.local().target();
                        if (target == null || !target.name().equals("Zulrah")) {
                            zulrah.processAction("Attack");
                            Time.sleep(400, 600);
                        }
                    } else {
                        // System.out.println("Walking to " + current.getTile());
                        Walking.walkTo(current.getTile());
                        Time.sleep(1400, 1600);
                        //sleep here or it'll spam walk
                        //shit run to dat tile
                    }
                }
            }
        } else {
            //it could be loot time!
            //or we might need to get to zulrah first
            //do we need to bank? before we do, call Potions.reset!
            //are we dead?
            //i am batman
        }
        //setup next loop
        Minibot.instance().client().resetMouseIdleTime();
        capture.capture(zulrah);//if either id or location change, is new stage -> callback
    }

    @Override
    public void render(Graphics2D g) {
        Debug.paint(g);
        g.drawString(Players.local().getOrientation() + "", 300, 300);
    }

    public static Capture getCapture() {
        return capture;
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
