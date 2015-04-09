package com.minibot.api.wrapper;

import com.minibot.api.method.Widgets;
import com.minibot.api.util.filter.Filter;
import com.minibot.client.natives.RSWidget;

import java.awt.*;

/**
 * @author Tyler Sedlar
 */
public class WidgetComponent extends Wrapper<RSWidget> {

    private int index;
    private int ownerId;

    public WidgetComponent(int ownerId, RSWidget raw) {
        super(raw);
        this.ownerId = ownerId;
        this.index = -1;
    }

    public WidgetComponent(RSWidget raw, int index) {
        super(raw);
        this.index = index;
    }

    public WidgetComponent[] children() {
        RSWidget[] children = raw.getChildren();
        if (children == null)
            return new WidgetComponent[0];
        int index = 0;
        WidgetComponent[] array = new WidgetComponent[children.length];
        for (RSWidget widget : children) {
            if (widget != null)
                array[index] = new WidgetComponent(widget, index);
            index++;
        }
        return array;
    }

    private int ownerUid() {
        return raw.getOwnerId();
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
        return raw.getContainerX() + relX();
    }

    public int y() {
        return raw.getContainerY() + relY();
    }

    public int uid() {
        return raw.getId() >>> 16;
    }

    public int id() {
        int id = raw.getId();
        return index < 0 ? id & 0xFF : index;
    }

    public int rawOwnerId() {
        return raw.getOwnerId();
    }

    public int boundsIndex() {
        return raw.getBoundsIndex();
    }

    public int itemId() {
        return raw.getItemId();
    }

    public int itemAmount() {
        return raw.getItemAmount();
    }

    public int relX() {
        return raw.getX();
    }

    public int relY() {
        return raw.getY();
    }

    public int width() {
        return raw.getWidth();
    }

    public int height() {
        return raw.getHeight();
    }

    public int scrollX() {
        return raw.getScrollX();
    }

    public int scrollY() {
        return raw.getScrollY();
    }

    public int type() {
        return raw.getType();
    }

    public int index() {
        return raw.getIndex();
    }

    public int[] itemIds() {
        return raw.getItemIds();
    }

    public int[] stackSizes() {
        return raw.getStackSizes();
    }

    public int textureId() {
        return raw.getTextureId();
    }

    public String text() {
        return raw.getText();
    }

    public String[] actions() {
        return raw.getActions();
    }

    public Rectangle bounds() {
        return new Rectangle(x(), y(), width(), height());
    }

    public boolean hidden() {
        WidgetComponent owner = owner();
        return (owner != null && owner.hidden()) || raw.isHidden();
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
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
