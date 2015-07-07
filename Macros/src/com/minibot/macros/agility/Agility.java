package com.minibot.macros.agility;

import com.minibot.Minibot;
import com.minibot.api.method.Game;
import com.minibot.api.method.Ground;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.method.Skills;
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
@Manifest(name = "Agility", author = "Jacob", version = "1.1.5", description = "Completes agility courses")
public class Agility extends Macro implements Renderable {

    private static final Course GNOME = new Course(false,
            new Obstacle(23145, "Walk-across", new Area(new Tile(2473, 3436, 0), new Tile(2487, 3439, 0)), null, new Tile(2474, 3435, 0)),
            new Obstacle(23134, "Climb-over", new Area(new Tile(2469, 3424, 0), new Tile(2478, 3430, 0)), null, new Tile(2473, 3425, 0)),
            new Obstacle(23559, "Climb", new Area(new Tile(2471, 3422, 1), new Tile(2476, 3424, 1)), null, new Tile(2473, 3422, 1)),
            new Obstacle(23557, "Walk-on", new Area(new Tile(2472, 3418, 2), new Tile(2477, 3421, 2)), null, new Tile(2478, 3420, 2)),
            new Obstacle(23560, "Climb-down", new Area(new Tile(2483, 3418, 2), new Tile(2488, 3421, 2)), null, new Tile(2486, 3419, 2)),
            new Obstacle(23135, "Climb-over", new Area(new Tile(2482, 3414, 0), new Tile(2490, 3425, 0)), null, new Tile(2483, 3426, 0)),
            new Obstacle(23138, "Squeeze-through", new Area(new Tile(2482, 3427, 0), new Tile(2490, 3432, 0)), null, new Tile(2484, 3431, 0))
    );

    private static final Course DRAYNOR = new Course(true, // fix draynor fails
            new Obstacle(10073, "Climb", new Area(new Tile(3102, 3260, 0), new Tile(3105, 3279, 0)), null, new Tile(3103, 3279, 0)),
            new Obstacle(10074, "Cross", new Area(new Tile(3097, 3277, 3), new Tile(3102, 3281, 3)), null, new Tile(3098, 3277, 3)),
            new Obstacle(10075, "Cross", new Area(new Tile(3088, 3273, 3) , new Tile(3092, 3276, 3)), null, new Tile(3092, 3276, 3)),
            new Obstacle(10077, "Balance", new Area(new Tile(3089, 3265, 3), new Tile(3095, 3267, 3)), null, new Tile(3089, 3264, 3)),
            new Obstacle(10084, "Jump-up", new Area(new Tile(3087, 3257, 3), new Tile(3088, 3261, 3)), null, new Tile(3088, 3256, 3)),
            new Obstacle(10085, "Jump", new Area(new Tile(3087, 3255, 3), new Tile(3094, 3255, 3)), null, new Tile(3095, 3255, 3)),
            new Obstacle(10086, "Climb-down", new Area(new Tile(3096, 3256, 3), new Tile(3101, 3261, 3)), null, new Tile(3102, 3261, 3))
    );

    private static final Course VARROCK = new Course(true, // fix varrock fails
            new Obstacle(10586, "Climb", new Area(new Tile(3221, 3409, 0), new Tile(3241, 3418, 0)), null, new Tile(3221, 3414, 0)),
            new Obstacle(10587, "Cross", new Area(new Tile(3214, 3410, 3), new Tile(3219, 3419, 3)), new Area(new Tile(0, 0, 0), new Tile(0, 0, 0)), new Tile(3213, 3414, 3)),
            new Obstacle(10642, "Leap", new Area(new Tile(3201, 3413, 3) , new Tile(3208, 3418, 3)), null, new Tile(3200, 3416, 3)),
            new Obstacle(10777, "Balance", new Area(new Tile(3194, 3416, 1), new Tile(3197, 3416, 1)), new Area(new Tile(0, 0, 0), new Tile(0, 0, 0)), new Tile(3191, 3415, 1)),
            new Obstacle(10778, "Leap", new Area(new Tile(3192, 3402, 3), new Tile(3198, 3406, 3)), null, new Tile(3193, 3401, 3)),
            new Obstacle(10779, "Leap", new Area(new Tile(3182, 3382, 3), new Tile(3208, 3400, 3)), null, new Tile(3209, 3397, 3)),
            new Obstacle(10780, "Leap", new Area(new Tile(3218, 3393, 3), new Tile(3232, 3402, 3)), null, new Tile(3233, 3402, 3)),
            new Obstacle(10781, "Hurdle", new Area(new Tile(3236, 3403, 3), new Tile(3240, 3408, 3)), null, new Tile(3236, 3409, 3)),
            new Obstacle(107, "Jump-off", new Area(new Tile(3236, 3410, 3), new Tile(3240, 3415, 3)), null, new Tile(3236, 3416, 3))
    );

    private static final Course CANIFIS = new Course(true,
            new Obstacle(10819, "Climb", new Area(new Tile(3504, 3484, 0), new Tile(3510, 3491, 0)), null, new Tile(3505, 3489, 0)),
            new Obstacle(10820, "Jump", new Area(new Tile(3502, 3488, 2), new Tile(3509, 3497, 2)), null, new Tile(3505, 3498, 2)),
            new Obstacle(10821, "Jump", new Area(new Tile(3497, 3504, 2), new Tile(3503, 3506, 2)), null, new Tile(3496, 3504, 2)),
            new Obstacle(10828, "Jump", new Area(new Tile(3486, 3499, 2), new Tile(3492, 3504, 2)), new Area(new Tile(3481, 3494, 0), new Tile(3484, 3503, 0)), new Tile(3485, 3499, 2)),
            new Obstacle(10822, "Jump", new Area(new Tile(3475, 3492, 3), new Tile(3479, 3499, 3)), null, new Tile(3478, 3491, 3)),
            new Obstacle(10831, "Vault", new Area(new Tile(3477, 3481, 2), new Tile(3484, 3487, 2)), null, new Tile(3480, 3483, 2)),
            new Obstacle(10823, "Jump", new Area(new Tile(3486, 3469, 3), new Tile(3503, 3478, 2)), new Area(new Tile(3505, 3470, 0), new Tile(3508, 3480, 0)), new Tile(3503, 3476, 3)),
            new Obstacle(10832, "Jump", new Area(new Tile(3509, 3475, 2), new Tile(3515, 3482, 2)), null, new Tile(3510, 3483, 2))
    );

    private static final Course SEERS = new Course(true,
            new Obstacle(11373, "Climb-up", new Area(new Tile(2704, 3459, 0), new Tile(2731, 3489, 0)), null, new Tile(2729, 3489, 0)),
            new Obstacle(11374, "Jump", new Area(new Tile(2721, 3490, 3), new Tile(2730, 3497, 3)), new Area(new Tile(2715, 3491, 0), new Tile(2718, 3497, 0)), new Tile(2720, 3492, 3)),
            new Obstacle(11378, "Cross", new Area(new Tile(2705, 3488, 2), new Tile(2714, 3495, 2)), new Area(new Tile(2706, 3483, 0), new Tile(2713, 3486, 0)), new Tile(2710, 3489, 2)),
            new Obstacle(11375, "Jump", new Area(new Tile(2710, 3477, 2), new Tile(2715, 3481, 2)), null, new Tile(2710, 3476, 2)),
            new Obstacle(11376, "Jump", new Area(new Tile(2700, 3470, 3), new Tile(2715, 3475, 3)), null, new Tile(2700, 3469, 3)),
            new Obstacle(11377, "Jump", new Area(new Tile(2698, 3460, 2), new Tile(2702, 3465, 2)), null, new Tile(2703, 3461, 2))
    );

    private static final Course RELLEKKA = new Course(true,
            new Obstacle(11391, "Climb", new Area(new Tile(2622, 3671, 0), new Tile(2654, 3685, 0)), null, new Tile(2625, 3677, 0)),
            new Obstacle(11392, "Leap", new Area(new Tile(2622, 3672, 3), new Tile(2626, 3676, 3)), null, new Tile(2621, 3669, 3)),
            new Obstacle(11393, "Cross", new Area(new Tile(2615, 3658, 3), new Tile(2622, 3668, 3)), new Area(new Tile(2621, 3654, 0), new Tile(2625, 3658, 0)), new Tile(2623, 3658, 3)),
            new Obstacle(11395, "Leap", new Area(new Tile(2626, 3651, 3), new Tile(2630, 3655, 3)), new Area(new Tile(2626, 3650, 0), new Tile(2638, 3658, 0)), new Tile(2629, 3656, 3)),
            new Obstacle(11396, "Hurdle", new Area(new Tile(2639, 3649, 3), new Tile(2644, 3653, 3)), null, new Tile(2643 ,3654, 3)),
            new Obstacle(11397, "Cross", new Area(new Tile(2643, 3657, 3), new Tile(2650, 3662, 3)), null, new Tile(2647, 3663, 3)),
            new Obstacle(11404, "Jump-in", new Area(new Tile(2655, 3665, 3), new Tile(2666, 3685, 3)), null, new Tile(2654, 3676, 3))
    );

    private static final Course ARDOUGNE = new Course(true,
            new Obstacle(11405, "Climb-up", new Area(new Tile(2666, 3294, 0), new Tile(2676, 3298, 0)), null, new Tile(2673 ,3298, 0)),
            new Obstacle(11406, "Jump", new Area(new Tile(2671, 3299, 3), new Tile(2671, 3309, 3)), new Area(new Tile(2661, 3308, 0), new Tile(2666, 3320, 0)), new Tile(2670, 3310, 3)),
            new Obstacle(11631, "Walk-on", new Area(new Tile(2662, 3318, 3), new Tile(2666, 3318, 3)), null, new Tile(2661, 3318, 3)),
            new Obstacle(11429, "Jump", new Area(new Tile(2654, 3318, 3), new Tile(2657, 3318, 3)), null, new Tile(2653, 3317, 3)),
            new Obstacle(11430, "Jump", new Area(new Tile(2653, 3311, 3), new Tile(2653, 3314, 3)), null, new Tile(2653, 3308, 3)),
            new Obstacle(11633, "Balance-across", new Area(new Tile(2651, 3301, 3), new Tile(2653, 3309, 3)), null, new Tile(2654, 3300, 3)),
            new Obstacle(11630, "Jump", new Area(new Tile(2656, 3297, 3), new Tile(2657, 3299, 3)), null, new Tile(2656, 3296, 3))
    );

    private static final Course[] COURSES = {GNOME, DRAYNOR, VARROCK, CANIFIS, SEERS, RELLEKKA, ARDOUGNE};

    private static Course course;

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static String status = "Nothing";

    private static int startExp;
    private static int obstacle;
    private static int marks;
    private static int percent;

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
        startExp = Game.experiences()[Skills.AGILITY];
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
        if (course == null) {
            System.err.println("No suitable course found");
            interrupt();
        }
        percent = Random.nextInt(35, 65);
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Player local = Players.local();
        if (local != null) {
            Obstacle current = course.obstacles()[0];
            Obstacle next = course.obstacles()[1];
            if (course.marks() || local.location().plane() != 0) {
                for (int i = 0; i < course.obstacles().length; i++) {
                    Obstacle o = course.obstacles()[i];
                    if (o.area() != null && o.area().contains(local)) {
                        obstacle = i + 1;
                        current = o;
                        next = course.obstacles()[(i + 1) % course.obstacles().length];
                        break;
                    }
                }
            }
            if (Game.energy() >= percent && !Game.runEnabled()) {
                Game.setRun(true);
            }
            if (course.marks()) {
                GroundItem mark = Ground.nearestByFilter(i -> i != null && i.id() == 11849);
                if (mark != null && current.area().contains(mark)) {
                    status = "Mark of anal spagh00ter";
                    mark.processAction("Take");
                    if (Time.sleep(() -> Ground.nearestByFilter(i -> i != null && i.id() == 11849) == null, Random.nextInt(8500, 10700))) {
                        marks++;
                    }
                }
            }
            GameObject nearest = Objects.topAt(current.at());
            if (nearest != null) {
                status = current.id() + " -> " + current.action();
                Area fail = current.fail();
                Area finish = next.area();
                for (int i = 0; i < Random.nextInt(2, 5); i++) {
                    nearest.processAction(current.action(), current.at().localX(), current.at().localY());
                    Time.sleep(160, 260);
                }
                if (Time.sleep(() -> (finish.contains(local) || (fail != null && fail.contains(local))) && local.animation() == -1, Random.nextInt(20000, 25000))) {
                    Time.sleep(660, 900);
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString(String.format("[%d/%d]: %s", obstacle, course.obstacles().length, status), 10, 10);
        g.drawString(String.format("Time %s" ,Time.format(runtime())), 10, 22);
        g.drawString(String.format("Exp: %s (%s/H)", ValueFormat.format(Game.experiences()[Skills.AGILITY] - startExp, TEXT_FORMAT),
                ValueFormat.format(hourly(Game.experiences()[Skills.AGILITY] - startExp), TEXT_FORMAT)), 10, 34);
        g.drawString(String.format("Level: %d", Game.levels()[Skills.AGILITY]), 10, 46);
        if (course.marks()) {
            g.drawString(String.format("Marks: %d (%d/H)", marks, hourly(marks)), 10, 58);
        }
    }
}