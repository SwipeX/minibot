package com.minibot.api.action.tree;

import com.minibot.api.method.Widgets;
import com.minibot.api.wrapper.WidgetComponent;

// Named abstract since TableAction can not be named
// The rest follow for simplicity through commonality
public abstract class AbstractTableAction extends Action {

    public AbstractTableAction(int opcode, int item_id, int item_index, int table_uid) {
        super(opcode, item_id, item_index, table_uid);
    }

    @Override
    public final int significantArgs() {
        return SIG_ALL;
    }

    public int itemId() {
        return arg0;
    }

    public int itemIndex() {
        return arg1;
    }

    public int tableUid() {
        return arg2;
    }

    public int parent() {
        return tableUid() >> 16;
    }

    public int child() {
        return tableUid() & 0xffff;
    }

    public int actionIndex() {
        return -1;
    }

    public WidgetComponent table() {
        final int parent = parent();
        final int child = child();
        return Widgets.get(parent, child);
    }

   /* public Item getItem() {
        Widget container = table();
        if(container == null) return null;
        return new WidgetItem(container,itemIndex());
    }
*/

    @Override
    public final boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0 && this.arg1 == arg1 && this.arg2 == arg2;
    }

    @Override
    public String toString() {
        return "TableAction:[Address(uid=" + tableUid() + ")=<" + parent() + "#" + child() + "> ] " +
                "Item(index=" + itemIndex() + ")=" /*+ getItem()*/;
    }
}
