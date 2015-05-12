package com.minibot.api.wrapper.locatable;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.ObjectAction;
import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.wrapper.Wrapper;
import com.minibot.client.natives.RSInteractableObject;
import com.minibot.client.natives.RSObjectDefinition;
import com.minibot.util.DefinitionLoader;

import java.awt.*;

/**
 * @author Tyler Sedlar
 */
public class GameObject extends Wrapper<RSInteractableObject> implements Locatable {


    public GameObject(RSInteractableObject raw) {
        super(raw);
    }


    public int uid() {
        return raw.getId();
    }

    public int id() {
        return raw != null ? uid() >> 14 & 0x7FFF : -1;
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
        return raw.getWorldX();
    }

    public int fineY() {
        return raw.getWorldY();
    }

    public int plane() {
        return raw.getPlane();
    }

    public int height() {
        return Math.max(0, raw.getHeight());
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
        return def !=null ? definition().getName() : null;
    }

    public RSObjectDefinition definition() {
        try {
            return DefinitionLoader.findObjectDefinition(id());
        } catch (Exception e) {
            return null;
        }
    }
    public void processAction(String action){
        processAction(ActionOpcodes.OBJECT_ACTION_0,action);
    }
    public void processAction(int opcode,String action) {
        Point screen = screen();
        RuneScape.processAction(new ObjectAction(opcode, uid(), localX(), localY()),
                action, definition().getName(), screen.x, screen.y);
    }

    private Point screen() {
        return Projection.groundToViewport(fineX(), fineY());
    }
}
