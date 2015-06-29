package com.minibot.macros;

import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "PowerMiner", author = "Tyler", version = "1.0.0", description = "Powermines surrounding rocks")
public class PowerMiner extends Macro implements Renderable, ChatboxListener {

    private final Set<Integer> identifiedRockIds = new HashSet<>();
    private final Set<Integer> validRockIds = new HashSet<>();

    private int messageCount = 0;
    private boolean valid = false;

    @Override
    public void run() {
        Deque<GameObject> objects = Objects.loaded(2);
        for (GameObject object : objects) {
            int id = object.id();
            if (!identifiedRockIds.contains(id)) {
                int cachedMessageCount = messageCount;
                object.processAction("Examine");
                while (messageCount == cachedMessageCount)
                    Time.sleep(50, 100);
                if (valid)
                    validRockIds.add(id);
                identifiedRockIds.add(id);
            }
        }
        GameObject rock = Objects.nearestByFilter(o -> validRockIds.contains(o.id()));
        if (rock != null) {
            Inventory.dropAll(i -> {
                String name = i.name();
                return name != null && name.toLowerCase().contains("ore");
            });
            rock.processAction("Mine");
            Time.sleep(() -> {
                if (Players.local().animation() == -1)
                    return true;
                GameObject current = Objects.topAt(rock.location());
                return current != rock;
            }, 5000);
        }
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message.contains("outcrop") || message.contains("Stoney!")) {
            messageCount++;
            valid = message.contains("outcrop");
        }
    }

    @Override
    public void render(Graphics2D g) {

    }
}