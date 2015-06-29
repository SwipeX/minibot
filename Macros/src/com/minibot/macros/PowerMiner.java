package com.minibot.macros;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.ExamineEntityAction;
import com.minibot.api.method.*;
import com.minibot.api.method.Objects;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;
import java.util.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "PowerMiner", author = "Tyler", version = "1.0.0", description = "Powermines surrounding rocks")
public class PowerMiner extends Macro implements Renderable, ChatboxListener {

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private final Set<Integer> identifiedRockIds = new HashSet<>();
    private final Set<Integer> validRockIds = new HashSet<>();

    private int messageCount = 0;
    private boolean valid = false;
    private int startExp;
    private int mined = 0;

    @Override
    public void atStart() {
        startExp = Game.experiences()[Skills.MINING];
    }

    @Override
    public void run() {
        Deque<GameObject> objects = Objects.loaded(2);
        for (GameObject object : objects) {
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
        GameObject rock = Objects.nearestByFilter(o -> validRockIds.contains(o.id()), 2);
        if (rock != null) {
            Inventory.dropAll(i -> {
                String name = i.name();
                return name != null && name.toLowerCase().contains("ore");
            });
            rock.processAction("Mine");
            if (Time.sleep(() -> {
                if (Players.local().animation() != -1)
                    return true;
                GameObject current = Objects.topAt(rock.location());
                return current.id() != rock.id();
            }, 5000)) {
                GameObject top = Objects.topAt(rock.location());
                if (top.id() == rock.id()) {
                    Time.sleep(300, 450);
                    Time.sleep(() -> {
                        if (Players.local().animation() == -1)
                            return true;
                        GameObject current = Objects.topAt(rock.location());
                        return current.id() != rock.id();
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
        int gained = Game.experiences()[Skills.MINING] - startExp;
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fChopped = ValueFormat.format(mined, COMMA_FORMAT);
        String fChoppedHr = ValueFormat.format(hourly(mined), COMMA_FORMAT);
        g.drawString("Mined: " + fChopped + " (" + fChoppedHr + "/HR)", 13, yOff += 15);
        String fExp = ValueFormat.format(gained, COMMA_FORMAT);
        String fExpHr = ValueFormat.format(hourly(gained), THOUSAND_FORMAT);
        g.drawString("Experience: " + fExp + " (" + fExpHr + "/HR)", 13, yOff + 15);
    }
}