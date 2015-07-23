package com.minibot.macros.zulrah.listener;

import com.minibot.api.method.Game;
import com.minibot.api.method.Projectiles;
import com.minibot.api.util.Random;
import com.minibot.bot.macro.LoopTask;
import com.minibot.client.natives.RSProjectile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 7/22/2015
 */
public abstract class ProjectileListener extends LoopTask {

    private Map<Integer, Integer> cycles = new HashMap<>();

    public abstract void onProjectileLoaded(ProjectileEvent evt);

    @Override
    public int loop() {
        for (RSProjectile projectile : Projectiles.loaded()) {
            int id = projectile.getId();
            int cycle = projectile.getCycle();
            boolean cached = cycles.containsKey(id) && (Game.cycle() - cycles.get(id) < 5);
            if ((Game.cycle() - cycle) < 5 && !cached) {
                cycles.put(id, cycle);
                onProjectileLoaded(new ProjectileEvent(projectile, id, cycle));
            }
        }
        return Random.nextInt(25, 50);
    }
}
