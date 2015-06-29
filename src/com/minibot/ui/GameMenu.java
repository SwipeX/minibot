package com.minibot.ui;

import com.minibot.Minibot;
import com.minibot.api.method.RuneScape;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.Component;
import java.awt.event.ActionListener;

/**
 * @author root
 * @since 5/15/15.
 */
public class GameMenu {

    private static final JMenuBar menuBar;
    private static final JMenuItem start;
    private static final JMenuItem stop;

    static {
        menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        menuBar.add(file);
        start = new JMenuItem("Start");
        file.add(combine(start, e -> {
            MacroSelector macroSelector = new MacroSelector();
            macroSelector.loadMacros();
            macroSelector.setVisible(true);
        }));
        stop = new JMenuItem("Stop");
        stop.setEnabled(false);
        file.add(combine(stop, e -> MacroSelector.halt()));
        JMenu options = new JMenu("Options");
        menuBar.add(options);
        options.add(combine(new JMenuItem("Rendering"), e -> {
                    RuneScape.LANDSCAPE_RENDERING_ENABLED = !RuneScape.LANDSCAPE_RENDERING_ENABLED;
                    RuneScape.MODEL_RENDERING_ENABLED = !RuneScape.MODEL_RENDERING_ENABLED;
                    RuneScape.WIDGET_RENDERING_ENABLED = !RuneScape.WIDGET_RENDERING_ENABLED;
                }
        ));
        options.add(combine(new JMenuItem("Farming"), e -> Minibot.instance().setFarming(!Minibot.instance().isFarming())));
    }

    public static JMenuItem start() {
        return start;
    }

    public static void setEnabled(boolean enabled) {
        start.setEnabled(enabled);
        stop.setEnabled(!enabled);
    }

    public static JMenuItem combine(JMenuItem button, ActionListener listener) {
        button.addActionListener(listener);
        return button;
    }

    public static Component component() {
        return menuBar;
    }
}