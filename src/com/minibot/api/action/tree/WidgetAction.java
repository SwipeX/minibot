package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Widgets;

public class WidgetAction extends Action {

    public WidgetAction(int opcode, int actionIndex, int widgetIndex, int widgetId) {
        super(opcode, actionIndex, widgetIndex, widgetId);
    }

    public WidgetAction(boolean type2, int actionIndex, int widgetIndex, int widgetId) {
        super(type2 ? ActionOpcodes.WIDGET_ACTION_2 : ActionOpcodes.WIDGET_ACTION,
                actionIndex, widgetIndex, widgetId);
    }

    public static boolean isInstance(int opcode) {
        opcode = Action.pruneOpcode(opcode);
        return opcode == ActionOpcodes.WIDGET_ACTION || opcode == ActionOpcodes.WIDGET_ACTION_2;
    }

    @Override
    public int significantArgs() {
        return SIG_ALL;
    }

    // Arg0 is automatically lowered by one when is derived from the client, when its a widgetAction
    public int actionIndex() {
        return arg0;
    }

    public int widgetIndex() {
        return arg1;
    }

    public int widgetUid() {
        return arg2;
    }


    public int parent() {
        return Widgets.findParentIndex(widgetUid());
    }

    public int child() {
        return Widgets.findChildIndex(widgetUid());
    }

    public boolean isType2() {
        return opcode == ActionOpcodes.WIDGET_ACTION_2;
    }

   /* public Widget get() {
        final int UID = getParentUID();
        final int parent0 = Interface.getParentIndex(UID);
        final int child0  = Interface.getChildIndex(UID);
        final int index0  = widgetIndex();
        RSClient client = Game.getClient();
        RSInterface parent = client.getInterfaces()[parent0][child0];
        if(parent == null) return null;
        RSInterface[] children = parent.getChildren();
        if(children != null && index0 > 0 && index0 < children.length)
            return children[index0];
        return parent;
    }*/


    @Override
    public boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0 && this.arg1 == arg1 && this.arg2 == arg2;
    }

    @Override
    public String toString() {
        final int UID = widgetUid();
        final int parent = Widgets.findParentIndex(UID);
        final int child = Widgets.findChildIndex(UID);
        final int index = widgetIndex();
        final int action = actionIndex();
        final int type = isType2() ? 2 : 1;
        return "WidgetAction:[Address=<" + parent + "#" + child + "#" + index + "> | ActonIndex=" + action +
                " | ActionType=" + type + "]" /*+ get()*/;
    }
}