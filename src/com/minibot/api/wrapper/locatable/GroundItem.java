package com.minibot.api.wrapper.locatable;


import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.GroundItemAction;
import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.wrapper.Wrapper;
import com.minibot.client.natives.RSItem;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSNode;
import com.minibot.util.DefinitionLoader;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Tyler Sedlar
 */
public class GroundItem extends Wrapper<RSItem> implements Locatable {

    private final int plane;
    private int x;
    private int y;

    public GroundItem(RSNode raw, int x, int y) {
        super((RSItem) raw);
        this.x = x;
        this.y = y;
        plane = Game.plane();
    }

    public Point screen() {
        return Projection.groundToViewport((x - Game.baseX()) * 128, (y - Game.baseY()) * 128);
    }

    public Point map() {
        return Projection.groundToMap(location().fineX(), location().fineY());
    }


    public int id() {
        return raw.getId();
    }

    public int stackSize() {
        return raw.getStackSize();
    }

    public String name() {
        RSItemDefinition def = definition();
        return def !=null ? definition().getName() : null;
    }

    public RSItemDefinition definition() {
        try {
            return DefinitionLoader.findItemDefinition(id());
        } catch (Exception e) {
            return null;
        }
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Tile location() {
        return new Tile(x, y, plane);
    }

    public int localX() {
        return x - Game.baseX();
    }

    public int localY() {
        return y - Game.baseY();
    }

    @Override
    public int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return (int) Projection.distance(Players.local(), this);
    }

    @Override
    public void processAction(int opcode, String action) {
        String name = name();
        if (name == null)
            return;
        RuneScape.processAction(new GroundItemAction(opcode, id(), localX(), localY()), action, name, 0, 0);
    }

    @Override
    public void processAction(String action) {
        RSItemDefinition definition = definition();
        if (definition == null)
            return;
        String[] actions = definition.getGroundActions();
        if (actions == null)
            return;
        int index = Arrays.asList(actions).indexOf(action);
        if (index == -1 && (actions[2] == null || actions[2].equals("null")) && action.equals("Take")) {
            processAction(ActionOpcodes.GROUND_ITEM_ACTION_2, action);
        } else {
            processAction(ActionOpcodes.GROUND_ITEM_ACTION_0 + index, action);
        }
    }
}