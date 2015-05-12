package com.minibot.api.wrapper;

import com.minibot.api.action.tree.TableItemAction;
import com.minibot.api.action.tree.WidgetAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Identifiable;
import com.minibot.api.util.Random;
import com.minibot.util.DefinitionLoader;

import java.awt.*;
import java.util.Arrays;

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

    public void processAction(int opcode, String action) {
        String itemName = name();
        if (itemName == null)
            return;
        Rectangle bounds = bounds();
        if (bounds == null)
            return;
        Point p = Random.nextPoint(bounds);
        int widgetParent, widgetChild;
        if (source == Source.BANK) {
            widgetParent = Bank.BANK_PARENT;
            widgetChild = Bank.SLOT_CONTAINER;
        } else {
            if (Bank.viewing()) {
                widgetParent = Inventory.INVENTORY_PARENT;
                widgetChild = Inventory.INVENTORY_CONTAINER;
            } else {
                widgetParent = Inventory.BANK_PARENT;
                widgetChild = Inventory.BANK_CONTAINER;
            }
        }
        WidgetComponent component = Widgets.get(widgetParent, widgetChild);
        if (component == null)
            return;
        int widgetUid = component.hash();
        if (source == Source.INVENTORY) {
            RuneScape.processAction(new TableItemAction(opcode, id(), index(), widgetUid), action,
                    "<col=ff9040>" + itemName + "</col>", p.x, p.y);
        } else {
            String[] actions = component.actions();
            if (actions == null)
                return;
            int actionIndex = Arrays.asList(actions).indexOf(action);
            RuneScape.processAction(new WidgetAction(opcode, actionIndex, index(), widgetUid),
                    action, "", p.x, p.y);
        }
    }
    public String name(){
        return DefinitionLoader.findItemDefinition(id()).getName();
    }

    public enum Source {
        INVENTORY(0), BANK(1), EQUIPMENT(2);

        private final int type;

        Source(int type) {
            this.type = type;
        }
    }
}