package com.minibot.macros.zulrah;

import com.minibot.Minibot;
import com.minibot.api.method.Players;
import com.minibot.api.method.Projectiles;
import com.minibot.api.util.Renderable;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSProjectile;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Tyler Sedlar
 * @since 7/22/2015
 */
@Manifest(name = "Bitch Bird", author = "Tyler", version = "1.0.0", description = "Fuck you")
public class BitchBird extends Macro implements Renderable {

    private List<String> projectileIds = new ArrayList<>();
    private Map<String, Integer> projectileCycles = new HashMap<>();

    @Override
    public void atStart() {
        Minibot.instance().setVerbose(false);
    }

    @Override
    public void run() {
        List<RSProjectile> projectiles = Projectiles.loaded();
        for (RSProjectile projectile : projectiles) {
            String id = Integer.toString(projectile.getId());
            if (!projectileIds.contains(id)) {
                projectileIds.add(id);
            }
            int cycle = projectile.getCycle();
            projectileCycles.put(id, cycle);
        }
        Collections.sort(projectileIds, (a, b) -> {
            return projectileCycles.get(b) - projectileCycles.get(a);
        });
    }

    @Override
    public void render(Graphics2D g) {
        g.drawString(Arrays.toString(projectileIds.toArray()), 15, 40);
    }
}
