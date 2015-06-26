package com.minibot.macros.agility;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Game;
import com.minibot.api.method.Ground;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Condition;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Jacob Doiron
 * @since 6/25/2015
 */
@Manifest(name = "Agility", author = "Jacob", version = "1.0.0", description = "Completes Canifis agility course")
public class Agility extends Macro implements Renderable {

    private static final Obstacle TREE = new Obstacle(10819, "Climb", new Tile(3505, 3489, 0), new Tile(3506, 3492, 2));
    private static final Obstacle GAP_ONE = new Obstacle(10820, "Jump", new Tile(3505, 3498, 2), new Tile(3502, 3504, 2));
    private static final Obstacle GAP_TWO = new Obstacle(10821, "Jump", new Tile(3496, 3504, 2), new Tile(3492, 3504, 2));
    private static final Obstacle GAP_THREE = new Obstacle(10828, "Jump", new Tile(3485, 3499, 2), new Tile(3479, 3499, 3));
    private static final Obstacle GAP_FOUR = new Obstacle(10822, "Jump", new Tile(3478, 3491, 3), new Tile(3478, 3486, 2));
    private static final Obstacle POLE_VAULT = new Obstacle(10831, "Vault", new Tile(3480, 3483, 2), new Tile(3489, 3476, 3));
    private static final Obstacle GAP_FIVE = new Obstacle(10823, "Jump", new Tile(3503, 3476, 3), new Tile(3510, 3476, 2));
    private static final Obstacle GAP_SIX = new Obstacle(10832, "Jump", new Tile(3510, 3483, 2), new Tile(3510, 3485, 0));

    private static final Obstacle[] OBSTACLES = {TREE, GAP_ONE, GAP_TWO, GAP_THREE, GAP_FOUR, POLE_VAULT, GAP_FIVE,
            GAP_SIX};

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static String status = "Nothing";
    private static int nextPlane;
    private static int startExp;
    private static int marks;
    private static int step;

    @Override
    public void atStart() {
        startExp = Game.totalExperience();
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Player local = Players.local();
        GroundItem mark = Ground.nearestByFilter(i -> i != null && i.id() == 11849);
        if (local != null) {
            if (mark != null && mark.distance() <= 3) {
                status = "Mark of spagnola";
                Walking.walkTo(mark.location());
                Time.sleep(225, 500);
                mark.processAction("Take");
                Time.sleep(() -> {
                    GroundItem mark1 = Ground.nearestByFilter(i -> i != null && i.id() == 11849);
                    return mark1 == null;
                }, Random.nextInt(4500, 5700));
                marks++;
            }
            if (local.location().plane() == 0) {
                step = 0;
            }
            Obstacle next = OBSTACLES[step];
            nextPlane = next.finish().plane();
            status = "Walking to next: " + next.id();
            GameObject nearest = Objects.topAt(next.start());
            if (nearest != null) {
                status = next.id() + " -> " + next.action();
                Tile finish = next.finish();
                nearest.processAction(next.action(), next.start().localX(), next.start().localY());
                if (Time.sleep(() -> finish.distance() <= 3 && local.location().plane() == nextPlane &&
                        local.animation() == -1, Random.nextInt(8500, 12000))) {
                    Time.sleep(550, 785);
                    step = step + 1 % 8;
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Status: " + status, 10, 10);
        g.drawString("Tile: " + (Players.local() != null ? Players.local().location() : "null"), 10, 22);
        g.drawString("Exp: " + ValueFormat.format(Game.totalExperience() - startExp, TEXT_FORMAT) + " (" +
                ValueFormat.format(hourly(Game.totalExperience() - startExp), TEXT_FORMAT) + "/H)", 10, 34);
        g.drawString("Marks: " + marks + " (" + hourly(marks) + "/H)", 10, 46);
    }
}