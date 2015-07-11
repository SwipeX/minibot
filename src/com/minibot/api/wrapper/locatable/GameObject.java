package com.minibot.api.wrapper.locatable;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.Action;
import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.wrapper.Wrapper;
import com.minibot.client.natives.*;
import com.minibot.util.DefinitionLoader;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Tyler Sedlar
 */
public class GameObject extends Wrapper<ClientNative> implements Locatable {

    public GameObject(ClientNative raw) {
        super(raw);
    }

    public int uid() {
        if (raw instanceof RSInteractableObject)
            return ((RSInteractableObject) raw).getId();
        return ((RSDecoration) raw).getId();
    }

    public int id() {
        return uid() >> 14 & 0x7FFF;
    }

    public int localX() {
        return fineX() >> 7;
    }

    public int x() {
        return Game.baseX() + localX();
    }

    public int localY() {
        return fineY() >> 7;
    }

    public int y() {
        return Game.baseY() + localY();
    }

    public int fineX() {
        if (raw instanceof RSInteractableObject)
            return ((RSInteractableObject) raw).getWorldX();
        return ((RSDecoration) raw).getWorldX();
    }

    public int fineY() {
        if (raw instanceof RSInteractableObject)
            return ((RSInteractableObject) raw).getWorldY();
        return ((RSDecoration) raw).getWorldY();
    }

    public int plane() {
        if (raw instanceof RSInteractableObject)
            return ((RSInteractableObject) raw).getPlane();
        return ((RSDecoration) raw).getPlane();
    }

    public int height() {
        int height = 1;
        if (raw instanceof RSInteractableObject) {
            height = ((RSInteractableObject) raw).getHeight();
        } else {
            RSRenderableNode model = ((RSDecoration) raw).getModel();
            if (model == null) {
                if (raw instanceof RSWallDecoration) {
                    model = ((RSWallDecoration) raw).getBackup();
                } else if(raw instanceof RSBoundary) {
                    model = ((RSBoundary) raw).getBackup();
                }
            }
            if (model != null)
                height = model.getHeight();
        }
        return Math.max(0, height);
    }

    @Override
    public Tile location() {
        return new Tile(x(), y(), Game.plane());
    }

    @Override
    public int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return (int) Projection.distance(Players.local(), this);
    }

    public String name() {
        RSObjectDefinition def = definition();
        return def != null ? definition().getName() : null;
    }

    public RSObjectDefinition definition() {
        try {
            return DefinitionLoader.findObjectDefinition(id());
        } catch (Exception e) {
            return null;
        }
    }

    public void processAction(int opcode, String action, int realLocalX, int realLocalY) {
        RSObjectDefinition definition = definition();
        if (definition == null)
            return;
        String name = definition.getName();
        if (name == null)
            return;
        if (realLocalX == -1)
            realLocalX = localX();
        if (realLocalY == -1)
            realLocalY = localY();
        RuneScape.processAction(Action.valueOf(opcode, uid(), realLocalX, realLocalY), action, name);
    }

    @Override
    public void processAction(int opcode, String action) {
        processAction(opcode, action, -1, -1);
    }

    public void processAction(String action, int realLocalX, int realLocalY) {
        RSObjectDefinition definition = definition();
        if (definition == null)
            return;
        String[] actions = definition.getActions();
        if (actions == null)
            return;
        int index = Arrays.asList(actions).indexOf(action);
        if (index >= 0)
            processAction(ActionOpcodes.OBJECT_ACTION_0 + index, action, realLocalX, realLocalY);
    }

    @Override
    public void processAction(String action) {
        processAction(action, -1, -1);
    }

    private Point screen() {
        return Projection.groundToViewport(fineX(), fineY());
    }

    public boolean containsAction(String action) {
        RSObjectDefinition def = definition();
        if (def != null && def.getActions() != null) {
            for (String action0 : def.getActions()) {
                if (action0 != null && action0.equals(action)) {
                    return true;
                }
            }
        }
        return false;
    }
}