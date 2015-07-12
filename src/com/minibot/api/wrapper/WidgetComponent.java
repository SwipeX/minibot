package com.minibot.api.wrapper;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.Action;
import com.minibot.api.action.tree.WidgetAction;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.filter.Filter;
import com.minibot.client.natives.RSWidget;

import java.awt.Rectangle;

/**
 * @author Tyler Sedlar
 */
public class WidgetComponent extends Wrapper<RSWidget> {

    private final int index;
    private int ownerId;

    public WidgetComponent(int ownerId, RSWidget raw) {
        super(raw);
        this.ownerId = ownerId;
        index = -1;
    }

    public WidgetComponent(RSWidget raw, int index) {
        super(raw);
        this.index = index;
    }

    public WidgetComponent[] children() {
        RSWidget[] children = raw.getChildren();
        if (children == null) {
            return new WidgetComponent[0];
        }
        int index = 0;
        WidgetComponent[] array = new WidgetComponent[children.length];
        for (RSWidget widget : children) {
            if (widget != null) {
                array[index] = new WidgetComponent(widget, index);
            }
            index++;
        }
        return array;
    }

    private int ownerId() {
        return raw.getOwnerId();
    }

    public int ownerIndex() {
        return ownerId;
    }

    public WidgetComponent owner() {
        int uid = ownerId();
        if (uid == -1) {
            return null;
        }
        int parent = uid >> 16;
        int child = uid & 0xFFFF;
        return Widgets.get(parent, child);
    }

    public int x() {
        return relX();
    }

    public int y() {
        return relY();
    }

    public int hash() {
        return raw.getId() >>> 16;
    }

    public int index() {
        int id = raw.getId();
        return index < 0 ? id & 0xFF : index;
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

    public int type() {
        return raw.getType();
    }

    public int rawIndex() {
        return raw.getIndex();
    }

    public int[] itemIds() {
        return raw.getItemIds();
    }

    public int[] itemStackSizes() {
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
        return boundsIndex() != -1;
    }

    public WidgetComponent child(Filter<WidgetComponent> filter) {
        for (WidgetComponent child : children()) {
            try {
                if (filter.accept(child)) {
                    return child;
                }
                WidgetComponent result = child.child(filter);
                if (result != null) {
                    return result;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @Override
    public void processAction(String action) {
        processAction(ActionOpcodes.WIDGET_ACTION, action);
    }

    @Override
    public void processAction(int opcode, String action) {
        processAction(ActionOpcodes.WIDGET_ACTION, Action.indexOf(actions(), action) + 1, action, "");
    }

    public void processAction(int opcode, int actionIndex, String action, String target) {
        RuneScape.processAction(new WidgetAction(opcode, actionIndex, index, raw.getId()), action, target, x(), y());
    }
}