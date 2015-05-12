package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.client.natives.RSInteractableObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Dekker
 * @since 5/11/15
 */
public class Objects {
    public static List<GameObject> all() {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        RSInteractableObject[] objects = Minibot.instance().client().getObjects();
        for (RSInteractableObject obj : objects) {
            if (obj == null)
                continue;
            gameObjects.add(new GameObject(obj));
        }
        return gameObjects;
    }

    public static GameObject topAt(Tile t) {
        for (GameObject obj : all()) {
            if (obj.location().equals(t))
                return obj;
        }
        return null;
    }
}
