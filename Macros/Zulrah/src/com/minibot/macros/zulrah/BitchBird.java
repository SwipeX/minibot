package com.minibot.macros.zulrah;

import com.minibot.Minibot;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSObjectDefinition;
import com.minibot.macros.zulrah.action.Food;
import com.minibot.macros.zulrah.action.Gear;
import com.minibot.macros.zulrah.action.Potions;
import com.minibot.macros.zulrah.action.Prayer;
import com.minibot.macros.zulrah.listener.ProjectileEvent;
import com.minibot.macros.zulrah.listener.ProjectileListener;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tyler Sedlar
 * @since 7/22/2015
 */
@Manifest(name = "Bitch Bird", author = "Tyler", version = "1.0.0", description = "Fuck you")
public class BitchBird extends Macro implements Renderable {

    public static final int PROJECTILE_CLOUD = 1045;
    public static final int PROJECTILE_SPERM = 1047;
    public static final int PROJECTILE_SNAKELING = 1230;
    public static final int PROJECTILE_RANGE = 1044;
    public static final int PROJECTILE_MAGE = 1046;

    public static final Tile OFFSET_NORTH_EAST = new Tile(4, 3);
    public static final Tile OFFSET_NORTH_WEST = new Tile(-4, 3);

    public static final Tile[] MAP_OFFSETS = {
            new Tile(-5, -1), new Tile(3, -6), new Tile(5, -4), new Tile(-5, 1), new Tile(-6, 0), new Tile(-3, -6),
            new Tile(-1, -7), new Tile(-4, -5), new Tile(4, 2), new Tile(4, 1), new Tile(4, -4), new Tile(-6, 2),
            new Tile(6, -2), new Tile(-6, -1), new Tile(5, -3), new Tile(4, 0), new Tile(-4, 0), new Tile(5, -1),
            new Tile(-5, -0), new Tile(2, -6), new Tile(2, -5), OFFSET_NORTH_EAST, new Tile(1, -5), new Tile(-5, -5),
            new Tile(2, -7), new Tile(6, 2), new Tile(5, -5), new Tile(-4, 1), new Tile(0, -5), new Tile(3, -5),
            new Tile(6, -3), new Tile(-6, -4), new Tile(-2, -5), new Tile(-5, 2), new Tile(4, -3), new Tile(-4, -2),
            new Tile(5, 2), new Tile(-4, -6), new Tile(-3, -5), new Tile(4, -1), new Tile(-5, -4), new Tile(4, -2),
            new Tile(0, -6), new Tile(5, 0), new Tile(-5, -2), new Tile(-5, -3), new Tile(6, 0), new Tile(-6, -2),
            new Tile(-2, -6), new Tile(-2, -7), new Tile(5, -2), new Tile(4, -6), new Tile(6, 1), new Tile(4, -5),
            new Tile(0, -7), new Tile(-4, -1), new Tile(1, -7), new Tile(-4, 2), OFFSET_NORTH_WEST, new Tile(-1, -6),
            new Tile(1, -6), new Tile(-6, -3), new Tile(6, -1), new Tile(-4, -4), new Tile(5, 1), new Tile(-6, 1),
            new Tile(6, -4), new Tile(-1, -5), new Tile(-4, -3)
    };

    private Tile origin;
    private int rangeId;
    private int meleeId;
    private int mageId;

    private int projectileType = -1;

    private String status = "N/A";

    private Npc npc;
    private int previousId;
    private Tile previousTile;
    private final List<Integer> previousIds = new ArrayList<>();

    private long lastRan = -1;

    private GameObject[] clouds;

    private final ProjectileListener projectileListener = new ProjectileListener() {
        public void onProjectileLoaded(ProjectileEvent evt) {
            if (evt.id == PROJECTILE_RANGE || evt.id == PROJECTILE_MAGE) {
                projectileType = evt.id;
            }
        }
    };

    @Override
    public void atStart() {
        Minibot.instance().setVerbose(false);
        projectileListener.start();
    }

    private int previousBackId(int backtrack) {
        return previousIds.get(previousIds.size() - 1 - backtrack);
    }

    private int matchesJad(int id) {
        if (previousIds.size() >= 5) {
            int a = previousBackId(0);
            int b = previousBackId(1);
            int c = previousBackId(2);
            int d = previousBackId(3);
            int e = previousBackId(4);
            boolean phase1 = (a == mageId && b == rangeId && c == mageId && d == meleeId && e == rangeId);
            if (phase1) {
                return 1;
            }
            boolean phase2 = (a == mageId && b == rangeId && c == meleeId && d == mageId && e == rangeId);
            if (phase2) {
                return 2;
            }
            boolean phase3 = (a == mageId && b == rangeId && c == mageId && d == rangeId && e == mageId);
            if (phase3) {
                return 3;
            }
            boolean phase4 = (a == mageId && b == rangeId && c == mageId && d == rangeId && e == rangeId);
            if (phase4) {
                return 4;
            }
        }
        return -1;
    }

    private List<GameObject> clouds() {
        return Objects.loaded(o -> {
            RSObjectDefinition def = o.definition();
            return def != null && def.getSizeX() == 3 && def.getSizeY() == 3 && o.hasBaseColors(10543);
        }, 20);
    }

    private Tile tileForOffset(Tile offset) {
        return origin.derive(offset.x(), offset.y());
    }

    private boolean clouded(Tile tile) {
        for (GameObject object : clouds) {
            if (object.location().distance(tile) <= 1) {
                return true;
            }
        }
        return false;
    }

    private Tile nearestSafeTileToZulrah() {
        List<Tile> valid = new ArrayList<>();
        for (Tile offset : MAP_OFFSETS) {
            Tile tile = tileForOffset(offset);
            if (!clouded(tile)) {
                valid.add(tile);
            }
        }
        valid.sort((a, b) -> npc.distance(a) - npc.distance(b));
        Tile nearest = valid.get(0);
        Tile nw = tileForOffset(OFFSET_NORTH_WEST);
        Tile ne = tileForOffset(OFFSET_NORTH_EAST);
        if (nearest.distance(nw) < 3) {
            nearest = nw;
        } else if (nearest.distance(ne) < 3) {
            nearest = ne;
        }
        return nearest;
    }

    private void handlePrayer(int id) {
        if (id == meleeId) {
            status = "Melee stage";
            Prayer.deactivateAll();
        } else {
            Prayer protect;
            int match = matchesJad(id);
            if (match != -1) {
                status = "Jad phase #" + match;
                protect = (projectileType == PROJECTILE_MAGE ? Prayer.PROTECT_FROM_MISSILES : Prayer.PROTECT_FROM_MAGIC);
            } else if (id == mageId) {
                status = "Magic phase";
                protect = Prayer.PROTECT_FROM_MAGIC;
            } else {
                status = "Range phase";
                protect = Prayer.PROTECT_FROM_MISSILES;
            }
            protect.setActive(true);
        }
    }

    private void handleMelee(Npc npc) {
        int orientation = npc.getOrientation();
//        boolean west = (orientation >= 1537 && orientation <= 1713);
        int sum = orientation + Players.local().getOrientation();
        if (orientation == 1281 || sum == 1583 || sum == 2048) {
            System.out.println("FUCKING RUN MARTY");
            Tile[] offsets = {new Tile(6, 2), new Tile(-6, 2)};
            Tile dest = null;
            for (Tile offset : offsets) {
                Tile tile = tileForOffset(offset);
                if (dest == null || tile.distance() < dest.distance()) {
                    dest = tile;
                }
            }
            for (int i = 0; i < 2; i++) {
                Walking.walkTo(dest);
                Time.sleep(100, 200);
            }
            lastRan = Time.millis();
        }
    }

    private void handleStats() {
        Potions.drink();
        Gear.equip();
        Food.eat();
    }

    @Override
    public void run() {
        List<GameObject> clouds = clouds();
        this.clouds = clouds.toArray(new GameObject[clouds.size()]);
        npc = Zulrah.monster();
        if (npc != null) {
            int id = npc.id();
            Tile tile = npc.location();
            if (origin == null) {
                origin = tile;
                rangeId = id;
                meleeId = (rangeId + 1);
                mageId = (rangeId + 2);
            }
            if ((previousId != -1 && previousId != id) || (previousTile != null && previousTile.distance(tile) > 0)) {
                previousIds.add(id);
            }
            previousId = id;
            previousTile = tile;
            handlePrayer(id);
            handleStats();
            Tile near = nearestSafeTileToZulrah();
            if (near != null && near.distance() > 0) {
                if (lastRan == -1 || Time.millis() - lastRan > Random.nextInt(4200, 4600)) {
                    Walking.walkTo(near);
                    Time.sleep(100, 200);
                }
            } else {
                if (id != meleeId) {
                    lastRan = -1;
                } else {
                    handleMelee(npc);
                }
                Character target = Players.local().target();
                if (target == null || !target.name().equals("Zulrah")) {
                    if (lastRan == -1 || Time.millis() - lastRan > Random.nextInt(4200, 4600)) {
                        handleStats();
                        npc.processAction("Attack");
                        Time.sleep(100, 200);
                    }
                }
            }
        } else {
            status = "N/A";
            origin = null;
            previousId = -1;
            previousIds.clear();
            Prayer.deactivateAll();
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.drawString("Status: " + status, 15, 40);
        if (origin != null && npc != null) {
            String ids = Arrays.toString(previousIds.toArray());
            ids = ids.replaceAll(Integer.toString(rangeId), "RANGED");
            ids = ids.replaceAll(Integer.toString(mageId), "MAGE");
            ids = ids.replaceAll(Integer.toString(meleeId), "MELEE");
            g.drawString("Previous IDS: " + ids, 15, 55);
            g.drawString("Orientation: " + npc.getOrientation(), 15, 70);
            int xOff = origin.x() - Players.local().x();
            int yOff = -(origin.y() - Players.local().y());
            g.drawString("Offset: " + xOff + ", " + yOff, 15, 90);
            for (Tile offset : MAP_OFFSETS) {
                Tile tile = tileForOffset(offset);
                g.setColor(clouded(tile) ? Color.RED : Color.GREEN);
                if (tile.distance() < 14) {
                    tile.draw(g);
                    Point p = tile.toViewport(0.5, 0.5, 0);
                    g.drawString(offset.toString(), p.x, p.y);
                }
            }
            Tile safe = nearestSafeTileToZulrah();
            g.setColor(Color.CYAN);
            safe.draw(g);
        }
    }
}