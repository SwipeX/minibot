package com.minibot.ui;

import com.minibot.Minibot;
import com.minibot.api.method.RuneScape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author root
 * @since 5/15/15.
 */
public class GameMenu {

    private static final JMenuBar menuBar;

    static {
        menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        menuBar.add(file);
        file.add(combine(new JMenuItem("Start"), e -> EventQueue.invokeLater(() -> {
                    MacroSelector macroSelector = new MacroSelector();
                    macroSelector.loadMacros();
                    macroSelector.setVisible(true);
                })
        ));
        file.add(combine(new JMenuItem("Stop"), e -> MacroSelector.halt()));
        JMenu options = new JMenu("Options");
        menuBar.add(options);
        options.add(combine(new JMenuItem("Rendering"), e -> {
                    RuneScape.LANDSCAPE_RENDERING_ENABLED = !RuneScape.LANDSCAPE_RENDERING_ENABLED;
                    RuneScape.MODEL_RENDERING_ENABLED = !RuneScape.MODEL_RENDERING_ENABLED;
                }
        ));
        options.add(combine(new JMenuItem("Farming"), e -> Minibot.instance().setFarming(!Minibot.instance().isFarming())));
    }

    public static JMenuItem combine(JMenuItem button, ActionListener listener) {
        button.addActionListener(listener);
        return button;
    }

    public static Component component() {
        return menuBar;
    }
}