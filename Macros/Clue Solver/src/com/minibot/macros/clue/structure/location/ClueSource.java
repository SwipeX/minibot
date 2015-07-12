package com.minibot.macros.clue.structure.location;

import com.minibot.api.method.*;
import com.minibot.api.method.web.WebPath;
import com.minibot.api.util.Time;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Path;
import com.minibot.api.wrapper.locatable.*;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.macros.clue.TeleportLocation;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
public class ClueSource {

    public final Area area;
    public final Tile bank, nearNpcs;
    public final TeleportLocation teleport;
    public final Filter<Npc> npcFilter;

    public ClueSource(Area area, Tile bank, Tile nearNpcs, TeleportLocation teleport, Filter<Npc> npcFilter) {
        this.area = area;
        this.bank = bank;
        this.nearNpcs = nearNpcs;
        this.teleport = teleport;
        this.npcFilter = npcFilter;
    }

    public void fetchClue(AtomicBoolean rewarded) {
        Player player = Players.local();
        if (player != null) {
            if (!area.contains(player)) {
                teleport.teleport();
            } else {
                if (rewarded.get()) {
                    if (bank.distance() > 10) {
                        WebPath.build(bank).step(Path.Option.TOGGLE_RUN);
                    } else {
                        if (!Bank.viewing()) {
                            Bank.openBooth();
                        } else {
                            // deposit teh shit
//                                rewarded = false;
                        }
                    }
                } else {
                    if (nearNpcs.distance() > 8) {
                        WebPath.build(nearNpcs).step(Path.Option.TOGGLE_RUN);
                    } else {
                        GroundItem groundClue = Ground.nearestByFilter(i -> {
                            String name = i.name();
                            return name != null && name.contains("Clue scroll");
                        });
                        if (groundClue != null) {
                            groundClue.take();
                        } else {
                            Character target = player.target();
                            if (target == null || target.dead()) {
                                Npc guard = Npcs.nearestByFilter(npcFilter);
                                if (guard != null) {
                                    guard.processAction("Attack");
                                    Time.sleep(() -> player.target() != null, 5000);
                                    Time.sleep(600, 800);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
