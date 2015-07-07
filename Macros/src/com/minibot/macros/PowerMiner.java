package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.ExamineEntityAction;
import com.minibot.api.method.ChatboxListener;
import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Skills;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "PowerMiner", author = "Tyler", version = "1.1.0", description = "Powermines surrounding rocks")
public class PowerMiner extends Macro implements Renderable, ChatboxListener {

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private final Set<Integer> identifiedRockIds = new HashSet<>(5);
    private final Set<Integer> validRockIds = new HashSet<>(5);

    private int messageCount;
    private boolean valid;
    private int startExp;
    private int mined;

    @Override
    public void atStart() {
        startExp = Game.experiences()[Skills.MINING];
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Deque<GameObject> objects = Objects.loaded(2);
        if (objects == null) {
            return;
        }
        for (GameObject object : objects) {
            if (object != null) {
                String name = object.name();
                if (name != null && name.equals("Rocks")) {
                    int id = object.id();
                    if (!identifiedRockIds.contains(id)) {
                        int cachedMessageCount = messageCount;
                        RuneScape.processAction(new ExamineEntityAction(ActionOpcodes.EXAMINE_OBJECT, object.uid(),
                                object.localX(), object.localY()));
                        while (messageCount == cachedMessageCount)
                            Time.sleep(50, 100);
                        System.out.println(id + " " + (valid ? "valid" : "invalid"));
                        if (valid)
                            validRockIds.add(id);
                        identifiedRockIds.add(id);
                    }
                }
            }
        }
        GameObject rock = Objects.nearestByFilter(o -> validRockIds.contains(o.id()), 2);
        if (rock != null) {
            Inventory.dropAll(i -> {
                String name = i.name();
                return name != null && name.contains("ore");
            });
            if (Inventory.full()) {
                Item uncut = Inventory.first(i -> {
                    String name = i.name();
                    return name != null && name.contains("Uncut") && !name.contains("diamond");
                });
                if (uncut != null) {
                    uncut.drop();
                }
            }
            rock.processAction("Mine");
            if (Time.sleep(() -> {
                if (Players.local().animation() != -1)
                    return true;
                GameObject current = Objects.topAt(rock.location());
                return current != null && current.id() != rock.id();
            }, 5000)) {
                GameObject top = Objects.topAt(rock.location());
                if (top != null && top.id() == rock.id()) {
                    Time.sleep(300, 450);
                    Time.sleep(() -> {
                        if (Players.local().animation() == -1)
                            return true;
                        GameObject current = Objects.topAt(rock.location());
                        return current != null && current.id() != rock.id();
                    }, 5000);
                }
            }
        }
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message.contains("outcrop") || message.contains("Stoney!")) {
            messageCount++;
            valid = message.contains("outcrop");
        } else if (message.contains("manage to mine")) {
            mined++;
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString(String.format("Runtime: %s", Time.format(runtime())), 13, 10);
        g.drawString(String.format("Mined: %s (%s/H)", ValueFormat.format(mined, COMMA_FORMAT),
                ValueFormat.format(hourly(mined), COMMA_FORMAT)), 13, 22);
        g.drawString(String.format("Experience: %s (%s/H)", ValueFormat.format(Game.experiences()[Skills.MINING] - startExp, COMMA_FORMAT),
                ValueFormat.format(hourly(Game.experiences()[Skills.MINING] - startExp), THOUSAND_FORMAT)), 13, 34);
    }
}