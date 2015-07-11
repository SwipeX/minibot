package com.minibot.macros;

import com.minibot.api.method.Game;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test extends Macro implements Renderable {

    private Tile tile;

    private static boolean level() {
        WidgetComponent component = Widgets.get(233, 2);
        return component != null && component.visible();
    }

    @Override
    public void atStart() {
        //if (!Game.playing()) {
        //  interrupt();
        //}
    }

    @Override
    public void run() {
//        Player local = Players.local();
//        if (local != null) {
//            if (tile == null || !local.location().equals(tile)) {
//                tile = local.location();
//                StringSelection stringSelection = new StringSelection("new Tile(" + tile.x() + ", " + tile.y() + ", " + tile.plane() + ")");
//                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//                clipboard.setContents(stringSelection, null);
//            }
//        }
//        WidgetComponent c = Widgets.byText(s -> s != null && s.contains("RuneScape has been updated!"));
//        if (c != null) {
//            System.out.println(c.owner().index() + "," + c.index() + "," + c.text() + "," + c.visible());
//        }
        GameObject cave = Objects.nearestByName("Cave entrance");
        if (cave != null) {
            cave.processAction("Enter");
            if (Time.sleep(Widgets::viewingContinue, 2500)) {
                Widgets.processContinue();
                if (Time.sleep(Widgets::viewingDialog, 2500)) {
                    Widgets.processDialogOption(0);
                }
            }
        }
        Time.sleep(5000);
    }

    @Override
    public void render(Graphics2D g) {
        g.drawString("Test " + Game.plane(), 50, 50);
        g.drawString(Players.local().location().toString(), 50, 75);
    }
}