package com.minibot.macros.clue;

import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.*;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.clue.structure.ClueScroll;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "Clue Solver", author = "Tyler, Jacob", version = "1.0.0", description = "Solves medium clues")
public class ClueSolver extends Macro implements Renderable {

    private static final Area FALADOR = new Area(new Tile(2938, 3357, 0), new Tile(2979, 3404, 0));
    private static final Tile FALADOR_GUARDS = new Tile(2966, 3393, 0);
    private static final Tile FALADOR_BANK = new Tile(2946, 3369, 0);

    private boolean rewarded = false;
    private int clueId = -1;
    private ClueScroll scroll;
    private AtomicReference<String> status = new AtomicReference<>();

    @Override
    public void atStart() {
        ClueScroll.populateMedium();
    }

    @Override
    public void run() {
        if (Widgets.viewingContinue()) {
            Widgets.processContinue();
            Time.sleep(600, 800);
            return;
        }
        Item casket = Inventory.first(i -> {
            String name = i.name();
            return name != null && name.contains("Casket");
        });
        if (casket != null) {
            int stackCount = Inventory.stackCount();
            casket.processAction("Open");
            Time.sleep(() -> Inventory.stackCount() != stackCount, 5000);
            return;
        }
        Item clueItem = ClueScroll.findInventoryItem();
        if (clueItem != null) {
            clueId = clueItem.id();
            scroll = ClueScroll.find(clueId);
            if (scroll != null) {
                scroll.solve(status);
            }
        } else {
            if (scroll != null) {
                scroll.reset();
                scroll = null;
            }
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

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int yOff = 11;
        g.drawString("Clue: " + (clueId != -1 ? clueId : "N/A"), 13, yOff += 15);
        String status = this.status.get();
        g.drawString("Status: " + (status != null ? status : "N/A"), 13, yOff + 15);
    }
}
