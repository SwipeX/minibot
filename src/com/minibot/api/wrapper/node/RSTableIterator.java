package com.minibot.api.wrapper.node;

/**
 * @author Tyler Sedlar
 */
public class RSTableIterator {

    private final RSHashTable table;
    private int index;
    private Object current;

    public RSTableIterator(RSHashTable table) {
        this.table = table;
    }

    public Object first() {
        index = 0;
        return next();
    }

    public Object next() {
        Object[] buckets = table.buckets();
        if (index > 0 && current.hashCode() != buckets[index - 1].hashCode()) {
            Object node = current;
            current = RSNode.previous(node);
            return node;
        }
        while (index < buckets.length) {
            Object node = RSNode.previous(buckets[index++]);
            if ((index - 1) >= buckets.length || node == null)
                return null;
            if (buckets[index - 1].hashCode() != node.hashCode()) {
                current = RSNode.previous(node);
                return node;
            }
        }
        return null;
    }

    public Object findByUid(int id) {
        for (Object node = first(); node != null; node = next()) {
            if (RSNode.uid(node) == id)
                return node;
        }
        return null;
    }

    public Object findByWidgetId(int id) {
        for (Object node = first(); node != null; node = next()) {
            if (RSNode.widgetId(node) == id)
                return node;
        }
        return null;
    }
}
