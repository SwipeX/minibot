package com.minibot.macros;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.macro.Macro;
import com.minibot.api.macro.Manifest;
import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Skills;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tim on 5/17/15.
 */
@Manifest(name = "NightmareZone", author = "Swipe", version = "1.0.0", description = "Absorption NMZ Player")
public class NightmareZone extends Macro implements Renderable {
    private boolean absorb;
    private long lastFlick = -1;
    private long lastCombat;
    Thread prayerThread;
    int startExp = -1;
    private long start_time;

    @Override
    public void run() {
        if (startExp == -1) {
            startExp = Game.experiences()[Skills.RANGE];
            start_time = System.currentTimeMillis();
        }
        if (prayerThread == null) {
            prayerThread = new Thread() {
                public void run() {
                    WidgetComponent absorbParent = Widgets.get(202, 2);
                    while (absorbParent != null) {
                        if (Game.levels()[Skills.PRAYER] <= 0)
                            return;
                        Item j = Inventory.first(item -> item != null && item.name() != null && item.name().contains("cake"));
                        if (j != null) {
                            if (j.index() == 27) {
                                WidgetComponent protect = Widgets.get(271, 18);
                                if (protect != null) {
                                    protect.processAction(ActionOpcodes.WIDGET_ACTION, 1, "Activate", "<col=ff9040>Protect from Melee");
                                    Time.sleep(520, 580);
                                    protect.processAction(ActionOpcodes.WIDGET_ACTION, 1, "Deactivate", "<col=ff9040>Protect from Melee");
                                    Time.sleep(160, 185);
                                }
                            }
                        }
                    }
                }
            };
            prayerThread.start();
        }
        WidgetComponent absorbParent = Widgets.get(202, 2);
        if (absorbParent != null) {
            WidgetComponent child = absorbParent.child(widgetComponent -> widgetComponent != null && widgetComponent.index() == 9);
            if (child != null) {
                String text = child.text().replace(",", "");
                if (text != null) {
                    int amount = Integer.parseInt(text);
                    if (amount < 200 && amount < 950) {
                        absorb = true;
                    }
                }
            }
        } else {
            Time.sleep(100);
            return; //Not in NMZ, we will use the absorb widget to determine this
        }
        if (lastFlick == -1 || System.currentTimeMillis() - lastFlick > 30000) {
            for (int i = 0; i < 2; i++) {
                WidgetComponent component = Widgets.get(271, 11);
                if (component != null) {
                    component.processAction(ActionOpcodes.WIDGET_ACTION, 1, (i == 0 ? "Activate" : "Deactivate"), "<col=ff9040>Rapid Heal");
                    Time.sleep(10, 30);
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
        if (absorb) {
            Item abs = Inventory.first(item -> item != null && item.name() != null && item.name().contains("Absorption"));
            if (abs != null) {
                abs.processAction(ActionOpcodes.ITEM_ACTION_0, "Drink");
                Time.sleep(100, 500);
            }
        }
    }

    public int hourly(int val, long difference) {
        return (int) Math.ceil(val * 3600000D / difference);
    }

    public static String format(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawRect(0, 0, 150, 50);
        long time_diff = System.currentTimeMillis() - start_time;
        int gain = Game.experiences()[Skills.RANGE] - startExp;
        g.drawString("Time: " + format(time_diff), 10, 10);
        g.drawString("Exp: " + gain, 10, 25);
        g.drawString("Exp/H: " + hourly(gain, time_diff), 10, 40);
    }
}
