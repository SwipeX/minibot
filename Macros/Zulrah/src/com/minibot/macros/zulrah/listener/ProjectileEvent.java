package com.minibot.macros.zulrah.listener;

import com.minibot.client.natives.RSProjectile;

/**
 * @author Tyler Sedlar
 * @since 7/22/2015
 */
public class ProjectileEvent {

    public final RSProjectile projectile;
    public final int id, cycle;

    public ProjectileEvent(RSProjectile projectile, int id, int cycle) {
        this.projectile = projectile;
        this.id = id;
        this.cycle = cycle;
    }
}