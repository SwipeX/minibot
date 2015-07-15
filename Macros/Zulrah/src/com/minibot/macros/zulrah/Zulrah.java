package com.minibot.macros.zulrah;

import com.minibot.Minibot;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Renderable;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.zulrah.boss.Phase;
import com.minibot.macros.zulrah.boss.Stage;
import com.minibot.macros.zulrah.util.*;

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
    private static Phase phase;

    @Override
    public void run() {
        Npc zulrah = getMonster();
        Prayer.setPrayers();
        Potions.drink();
        Gear.equip();
        Food.eat();
        if (zulrah != null) {
            if (capture.getPreviousId() != zulrah.id() ||
                    capture.getPreviousLocation() != zulrah.location()) {
                System.out.println(String.format("Boss changed %s %s -> %s %s", capture.getPreviousId(),
                        capture.getPreviousLocation(), zulrah.id(), zulrah.location()));
                previous.add(capture.getPreviousId());
                if (phase == null) {
                    //check if we know phase yet (should know by stage 2)
                }
            }
            if (phase != null) {
                Stage current = null; //obtain current stage
                if (current != null) {
                    if (current.getTile().equals(Players.local().location())) {
                        //cool, we can attack (if we aren't)
                    } else {
                        Walking.walkTo(current.getTile());
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
        Minibot.instance().client().resetMouseIdleTime();
        capture.capture(zulrah);//if either id or location change, is new stage -> callback
    }

    @Override
    public void render(Graphics2D g) {

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

    public static Npc getMonster() {
        return Npcs.nearestByName("Zulrah");
    }
}
