package com.minibot.ui;

import com.minibot.Minibot;
import com.minibot.api.method.Players;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.MacroDefinition;
import com.minibot.bot.macro.Manifest;
import com.minibot.api.util.Renderable;
import com.minibot.client.GameCanvas;
import com.minibot.util.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * @author Tyler Sedlar
 * @since 4/30/2015
 */
public class MacroSelector extends JDialog {

    private final DefaultTableModel model;
    private final JTable table;

    private static MacroVector selected;

    private static Macro current;

    public static Macro current() {
        return current;
    }

    public static void halt() {
        Minibot.connection().script(1, Players.local().name(), selected.def.manifest().name());
        Minibot.instance().canvas();
        if (current instanceof Renderable)
            GameCanvas.removeRenderable((Renderable) current);
        if (current != null) {
            current.interrupt();
        }
        current = null;
        Minibot.instance().setMacroRunning(false);
    }

    public MacroSelector() {
        setTitle("Macro Selector");
        setResizable(false);
        setModal(true);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                Minibot.instance().setMacroRunning(false);
            }
        });
        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(500, 300));
        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Author");
        model.addColumn("Name");
        model.addColumn("Description");
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder);
                return this;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setRowSelectionAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        TableColumnModel columns = table.getColumnModel();
        columns.getColumn(0).setPreferredWidth(100);
        columns.getColumn(1).setPreferredWidth(100);
        columns.getColumn(2).setPreferredWidth(300);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            selected = (MacroVector) model.getDataVector().get(row);
        });
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(500, 270));
        container.add(scrollpane, BorderLayout.NORTH);
        JButton start = new JButton("Start");
        start.setPreferredSize(new Dimension(500, 30));
        start.addActionListener(e -> {
            if (selected != null) {
                boolean success = true;
                try {
                    Manifest manifest = selected.def.manifest();
                    System.out.println("Started " + manifest.name() + " by " + manifest.author());
                    Minibot.connection().script(0, Players.local().name(), manifest.name());
                    current = selected.def.mainClass().newInstance();
                } catch (Exception err) {
                    err.printStackTrace();
                    success = false;
                }
                if (success) {
                    Minibot.instance().setMacroRunning(true);
                    current().start();
                    Minibot.instance().canvas();
                    if (current() instanceof Renderable)
                        GameCanvas.addRenderable((Renderable) current());
                } else {
                    Minibot.instance().setMacroRunning(false);
                    halt();
                }
                dispose();
            }
        });
        container.add(start, BorderLayout.SOUTH);
        add(container);
        pack();
        setLocationRelativeTo(Minibot.instance().canvas());
    }

    public void loadMacros() {
        model.getDataVector().clear();
        model.fireTableDataChanged();
        LocalMacroLoader loader = new LocalMacroLoader();
        try {
            loader.parse(new File(Configuration.MACROS));
            MacroDefinition[] definitions = loader.definitions();
            for (MacroDefinition def : definitions)
                addMacro(def);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        model.fireTableDataChanged();
    }

    private class MacroVector extends Vector<String> {

        public final MacroDefinition def;

        public MacroVector(MacroDefinition def, String... strings) {
            this.def = def;
            for (String string : strings)
                add(string);
        }
    }

    public void addMacro(MacroDefinition def) {
        Manifest manifest = def.manifest();
        model.addRow(new MacroVector(def, manifest.author(), manifest.name(), manifest.description()));
    }

    public static void main(String... args) {
        new MacroSelector().setVisible(true);
    }
}