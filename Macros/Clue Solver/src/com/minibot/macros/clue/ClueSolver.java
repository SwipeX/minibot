package com.minibot.macros.clue;

import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.macros.clue.structure.ClueScroll;
import com.minibot.macros.clue.structure.location.ClueMediumSource;
import com.minibot.macros.clue.structure.location.ClueSource;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "Clue Solver", author = "Tyler, Jacob", version = "1.0.0", description = "Solves clues")
public class ClueSolver extends Macro implements Renderable {

    private static final ClueSource SOURCE = new ClueMediumSource();

    private AtomicBoolean rewarded = new AtomicBoolean(false);
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
            // check if rewards iface is valid and rewarded.set(true);
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
            SOURCE.fetchClue(rewarded);
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
