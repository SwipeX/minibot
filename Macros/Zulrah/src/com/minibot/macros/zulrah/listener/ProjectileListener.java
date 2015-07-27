package com.minibot.macros.zulrah.listener;

import com.minibot.api.method.Projectiles;
import com.minibot.api.util.Random;
import com.minibot.bot.macro.LoopTask;
import com.minibot.client.natives.RSProjectile;
import com.minibot.macros.zulrah.Zulrah;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 7/22/2015
 */
public abstract class ProjectileListener extends LoopTask {

    private final Map<Integer, Integer> cycles = new HashMap<>();

    public abstract void onProjectileLoaded(ProjectileEvent evt);

    @Override
    public int loop() {
        for (RSProjectile projectile : Projectiles.loaded()) {
            int id = projectile.getId();
            int cycle = projectile.getCycle();
            boolean cached = cycles.containsKey(id) && (cycle - cycles.get(id) < 50);
            if (!cached) {
                cycles.put(id, cycle);
                if (id == Zulrah.PROJECTILE_MAGE) {
                    System.out.println("MAGE PROJECTILE");
                } else if (id == Zulrah.PROJECTILE_RANGED) {
                    System.out.println("RANGED PROJECTILE");
                } else if (id != Zulrah.PROJECTILE_CLOUD && id != Zulrah.PROJECTILE_SNAKELING && id !=
                        Zulrah.PROJECTILE_SPERM) {
                    System.out.println("UNKNOWN PROJECTILE " + id);
                }
                onProjectileLoaded(new ProjectileEvent(projectile, id, cycle));
            }
        }
        return Random.nextInt(25, 50);
    }
}