package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.util.Identifiable;
import com.minibot.api.util.NodeTable;
import com.minibot.client.natives.RSHashTable;
import com.minibot.client.natives.RSItemContainer;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSNode;
import com.minibot.util.DefinitionLoader;

import java.util.ArrayList;
import java.util.List;

public class ItemContainers {
 
    public static final int VARROCK_GENERAL_STORE = 4;
    public static final int VARROCK_RUNE_STORE = 5;
    public static final int VARROCK_STAFF_STORE = 51;
    public static final int PRICE_CHECKER = 90;
    public static final int INVENTORY = 93;
    public static final int EQUIPMENT = 94;
    public static final int BANK = 95;
    public static final int EXCHANGE_COLLECTION = 518;
 
    public static RSHashTable raw() {
        return Minibot.instance().client().getItemContainers();
    }
 
    public static NodeTable storage() {
        RSHashTable raw = raw();
        return raw != null ? new NodeTable(raw) : null;
    }
 
    private static RSItemContainer lookup(int tableKey) {
        NodeTable store = storage();
        if (store == null)
            return null;
        RSNode node = store.lookup(tableKey);
        return node != null && node instanceof RSItemContainer ? (RSItemContainer) node : null;
    }
 
    public static Entry[] entriesIn(int tableKey) {
        RSItemContainer table = lookup(tableKey);
        if (table == null)
            return new Entry[0];
        int len = table.getIds().length;
        if (len != table.getStackSizes().length)
            return new Entry[0];
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            int id = table.getIds()[i];
            int amt = table.getStackSizes()[i];
            if (id > 0 && amt > 0)
                entries.add(new Entry(i, id, amt));
        }
        return entries.toArray(new Entry[entries.size()]);
    }
 
    public static int[] idsIn(int tableKey) {
        RSItemContainer table = lookup(tableKey);
        if (table == null)
            return new int[0];
        int len = table.getIds().length;
        if (len != table.getStackSizes().length)
            return new int[0];
        List<Integer> values = new ArrayList<>();
        for (int value : table.getIds()) {
            if (value > 0)
                values.add(value);
        }
        int[] valuesNew = new int[values.size()];
        for (int i = 0; i < valuesNew.length; i++)
            valuesNew[i] = values.get(i);
        return valuesNew;
    }
 
    public static int idAt(int tableKey, int index) {
        int[] ids = idsIn(tableKey);
        if (ids.length > index)
            return ids[index];
        return -1;
    }
 
    public static int amountAt(int tableKey, int index) {
        int[] qtys = amountsIn(tableKey);
        if (qtys.length > index)
            return qtys[index];
        return -1;
    }
 
    public static Entry entryAt(int tableKey, int index) {
        RSItemContainer table = lookup(tableKey);
        if (table == null)
            return null;
        int len = table.getIds().length;
        if (len != table.getStackSizes().length)
            return null;
        return len > index ? new Entry(index, table.getIds()[index], table.getStackSizes()[index]) : null;
    }
 
    public static int[] amountsIn(int tableKey) {
        RSItemContainer table = lookup(tableKey);
        if (table == null)
            return new int[0];
        int len = table.getStackSizes().length;
        if (len != table.getIds().length)
            return new int[0];
        List<Integer> values = new ArrayList<>();
        for (int value : table.getStackSizes()) {
            if (value > 0)
                values.add(value);
        }
        int[] valuesNew = new int[values.size()];
        for (int i = 0; i < valuesNew.length; i++)
            valuesNew[i] = values.get(i);
        return valuesNew;
    }
 
    public static Entry[] inventory() {
        return entriesIn(INVENTORY);
    }
 
    public static Entry[] equipment() {
        return entriesIn(EQUIPMENT);
    }
 
    public static Entry[] priceChecker() {
        return entriesIn(PRICE_CHECKER);
    }
 
    public static Entry[] bank() { //the items are cached until you enter a new region
        return entriesIn(BANK);
    }
 
    public static Entry[] exchangeCollection() {
        return entriesIn(EXCHANGE_COLLECTION);
    }
 
    public static Entry[] varrockGeneralStore() {
        return entriesIn(VARROCK_GENERAL_STORE);
    }
 
    public static Entry[] varrockRuneStore() {
        return entriesIn(VARROCK_RUNE_STORE);
    }
 
    public static Entry[] varrockStaffStore() {
        return entriesIn(VARROCK_STAFF_STORE);
    }
 
    public static class Entry implements Identifiable {
 
        private final int index;
        private final int id;
        private final int quantity;
        private final RSItemDefinition definition;
 
        public Entry(int index, int id, int quantity) {
            this.index = index;
            this.id = id;
            this.quantity = quantity;
            this.definition = DefinitionLoader.findItemDefinition(id);
        }
 
        public int id() {
            return id;
        }
 
        public int amount() {
            return quantity;
        }
 
        public RSItemDefinition definition() {
            return definition;
        }
 
        public String name() {
            return definition == null ? null : definition.getName();
        }
 
        public String[] actions() {
            return definition == null ? new String[0] : definition.getActions();
        }
 
        public String[] groundActions() {
            return definition == null ? new String[0] : definition.getGroundActions();
        }
 
        public int index() {
            return index;
        }
    }
}