package com.minibot.ui;

import com.minibot.Minibot;
import com.minibot.api.method.RuneScape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by root on 5/15/15.
 */
public class GameMenu {
    private static JMenuBar menuBar;
    private static MacroSelector macroSelector;

    static {
        macroSelector = new MacroSelector();
        menuBar = new JMenuBar();
        menuBar.add(combine(new JButton("Start"), e -> {
            macroSelector.loadMacros();
            macroSelector.setVisible(true);
        }));
        menuBar.add(combine(new JButton("Stop"), e -> MacroSelector.halt()));
        menuBar.add(combine(new JButton("Rendering"), e -> {
                    RuneScape.LANDSCAPE_RENDERING_ENABLED = !RuneScape.LANDSCAPE_RENDERING_ENABLED;
                    RuneScape.MODEL_RENDERING_ENABLED = !RuneScape.MODEL_RENDERING_ENABLED;
                }
        ));
        menuBar.add(combine(new JButton("Farming"), e -> Minibot.instance().setFarming(!Minibot.instance().isFarming())));
    }

    public static JButton combine(JButton button, ActionListener listener) {
        button.addActionListener(listener);
        return button;
    }

    public static Component component() {
        return menuBar;
    }
}
