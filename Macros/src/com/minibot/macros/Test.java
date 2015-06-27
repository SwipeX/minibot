package com.minibot.macros;

import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test extends Macro implements Renderable {

    private Tile tile;

    @Override
    public void run() {
        Player local = Players.local();
        if (local != null) {
            if (tile == null || !local.location().equals(tile)) {
                tile = local.location();
                StringSelection stringSelection = new StringSelection("new Tile(" + tile.x() + ", " + tile.y() + ", " + tile.plane() + ")");
                Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
                clpbrd.setContents(stringSelection, null);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
    }
}