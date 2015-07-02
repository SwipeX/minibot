package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.SpellButtonAction;
import com.minibot.api.action.tree.TableAction;
import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Skills;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/29/2015
 */
@Manifest(name = "HighAlch", author = "Tyler", version = "1.0.0", description = "High alchs")
public class HighAlch extends Macro implements Renderable {

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private static final int EXP_EACH = 65;

    private static final Filter<Item> NATURE_FILTER = (i -> {
        String name = i.name();
        return name != null && name.equals("Nature rune");
    });

    private static final Filter<Item> OTHER_FILTER = (i -> !NATURE_FILTER.accept(i));

    private int startExp;

    @Override
    public void atStart() {
        startExp = Game.experiences()[Skills.MAGIC];
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Item runes = Inventory.first(NATURE_FILTER);
        Item other = Inventory.first(OTHER_FILTER);
        if (runes != null && other != null) {
            RuneScape.processAction(new SpellButtonAction(14286883));
            RuneScape.processAction(new TableAction(ActionOpcodes.SPELL_ON_ITEM, other.id(), other.index(), 9764864));
            Time.sleep(500, 700);
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int gained = Game.experiences()[Skills.MAGIC] - startExp;
        int casts = (gained / EXP_EACH);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fCrafted = ValueFormat.format(casts, COMMA_FORMAT);
        String fCraftedHr = ValueFormat.format(hourly(casts), COMMA_FORMAT);
        g.drawString("Casts: " + fCrafted + " (" + fCraftedHr + "/HR)", 13, yOff += 15);
        String fExp = ValueFormat.format(gained, COMMA_FORMAT);
        String fExpHr = ValueFormat.format(hourly(gained), THOUSAND_FORMAT);
        g.drawString("Experience: " + fExp + " (" + fExpHr + "/HR)", 13, yOff + 15);
    }
}
