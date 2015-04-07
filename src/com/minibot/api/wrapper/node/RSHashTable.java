package com.minibot.api.wrapper.node;

import com.minibot.api.wrapper.Wrapper;
import com.minibot.mod.hooks.ReflectionData;

/**
 * @author Tyler Sedlar
 */
@ReflectionData(className = "HashTable")
public class RSHashTable extends Wrapper {

    public RSHashTable(Object raw) {
        super(raw);
    }

    public int index() {
        return hook("index").getInt(get());
    }

    public int size() {
        return hook("size").getInt(get());
    }

    public Object[] buckets() {
        Object[] rawBuckets = (Object[]) hook("buckets").get(get());
        if (rawBuckets == null)
            return new Object[0];
        Object[] buckets = new Object[rawBuckets.length];
        System.arraycopy(rawBuckets, 0, buckets, 0, rawBuckets.length);
        return buckets;
    }

    public Object head() {
        return hook("head").get(get());
    }

    public Object tail() {
        return hook("tail").get(get());
    }

    public RSTableIterator iterator() {
        return new RSTableIterator(this);
    }
}
