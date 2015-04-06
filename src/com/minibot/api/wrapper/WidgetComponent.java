package com.minibot.api.wrapper;

import com.minibot.api.action.tree.WidgetAction;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Array;
import com.minibot.api.util.Random;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.node.RSNode;
import com.minibot.internal.mod.hooks.ReflectionData;

import java.awt.*;

/**
 * @author Tyler Sedlar
 */
@ReflectionData(className = "Widget")
public class WidgetComponent extends Wrapper {

    private int index;
    private int ownerId;

    public WidgetComponent(int ownerId, Object raw) {
        super(raw);
        this.ownerId = ownerId;
        this.index = -1;
    }

    public WidgetComponent(Object raw, int index) {
        super(raw);
        this.index = index;
    }

    public WidgetComponent[] children() {
        Object[] children = (Object[]) hook("children").get(get());
        if (children == null)
            return new WidgetComponent[0];
        int index = 0;
        WidgetComponent[] array = new WidgetComponent[0];
        for (Object widget : children) {
            if (widget != null)
                array = Array.add(array, new WidgetComponent(widget, index));
            index++;
        }
        return array;
    }

    private int ownerUid() {
        int uid = rawOwnerId();
        if (uid == -1) {
            Object node = Widgets.table().iterator().findByWidgetId(uid());
            if (node != null)
                uid = (int) RSNode.uid(node);
        }
        return uid;
    }

    public int widget() {
        return ownerId;
    }

    public WidgetComponent owner() {
        int uid = ownerUid();
        if (uid == -1)
            return null;
        int parent = uid >> 16;
        int child = uid & 0xFFFF;
        return Widgets.get(parent, child);
    }

    public int x() {
        int[] positionsX = Widgets.positionsX();
        int index = boundsIndex();
        int relX = relX();
        WidgetComponent owner = owner();
        int x = 0;
        if (owner != null) {
            x = owner.x() - scrollX();
        } else {
            if (index >= 0 && positionsX[index] > 0) {
                int absX = positionsX[index];
                if (type() > 0)
                    absX += relX;
                return absX;
            }
        }
        return x + relX;
    }

    public int y() {
        int[] positionsY = Widgets.positionsY();
        int index = boundsIndex();
        int relY = relY();
        WidgetComponent owner = owner();
        int y = 0;
        if (owner != null) {
            y = owner.y() - scrollY();
        } else {
            if (index >= 0 && positionsY[index] > 0) {
                int absY = positionsY[index];
                if (type() > 0)
                    absY += relY;
                return absY;
            }
        }
        return y + relY;
    }

    public int uid() {
        return hook("id").getInt(get()) >>> 16;
    }

    public int id() {
        int id = hook("id").getInt(get());
        return index < 0 ? id & 0xFF : index;
    }

    public int rawOwnerId() {
        return hook("ownerId").getInt(get());
    }

    public int boundsIndex() {
        return hook("boundsIndex").getInt(get());
    }

    public int itemId() {
        return hook("itemId").getInt(get());
    }

    public int itemAmount() {
        return hook("itemAmount").getInt(get());
    }

    public int relX() {
        return hook("x").getInt(get());
    }

    public int relY() {
        return hook("y").getInt(get());
    }

    public int width() {
        return hook("width").getInt(get());
    }

    public int height() {
        return hook("height").getInt(get());
    }

    public int scrollX() {
        return hook("scrollX").getInt(get());
    }

    public int scrollY() {
        return hook("scrollY").getInt(get());
    }

    public int type() {
        return hook("type").getInt(get());
    }

    public int index() {
        return hook("index").getInt(get());
    }

    public int[] itemIds() {
        return (int[]) hook("itemIds").get(get());
    }

    public int[] stackSizes() {
        return (int[]) hook("stackSizes").get(get());
    }

    public int textureId() {
        return hook("textureId").getInt(get());
    }

    public String text() {
        return hook("text").getString(get());
    }

    public String[] actions() {
        return (String[]) hook("actions").get(get());
    }

    public Rectangle bounds() {
        return new Rectangle(x(), y(), width(), height());
    }

    public boolean hidden() {
        WidgetComponent owner = owner();
        return (owner != null && owner.hidden()) || hook("hidden").getBoolean(get());
    }

    public boolean visible() {
        return !hidden();
    }

    public WidgetComponent findChildByFilter(Filter<WidgetComponent> filter) {
        for (WidgetComponent child : children()) {
            try {
                if (filter.accept(child))
                    return child;
                WidgetComponent result = child.findChildByFilter(filter);
                if (result != null)
                    return result;
            } catch (Exception ignored) {}
        }
        return null;
    }

    //TODO confirm if this works for other widgets. only tested on Click here to continue
    public void processAction(String action) {
        Rectangle bounds = bounds();
        if (bounds == null || bounds.x < 0 || bounds.y < 0 || bounds.width < 0 || bounds.height < 0)
            return;
        Point p = Random.nextPoint(bounds);
        RuneScape.processAction(new WidgetAction(-1, id(), 30, 0), action, "", p.x, p.y);
    }
}
