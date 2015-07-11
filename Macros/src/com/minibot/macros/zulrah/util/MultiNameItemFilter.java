package com.minibot.macros.zulrah.util;

import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;

public class MultiNameItemFilter implements Filter<Item> {

    public final String[] matches;

    public MultiNameItemFilter(String... matches) {
        this.matches = matches;
    }

    @Override
    public boolean accept(Item item) {
        String name = item.name();
        if (name != null) {
            for (String match : matches) {
                if (name.contains(match))
                    return true;
            }
        }
        return false;
    }
}