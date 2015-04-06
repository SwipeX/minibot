package com.minibot.api.wrapper.def;

import com.minibot.api.method.Game;
import com.minibot.api.wrapper.Wrapper;
import com.minibot.internal.mod.ModScript;
import com.minibot.internal.mod.hooks.ReflectionData;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
@ReflectionData(className = "NpcDefinition")
public class NpcDefinition extends Wrapper {

    public NpcDefinition(Object raw) {
        super(raw);
    }

    public boolean transformed() {
        String name = name();
        return name == null || name.equals("null");
    }

    public int id() {
        return hook("id").getInt(get());
    }

    public String name() {
        return hook("name").getString(get());
    }

    public int transformIndex() {
        return hook("transformIndex").getInt(get());
    }

    public int[] transformIds() {
        return (int[]) hook("transformIds").get(get());
    }

    public void fix() {
        try {
            int[] transformIds = transformIds();
            if (transformIds == null)
                return;
            int newId = transformIds[Game.settings()[transformIndex()]];
            Object raw = ModScript.serveInvoke("Client#loadNpcDefinition").invokeStatic(
                    new Class<?>[]{int.class}, new Object[]{newId}
            );
            set(raw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
