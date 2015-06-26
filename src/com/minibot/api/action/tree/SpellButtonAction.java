package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

// Selects a spell
public class SpellButtonAction extends ButtonAction {

    public SpellButtonAction(int widgetUid) {
        super(ActionOpcodes.BUTTON_SPELL, widgetUid);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.BUTTON_SPELL;
    }
}