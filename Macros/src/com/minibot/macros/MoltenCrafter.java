package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.tree.InputButtonAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/29/2015
 */
@Manifest(name = "MoltenCrafter", author = "Tyler", version = "1.0.0", description = "Crafts molten")
public class MoltenCrafter extends Macro implements Renderable, ChatboxListener {

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private static final Filter<Item> MOLTEN_FILTER = (i -> {
        String name = i.name();
        return name != null && name.equals("Molten glass");
    });

    private static final Filter<Item> PIPE_FILTER = (i -> {
        String name = i.name();
        return name != null && name.equals("Glassblowing pipe");
    });

    private static final Filter<Item> OTHER_FILTER = (i -> !MOLTEN_FILTER.accept(i) && !PIPE_FILTER.accept(i));

    private int startExp;
    private int crafted = 0;

    @Override
    public void atStart() {
        startExp = Game.experiences()[Skills.CRAFTING];
    }

    private boolean openBank() {
        Npc banker = Npcs.nearestByName("Banker");
        if (banker != null) {
            banker.processAction("Bank");
            return Time.sleep(Bank::viewing, 10000);
        }
        return false;
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Item molten = Inventory.first(MOLTEN_FILTER);
        if (molten != null) {
            if (Bank.viewing()) {
                Bank.close();
            } else {
                Item pipe = Inventory.first(PIPE_FILTER);
                if (pipe != null) {
                    pipe.use(molten);
                    Time.sleep(600, 800);
                    RuneScape.processAction(new InputButtonAction(35520642));
                    if (Time.sleep(() -> Players.local().animation() != -1, 5000)) {
                        int timeout = 17000;
                        long start = Time.millis();
                        Time.sleep(() -> Inventory.first(MOLTEN_FILTER) == null || Time.millis() - start >= timeout,
                                timeout);
                    }
                }
            }
        } else {
            if (!Bank.viewing()) {
                openBank();
            } else {
                Item item = Inventory.first(OTHER_FILTER);
                if (item != null) {
                    item.processAction("Deposit-All");
                    Time.sleep(300, 400);
                }
                Item bankMolten = Bank.first(MOLTEN_FILTER);
                if (bankMolten != null) {
                    bankMolten.processAction("Withdraw-All");
                    Bank.close();
                    Time.sleep(() -> Inventory.first(MOLTEN_FILTER) != null, 5000);
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int gained = Game.experiences()[Skills.CRAFTING] - startExp;
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fCrafted = ValueFormat.format(crafted, COMMA_FORMAT);
        String fCraftedHr = ValueFormat.format(hourly(crafted), COMMA_FORMAT);
        g.drawString("Crafted: " + fCrafted + " (" + fCraftedHr + "/HR)", 13, yOff += 15);
        String fExp = ValueFormat.format(gained, COMMA_FORMAT);
        String fExpHr = ValueFormat.format(hourly(gained), THOUSAND_FORMAT);
        g.drawString("Experience: " + fExp + " (" + fExpHr + "/HR)", 13, yOff + 15);
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message.contains("You make"))
            crafted++;
    }
}
