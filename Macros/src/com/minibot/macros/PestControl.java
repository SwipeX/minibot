package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.*;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

@Manifest(name = "PestControl", author = "un faggoto", version = "0.1", description = "Fights npcs in pest control")
public class PestControl extends Macro implements Renderable {

    //TODO make it open doors if needed instead of running all the way around to attack

    private static int startExp;
    private static int startPoints;
    private static int currentPoints;

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static final Filter<Npc> ATTACKABLE = npc -> npc.name() != null && !npc.dead()
            && (npc.name().equals("Portal") || npc.name().equals("Brawler")
            || npc.name().equals("Defiler") || npc.name().equals("Ravager")
            || npc.name().equals("Shifter") || npc.name().equals("Spinner")
            || npc.name().equals("Splatter") || npc.name().equals("Torcher"));

    private static final Boat boat = Boat.INTERMEDIATE;

    private static Tile safeTileFor(Tile src, Tile bad) {
        return src.x() < bad.x() ? src.derive(-2, 0) : src.derive(2, 0);
    }

    private static State state() {
        if (Players.local().x() == boat.startX) {
            return State.JOINING;
        } else if (boat.area.contains(Players.local())) {
            return State.WAITING;
        } else if (Npcs.nearestByFilter(ATTACKABLE) != null) {
            return State.ATTACKING_NPC;
        }
        return State.MOVING;
    }

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
        startExp = Game.experiences()[Skills.RANGED];
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (Players.local().targetIndex() != -1) {
            Character target = Players.local().target();
            if (target != null && target.dead() && "Splatter".equals(target.name())) {
                Walking.walkTo(safeTileFor(Players.local().location(), target.location()));
            }
        }
        switch (state()) {
            case JOINING:
                GameObject cross = Objects.nearestByName("Gangplank");
                if (cross != null)
                    cross.processAction("Cross");
                break;
            case WAITING:
                WidgetComponent component = Widgets.get(407, 15);
                if (component != null && component.visible()) {
                    if (startPoints == 0) {
                        startPoints = Integer.parseInt(component.text().split("Pest Points: ")[1]);
                    }
                    currentPoints = Integer.parseInt(component.text().split("Pest Points: ")[1]);
                }
                Time.sleep(500, 1000);
                break;
            case ATTACKING_NPC:
                if (Players.local().targetIndex() == -1) {
                    Npc pest = Npcs.nearestByFilter(ATTACKABLE);
                    if (pest != null) {
                        pest.processAction("Attack");
                    }
                }
                break;
            case MOVING:
                Npc voidKnight = Npcs.nearestByName("Void Knight");
                if (voidKnight != null) {
                    Walking.walkTo(voidKnight.location());
                } else {
                    Walking.walkTo(new Tile(Players.local().x(), Players.local().y() - 18));
                }
                break;
        }
        Time.sleep(Random.nextInt(400, 1500));
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString(String.format("Time: %s", Time.format(runtime())), 10, 10);
        int exp = Game.experiences()[Skills.RANGED] - startExp;
        int points = currentPoints - startPoints;
        g.drawString(String.format("Points: %d/+%d (%s/H)", currentPoints, points, ValueFormat.format(hourly(points), TEXT_FORMAT)), 10, 22);
        g.drawString(String.format("Exp: %s (%s/H)", ValueFormat.format(exp, TEXT_FORMAT),
                ValueFormat.format(hourly(exp), TEXT_FORMAT)), 10, 34);
    }

    private enum Boat {

        INTERMEDIATE(new Area(new Tile(2638, 2642, 0), new Tile(2641, 2647, 0)), 2644);

        private final Area area;
        private final int startX;

        Boat(Area area, int startX) {
            this.area = area;
            this.startX = startX;
        }
    }

    private enum State {
        JOINING,
        WAITING,
        ATTACKING_NPC,
        MOVING
    }
}