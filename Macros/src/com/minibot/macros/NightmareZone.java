package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Players;
import com.minibot.api.method.Skills;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Tim
 * @since 5/17/15.
 */
@Manifest(name = "NightmareZone", author = "Swipe", version = "1.0.0", description = "Absorption NMZ Player")
public class NightmareZone extends Macro implements Renderable {

    private boolean absorb;
    private long lastFlick = -1;
    private int startExp;

    private Thread prayerThread;

    @Override
    public void atStart() {
        if (Players.local() == null) {
            interrupt();
        }
        startExp = Game.experiences()[Skills.STRENGTH];
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (prayerThread == null) {
            prayerThread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        if (Game.levels()[Skills.PRAYER] <= 0)
                            return;
                        Item abs = Inventory.first(item -> item != null && item.name() != null && item.name().contains("verload"));
                        if (abs == null) System.exit(1);
                        Item j = Inventory.first(item -> item != null && item.name() != null && item.name().contains("cake"));
                        if (j != null) {
                            if (j.index() == 27) {
                                WidgetComponent protect = Widgets.get(271, 18);
                                if (protect != null) {
                                    protect.processAction(ActionOpcodes.WIDGET_ACTION, 1, "Activate", "<col=ff9040>Protect from Melee");
                                    Time.sleep(570, 580);
                                    protect.processAction(ActionOpcodes.WIDGET_ACTION, 1, "Deactivate", "<col=ff9040>Protect from Melee");
                                    Time.sleep(10, 20);
                                }
                            }
                        }
                    }
                }
            };
            prayerThread.start();
        }
        if (lastFlick == -1 || System.currentTimeMillis() - lastFlick > 30000) {
            for (int i = 0; i < 2; i++) {
                WidgetComponent component = Widgets.get(271, 11);
                if (component != null) {
                    component.processAction(ActionOpcodes.WIDGET_ACTION, 1, (i == 0 ? "Activate" : "Deactivate"), "<col=ff9040>Rapid Heal");
                    Time.sleep(460, 920);
                }
            }
            lastFlick = System.currentTimeMillis();
        }
        if (Game.levels()[Skills.CONSTITUTION] == 51) {
            Item abs = Inventory.first(item -> item != null && item.name() != null && item.name().contains("verload"));
            if (abs != null) {
                abs.processAction(ActionOpcodes.ITEM_ACTION_0, "Drink");
                Time.sleep(1200);
            }
        }
        WidgetComponent absorbParent = Widgets.get(202, 2);
        if (absorbParent != null) {
            WidgetComponent child = absorbParent.child(widgetComponent -> widgetComponent != null && widgetComponent.index() == 9);
            if (child != null) {
                String text = child.text().replace(",", "");
                if (text != null) {
                    int amount = Integer.parseInt(text);
                    if (amount < 950) {
                        absorb = true;
                    }
                }
            }
        } else {
            Time.sleep(100);
            return; //Not in NMZ, we will use the absorb widget to determine this
        }
        if (absorb) {
            Item abs = Inventory.first(item -> item != null && item.name() != null && item.name().contains("Absorption"));
            if (abs != null) {
                abs.processAction(ActionOpcodes.ITEM_ACTION_0, "Drink");
                Time.sleep(100, 500);
            }
            absorb = false;
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int gained = Game.experiences()[Skills.STRENGTH] - startExp;
        g.drawString("Time: " + Time.format(runtime()), 10, 10);
        g.drawString("Exp: " + gained, 10, 25);
        g.drawString("Exp/H: " + hourly(gained), 10, 40);
    }
}