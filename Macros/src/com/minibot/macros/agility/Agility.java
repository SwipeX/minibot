package com.minibot.macros.agility;

import com.minibot.Minibot;
import com.minibot.api.method.Game;
import com.minibot.api.method.Ground;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.locatable.Area;
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
@Manifest(name = "Agility", author = "Jacob", version = "1.1.2", description = "Completes Canifis agility course")
public class Agility extends Macro implements Renderable {

    private static final Course GNOME = new Course(0,
            new Obstacle(23145, "Walk-across", new Area(new Tile(2473, 3436, 0), new Tile(2487, 3439, 0)), new Tile(2474, 3435, 0), new Tile(2474, 3426, 0)),
            new Obstacle(23134, "Climb-over", new Area(new Tile(2469, 3424, 0), new Tile(2478, 3430, 0)), new Tile(2473, 3425, 0), new Tile(2473, 3423, 1)),
            new Obstacle(23559, "Climb", new Area(new Tile(2471, 3422, 1), new Tile(2476, 3424, 1)), new Tile(2473, 3422, 1), new Tile(2473, 3420, 2)),
            new Obstacle(23557, "Walk-on", new Area(new Tile(2472, 3418, 2), new Tile(2477, 3421, 2)), new Tile(2478, 3420, 2), new Tile(2486, 3419, 2)),
            new Obstacle(23560, "Climb-down", new Area(new Tile(2483, 3418, 2), new Tile(2488, 3421, 2)), new Tile(2486, 3419, 2), new Tile(2487, 3420, 0)),
            new Obstacle(23135, "Climb-over", new Area(new Tile(2482, 3414, 0), new Tile(2490, 3425, 0)), new Tile(2483, 3426, 0), new Tile(2483, 3431, 0)),
            new Obstacle(23138, "Squeeze-through", new Area(new Tile(2482, 3427, 0), new Tile(2490, 3432, 0)), new Tile(2484, 3431, 0), new Tile(2484, 3440, 0))
    );

    private static final Course DRAYNOR = new Course(25,
            new Obstacle(10073, "Climb", new Area(new Tile(3102, 3260, 0), new Tile(3105, 3279, 0)), new Tile(3103, 3279, 0), null),
            new Obstacle(10074, "Cross", new Area(new Tile(3097, 3277, 3), new Tile(3102, 3281, 3)), new Tile(3098, 3277, 3), null),
            new Obstacle(10075, "Cross", new Area(new Tile(3088, 3273, 3) , new Tile(3092, 3276, 3)), new Tile(3092, 3276, 3), null),
            new Obstacle(10077, "Balance", new Area(new Tile(3089, 3265, 3), new Tile(3095, 3267, 3)), new Tile(3089, 3264, 3), null),
            new Obstacle(10084, "Jump-up", new Area(new Tile(3087, 3257, 3), new Tile(3088, 3261, 3)), new Tile(3088, 3256, 3), null),
            new Obstacle(10085, "Jump", new Area(new Tile(3087, 3255, 3), new Tile(3094, 3255, 3)), new Tile(3095, 3255, 3), null),
            new Obstacle(10086, "Climb-down", new Area(new Tile(3096, 3256, 3), new Tile(3101, 3261, 3)), new Tile(3102, 3261, 3), null)
    );

    private static final Course CANIFIS = new Course(25,
            new Obstacle(10819, "Climb", new Area(new Tile(0, 0, 0), new Tile(0, 0, 0)), new Tile(3505, 3489, 0), new Tile(3506, 3492, 2)),
            new Obstacle(10820, "Jump", new Area(new Tile(3502, 3488, 2), new Tile(3509, 3497, 2)), new Tile(3505, 3498, 2), new Tile(3502, 3504, 2)),
            new Obstacle(10821, "Jump", new Area(new Tile(3497, 3504, 2), new Tile(3503, 3506, 2)), new Tile(3496, 3504, 2), new Tile(3492, 3504, 2)),
            new Obstacle(10828, "Jump", new Area(new Tile(3486, 3499, 2), new Tile(3492, 3504, 2)), new Tile(3485, 3499, 2), new Tile(3479, 3499, 3)),
            new Obstacle(10822, "Jump", new Area(new Tile(3475, 3492, 3), new Tile(3479, 3499, 3)), new Tile(3478, 3491, 3), new Tile(3478, 3486, 2)),
            new Obstacle(10831, "Vault", new Area(new Tile(3477, 3481, 2), new Tile(3484, 3487, 2)), new Tile(3480, 3483, 2), new Tile(3489, 3476, 3)),
            new Obstacle(10823, "Jump", new Area(new Tile(3486, 3469, 3), new Tile(3503, 3478, 2)), new Tile(3503, 3476, 3), new Tile(3510, 3476, 2)),
            new Obstacle(10832, "Jump", new Area(new Tile(3509, 3475, 2), new Tile(3515, 3482, 2)), new Tile(3510, 3483, 2), new Tile(3510, 3485, 0))
    );

    private static final Course[] COURSES = {GNOME, DRAYNOR, CANIFIS};

    private static Course course;

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static String status = "Nothing";
    private static int finishPlane;
    private static int startExp;
    private static int marks;

    @Override
    public void atStart() {
        startExp = Game.totalExperience();
        Player local = Players.local();
        if (local != null) {
            search: for (Course c : COURSES) {
                for (Obstacle o : c.obstacles()) {
                    if (o.area().contains(local)) {
                        course = c;
                        break search;
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Player local = Players.local();
        if (local != null) {
            Obstacle current = course.obstacles()[0];
            Obstacle next = course.obstacles()[1];
            if (course.equals(GNOME) || local.location().plane() != 0) {
                for (int i = 0; i < course.obstacles().length; i++) {
                    Obstacle o = course.obstacles()[i];
                    if (o.area() != null && o.area().contains(local)) {
                        current = o;
                        next = course.obstacles()[(i + 1) % course.obstacles().length];
                        break;
                    }
                }
            }
            GroundItem mark = Ground.nearestByFilter(i -> i != null && i.id() == 11849);
            if (mark != null && current.area().contains(mark)) {
                status = "Mark of anal spagh00ter";
                mark.processAction("Take");
                Time.sleep(() -> {
                    GroundItem mark1 = Ground.nearestByFilter(i -> i != null && i.id() == 11849);
                    return mark1 == null;
                }, Random.nextInt(4500, 5700));
                marks++;
            }
            //finishPlane = current.finish().plane();
            GameObject nearest = Objects.topAt(current.start());
            if (nearest != null) {
                status = current.id() + " -> " + current.action();
                //Tile finish = current.finish();
                Area finish = next.area();
                for (int i = 0; i < Random.nextInt(1, 3); i++) {
                    nearest.processAction(current.action(), current.start().localX(), current.start().localY());
                    Time.sleep(60, 240);
                }
                if (Time.sleep(() -> /*finish.distance() <= 3 && local.location().plane() == finishPlane &&
                        */ finish.contains(local) && local.animation() == -1, Random.nextInt(8500, 12000))) {
                    Time.sleep(550, 785);
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Status: " + status, 10, 10);
        g.drawString("Time " + Time.format(runtime()), 10, 22);
        g.drawString("Exp: " + ValueFormat.format(Game.totalExperience() - startExp, TEXT_FORMAT) + " (" +
                ValueFormat.format(hourly(Game.totalExperience() - startExp), TEXT_FORMAT) + "/H)", 10, 34);
        g.drawString("Marks: " + marks + " (" + hourly(marks) + "/H)", 10, 46);
    }
}