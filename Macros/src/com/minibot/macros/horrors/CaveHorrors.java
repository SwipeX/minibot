package com.minibot.macros.horrors;

import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.bot.macro.Macro;
import com.minibot.macros.horrors.util.Lootables;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 7/10/2015
 */
public class CaveHorrors extends Macro implements Renderable {

    private int profit = 0;

    @Override
    public void atStart() {
        Lootables.initRareDropTable();
        Lootables.initCaveHorrors();
    }

    @Override
    public void run() {
        int loot = Lootables.loot();
        if (loot != -1) {
            profit += loot;
        } else {

        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fProfit = ValueFormat.format(profit, ValueFormat.COMMAS);
        String fProfitHr = ValueFormat.format(hourly(profit), ValueFormat.COMMAS);
        g.drawString("Profit: " + fProfit + " (" + fProfitHr + "/HR)", 13, yOff);
    }
}
