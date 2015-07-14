package com.minibot.api.util;

import com.minibot.client.natives.RSHashTable;
import com.minibot.client.natives.RSNode;

import java.util.Iterator;

public class NodeTable implements Iterator<RSNode> {

    private int index;
    private RSNode current;
    private RSHashTable raw;

    public NodeTable(RSHashTable table) {
        this.raw = table;
    }

    public RSNode first() {
        index = 0;
        return next();
    }

    @Override
    public boolean hasNext() {
        return index < raw.getBuckets().length;
    }

    public RSNode next() {
        RSNode[] buckets = raw.getBuckets();
        if (index > 0 && current != buckets[index - 1]) {
            RSNode node = current;
            current = node.getPrevious();
            return node;
        }
        while (index < buckets.length) {
            RSNode node = buckets[index++].getPrevious();
            if (index - 1 >= buckets.length || node == null)
                return null;
            if (buckets[index - 1] != node) {
                current = node.getPrevious();
                return node;
            }
        }
        return null;
    }

    public RSNode lookup(int uid) {
        for (RSNode node = first(); node != null; node = next()) {
            System.out.println(uid + " "+node.getUid());
            if (node.getUid() == uid)
                return node;
        }
        return null;
    }
}