package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.util.filter.Filter;
import com.minibot.client.natives.RSNode;
import com.minibot.client.natives.RSNodeDeque;
import com.minibot.client.natives.RSProjectile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tyler Sedlar
 * @since 7/22/2015
 */
public class Projectiles {

    public static List<RSProjectile> loaded(Filter<RSProjectile> filter) {
        List<RSProjectile> projectiles = new ArrayList<>();
        RSNodeDeque deque = Minibot.instance().client().getProjectiles();
        if (deque != null) {
            RSNode tail = deque.getTail();
            RSNode current = tail.getPrevious();
            while (current != null && !current.equals(tail)) {
                RSProjectile projectile = (RSProjectile) current;
                if (filter.accept(projectile)) {
                    projectiles.add(projectile);
                }
                current = current.getPrevious();
            }
        }
        return projectiles;
    }

    public static List<RSProjectile> loaded() {
        return loaded(p -> true);
    }
}
