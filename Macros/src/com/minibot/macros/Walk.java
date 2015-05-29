package com.minibot.macros;

import com.minibot.api.macro.Macro;
import com.minibot.api.macro.Manifest;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;

import java.awt.*;

/**
 * Created by tim on 5/28/15.
 */
@Manifest(name = "Walker", author = "Swipe", version = "1.0.0", description = "walks n shit")
public class Walk extends Macro implements Renderable {

    @Override
    public void run() {
        Walking.walkTo(Players.local().location().derive(1, 0));
        Time.sleep(5000);
    }

    @Override
    public void render(Graphics2D g) {

    }
}