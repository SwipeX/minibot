package com.minibot.macros.clue;

import com.minibot.api.method.Bank;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Area;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.clue.structure.ClueScroll;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "Clue Solver", author = "Tyler, Jacob", version = "1.0.0", description = "Solves medium clues")
public class ClueSolver extends Macro {

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
                        if (FALADOR_GUARDS.distance() > 10) {
                            Walking.walkTo(FALADOR_GUARDS);
                            Time.sleep(600, 800);
                        } else {
                            if (player.target() == null) {
                                Npc guard = Npcs.nearestByFilter(n -> {
                                    String name = n.name();
                                    return name != null && name.equals("Guard") && n.level() < 22;
                                });
                                if (guard != null) {
                                    guard.processAction("Attack");
                                    Time.sleep(() -> player.target() != null, 5000);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
