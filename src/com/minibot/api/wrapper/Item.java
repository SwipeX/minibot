package com.minibot.api.wrapper;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.Action;
import com.minibot.api.action.tree.TableItemAction;
import com.minibot.api.action.tree.WidgetAction;
import com.minibot.api.method.RuneScape;
import com.minibot.api.util.Identifiable;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.util.DefinitionLoader;

import java.awt.*;

/**
 * @author Tyler Sedlar
 */
public class Item implements Identifiable {

    private int id;
    private int amount;
    private int index;
    private Source source = Source.INVENTORY;
    private WidgetComponent comp;

    public Item(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public Item(int id, int amount, int index) {
        this.id = id;
        this.index = index;
        this.amount = amount;
    }

    public Item(int id, int amount, Source source) {
        this.id = id;
        this.amount = amount;
        this.source = source;
    }

    public Item(WidgetComponent comp, Source source, int index) {
        this(comp.itemId(), comp.itemAmount());
        this.source = source;
        this.comp = comp;
        this.index = index;
    }

    public WidgetComponent owner() {
        return comp;
    }

    public int id() {
        return id;
    }

    public int index() {
        return index;
    }

    public Point point() {
        if (comp != null) {
            return new Point(comp.x(), comp.y());
        } else if (source != Source.INVENTORY) {
            return new Point(-1, -1);
        }
        int col = (index % 4);
        int row = (index / 4);
        int x = 580 + (col * 42);
        int y = 228 + (row * 36);
        return new Point(x - 19, y - 18);
    }

    public Rectangle bounds() {
        if (comp != null)
            return comp.bounds();
        Point screen = point();
        return new Rectangle(screen.x, screen.y, 36, 36);
    }

    public int amount() {
        return amount;
    }

    public Source source() {
        return source;
    }

    /**
     * @return true if this item is contained within a table-type widget
     */
    public boolean table() {
        if (comp == null) return true;
        return comp.type() == 2;
    }

    public void processAction(int opcode, String action) {
        String itemName = name();
        if (itemName == null)
            return;
        if (table()) {
            RuneScape.processAction(new TableItemAction(opcode, id, index, comp.raw.getId()), action, itemName, 0, 0);
        } else {
            int index = Action.indexOf(comp.actions(), action) + 1;
            if (index > 4) {
                RuneScape.processAction(new WidgetAction(true, index, this.index, comp.raw.getId()), action, itemName, 0, 0);
            } else {
                RuneScape.processAction(new WidgetAction(opcode, index, this.index, comp.raw.getId()), action, itemName, 0, 0);
            }
        }
    }

    public void processAction(String action) {
        if (table()) {
            if (definition() == null)
                return;
            processAction(ActionOpcodes.ITEM_ACTION_0 + Action.indexOf(definition().getActions(), action), action);
        } else {
            int index = Action.indexOf(comp.actions(), action) + 1;
            RuneScape.processAction(new WidgetAction(index > 4, index, this.index, comp.raw.getId()), action, name(), 50, 50);
        }
    }

    @Override
    public String name() {
        RSItemDefinition definition = definition();
        return definition == null ? null : definition.getName();
    }

    private RSItemDefinition definition() {
        return DefinitionLoader.findItemDefinition(id());
    }

    public void setComponent(WidgetComponent comp) {
        this.comp = comp;
    }


    public enum Source {
        INVENTORY(0), BANK(1), EQUIPMENT(2);

        private final int type;

        Source(int type) {
            this.type = type;
        }
    }
}