package com.minibot.ui;

import com.minibot.Minibot;
import com.minibot.api.method.RuneScape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        file.add(combine(start, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), e -> {
            MacroSelector macroSelector = new MacroSelector();
            macroSelector.loadMacros();
            macroSelector.setVisible(true);
        }));
        stop = new JMenuItem("Stop");
        stop.setEnabled(false);
        file.add(combine(stop, KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK), e -> MacroSelector.halt()));
        JMenu options = new JMenu("Options");
        menuBar.add(options);
        options.add(combine(new JCheckBoxMenuItem("!Rendering"), KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK), e -> {
                    RuneScape.LANDSCAPE_RENDERING_ENABLED = !RuneScape.LANDSCAPE_RENDERING_ENABLED;
                    RuneScape.MODEL_RENDERING_ENABLED = !RuneScape.MODEL_RENDERING_ENABLED;
                    RuneScape.WIDGET_RENDERING_ENABLED = !RuneScape.WIDGET_RENDERING_ENABLED;
                }
        ));
        options.add(combine(new JCheckBoxMenuItem("Farming"), KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK),
                e -> Minibot.instance().setFarming(!Minibot.instance().isFarming())));
    }

    public static JMenuItem start() {
        return start;
    }

    public static void setEnabled(boolean enabled) {
        start.setEnabled(enabled);
        stop.setEnabled(!enabled);
    }

    public static JMenuItem combine(JMenuItem button, KeyStroke accelerator, ActionListener listener) {
        button.addActionListener(listener);
        if (accelerator != null)
            button.setAccelerator(accelerator);
        return button;
    }

    public static Component component() {
        return menuBar;
    }
}