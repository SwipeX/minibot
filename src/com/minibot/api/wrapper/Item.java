package com.minibot.api.wrapper;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.TableItemAction;
import com.minibot.api.method.Bank;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Identifiable;
import com.minibot.api.util.Random;
import com.minibot.api.wrapper.def.ItemDefinition;
import com.minibot.internal.def.DefinitionLoader;

import java.awt.*;

/**
 * @author Tyler Sedlar
 */
public class Item implements Identifiable {

    private int id;
    private int stackSize;
    private int index;
    private ItemType type = ItemType.INVENTORY;
    private WidgetComponent comp;

    public Item(int id, int stackSize) {
        this.id = id;
        this.stackSize = stackSize;
    }

    public Item(int id, int stackSize, int index) {
        this.id = id;
        this.index = index;
        this.stackSize = stackSize;
    }

    public Item(int id, int stackSize, ItemType type) {
        this.id = id;
        this.stackSize = stackSize;
        this.type = type;
    }

    public Item(WidgetComponent comp, ItemType type, int index) {
        this(comp.itemId(), comp.itemAmount());
        this.type = type;
        this.comp = comp;
        this.index = index;
    }

    private Object definition() {
        return DefinitionLoader.findObjectDefinition(id);
    }

    public WidgetComponent component() {
        return comp;
    }

    public int id() {
        return id;
    }

    public int index() {
        return index;
    }

    public Point screen() {
        if (comp != null) {
            return new Point(comp.x(), comp.y());
        } else if (type != ItemType.INVENTORY) {
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
        Point screen = screen();
        return new Rectangle(screen.x, screen.y, 36, 36);
    }

    public int stackSize() {
        return stackSize;
    }

    public String name() {
        return ItemDefinition.name(definition());
    }

    public ItemType type() {
        return type;
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
        if (type == ItemType.BANK) {
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
        int widgetUid = component.uid();
        RuneScape.processAction(new TableItemAction(opcode, id(), index(), widgetUid), action,
                "<col=ff9040>" + itemName + "</col>", p.x, p.y);
    }

    public enum ItemType {
        INVENTORY(0), BANK(1), EQUIPMENT(2);

        final int type;

        ItemType(int type) {
            this.type = type;
        }
    }
}