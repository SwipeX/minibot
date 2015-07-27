package com.minibot.ui;

import com.minibot.Minibot;
import com.minibot.api.method.RuneScape;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author root
 * @since 5/15/15.
 */
public class GameMenu {

    private static final JMenuBar menuBar;
    private static final JMenuItem start;
    private static final JMenuItem stop;
    private static final JCheckBoxMenuItem render;
    private static final JCheckBoxMenuItem farm;
    private static final JCheckBoxMenuItem verbose;

    static {
        menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        menuBar.add(file);
        start = new JMenuItem("Start");
        file.add(combine(start, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), e -> {
            MacroSelector macroSelector = new MacroSelector();
            macroSelector.loadMacros();
            macroSelector.setVisible(true);
        }));
        stop = new JMenuItem("Stop");
        stop.setEnabled(false);
        file.add(combine(stop, KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), e -> MacroSelector.halt()));
        JMenu options = new JMenu("Options");
        menuBar.add(options);
        render = new JCheckBoxMenuItem("!Rendering");
        options.add(combine(render, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), e -> {
                    RuneScape.LANDSCAPE_RENDERING_ENABLED = !RuneScape.LANDSCAPE_RENDERING_ENABLED;
                    RuneScape.MODEL_RENDERING_ENABLED = !RuneScape.MODEL_RENDERING_ENABLED;
                    RuneScape.WIDGET_RENDERING_ENABLED = !RuneScape.WIDGET_RENDERING_ENABLED;
                }
        ));
        farm = new JCheckBoxMenuItem("Farming");
        options.add(combine(farm, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK),
                e -> Minibot.instance().setFarming(!Minibot.instance().farming())));
        verbose = new JCheckBoxMenuItem("Verbose");
        options.add(combine(verbose, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK),
                e -> Minibot.instance().setVerbose(!Minibot.instance().verbose())));
    }

    public static JMenuItem start() {
        return start;
    }

    public static void setEnabled() {
        start.setEnabled(!start.isEnabled());
        stop.setEnabled(!start.isEnabled());
    }

    public static void setRender() {
        render.setState(!render.getState());
    }

    public static void setFarm() {
        farm.setState(!farm.getState());
    }

    public static void setVerbose() {
        verbose.setState(!verbose.getState());
    }

    public static JMenuItem combine(JMenuItem button, KeyStroke accelerator, ActionListener listener) {
        button.addActionListener(listener);
        if (accelerator != null) {
            button.setAccelerator(accelerator);
        }
        return button;
    }

    public static Component component() {
        return menuBar;
    }
}