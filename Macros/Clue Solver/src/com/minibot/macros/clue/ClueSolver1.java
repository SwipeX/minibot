package com.minibot.macros.clue;

import com.minibot.api.method.*;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.*;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.clue.structure.ClueScroll;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "Clue Solver", author = "Tyler, Jacob", version = "1.0.0", description = "Solves medium clues")
public class ClueSolver1 extends Macro {

    private static final Area FALADOR = new Area(new Tile(2938, 3357, 0), new Tile(2979, 3404, 0));
    private static final Tile FALADOR_GUARDS = new Tile(2966, 3393, 0);
    private static final Tile FALADOR_BANK = new Tile(2946, 3369, 0);

    private boolean rewarded = false;

    @Override
    public void run() {
        Item clueItem = ClueScroll.findInventoryItem();
        if (clueItem != null) {
            // solve
        } else {
            Player player = Players.local();
            if (player != null) {
                if (!FALADOR.contains(player)) {
                    TeleportLocation.FALADOR.teleport();
                } else {
                    if (rewarded) {
                        if (FALADOR_BANK.distance() > 10) {
                            Walking.walkTo(FALADOR_BANK);
                            Time.sleep(600, 800);
                        } else {
                            if (!Bank.viewing()) {
                                Bank.openBooth();
                            } else {
                                // deposit teh shit
//                                rewarded = false;
                            }
                        }
                    } else {
                        if (FALADOR_GUARDS.distance() > 8) {
                            Walking.walkTo(FALADOR_GUARDS);
                            Time.sleep(600, 800);
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
                                    Npc guard = Npcs.nearestByFilter(n -> {
                                        if (n.dead())
                                            return false;
                                        String name = n.name();
                                        return name != null && name.equals("Guard") && n.level() < 22 &&
                                                n.distance(FALADOR_GUARDS) < 8;
                                    });
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
}
