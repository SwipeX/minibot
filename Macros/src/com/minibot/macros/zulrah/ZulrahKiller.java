package com.minibot.macros.zulrah;

import com.minibot.api.method.ChatboxListener;
import com.minibot.api.method.Players;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "Zulrah Killer", author = "Tyler, Jacob", version = "1.0.0", description = "Kills Zulrah")
public class ZulrahKiller extends Macro implements Renderable, ChatboxListener {

    private String status = "N/A";
    private ZulrahMode mode;
    private boolean died = false;
    private boolean collected = false;
    private boolean reset = false;

    @Override
    public void run() {
        Npc zulrah = ZulrahEnvironment.findZulrah();
        if (Widgets.viewingContinue()) {
            Widgets.processContinue();
            Time.sleep(400, 600);
        }
        if (zulrah != null) {
            if (!reset) {
                died = false;
                collected = false;
                reset = true;
            }
            int id = zulrah.id();
            for (ZulrahMode zm : ZulrahMode.values()) {
                if (id == zm.id) {
                    mode = zm;
                    status = "Protect " + mode.toString().toLowerCase();
                    mode.activate();
                }
            }
            // handle anti-venom here
            // handle eating here
            // handle running to different location based on 'mode' here
            Player local = Players.local();
            if (local != null) {
                if (!local.interacting())
                    zulrah.attack(); // could also handle switches prior based on 'mode'
            }
        } else {
            mode = null;
            for (ZulrahMode mode : ZulrahMode.values())
                mode.deactivate();
            if (ZulrahEnvironment.atCamp()) {
                if (died) {
                    if (!collected) {
                        status = "Collecting items";
                        collected = ZulrahEnvironment.collect();
                    } else {
                        // teleport to bank
                        // died = false;
                    }
                } else {
                    status = "Boarding boat";
                    ZulrahEnvironment.boardBoat();
                }
            } else {
//                if (setup()) {
//                    status = "Teleporting to Zulrah";
//                    // teleport to zulrah
//                } else {
//                    if (BANK.distance() > 5) {
//                        status = "Traveling to bank";
//                        // travel to bank
//                    } else {
//                        if (Bank.viewing()) {
//                            status = "Preparing inventory";
//                            // prepareInventory
//                        } else {
//                            status = "Opening bank";
//                            Bank.openBooth();
//                        }
//                    }
//                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        g.drawString("Status: " + status, 13, yOff += 15);
        g.drawString("Mode: " + (mode != null ? mode : "N/A"), 13, yOff += 15);
        g.drawString("Died: " + died, 13, yOff + 15);
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message.equals("Oh dear, you are dead!")) {
            died = true;
            reset = false;
        }
    }
}
