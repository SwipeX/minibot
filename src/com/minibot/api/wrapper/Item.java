package com.minibot.api.wrapper;

import com.minibot.api.method.Bank;
import com.minibot.api.method.RuneScape;
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

    public void doAction(int packetId, String action) {
        String itemName = name();
        if (itemName == null)
            return;
        Rectangle bounds = bounds();
        if (bounds == null)
            return;
        Point p = new Point(bounds.x + (bounds.width / 2) + Random.nextInt(-4, 4),
                bounds.y + (bounds.height / 2) + Random.nextInt(-4, 4));
        int type = type() == ItemType.BANK ? 786442 : (Bank.viewing() ? 983043 : 9764864);
        RuneScape.doAction(component() != null ? component().id() : index(), type, packetId, index(), action,
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