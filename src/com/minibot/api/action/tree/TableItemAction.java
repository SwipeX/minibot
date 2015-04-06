package com.minibot.api.action.tree;


import com.minibot.api.action.ActionOpcodes;

// Item actions only occur on tables
public class TableItemAction extends AbstractTableAction {

    public TableItemAction(int opcode, int itemId, int itemIndex, int containerUid) {
        super(opcode, itemId, itemIndex, containerUid);
    }

    public static int actionIndexOpcode(int index) {
        if (index < 0 || index > 4) return -1;
        return ActionOpcodes.ITEM_ACTION_0 + index;
    }

    public static boolean isInstance(int op) {
        return op >= ActionOpcodes.ITEM_ACTION_0
                && op <= ActionOpcodes.ITEM_ACTION_4;
    }

    public int actionIndex() {
        return opcode - ActionOpcodes.ITEM_ACTION_0;
    }

    /*  public Widget getContainer() {
          final int parent = parent();
          final int child = child();
          return Widget.get(parent, child);
      }

      public Item getItem() {
          Widget container = getContainer();
          if(container == null) return null;
          return new WidgetItem(container,itemIndex());
      }
  */
    @Override
    public String toString() {
        final int parent = parent();
        final int child = child();
        final int index = itemIndex();
        final int address = tableUid();
        return "ItemAction:[TableAddress(" + address + "," + index + ")=<" + parent + "#" + child + "#" + index +
                "> | ItemId=" + itemId() + " | ItemIndex=" + itemIndex() + " | ActionIndex=" + actionIndex() + "]";
    }
}
