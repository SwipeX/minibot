package com.minibot.bot.macro;

/**
 * @author Tyler Sedlar
 * @since 4/30/2015
 */
public class MacroDefinition {

    private final Class<? extends Macro> mainClass;
    private final Manifest manifest;
    private long time;

    public MacroDefinition(Class<? extends Macro> mainClass) {
        this.mainClass = mainClass;
        this.manifest = mainClass.getAnnotation(Manifest.class);
        time = System.currentTimeMillis();
    }

    public Class<? extends Macro> mainClass() {
        return mainClass;
    }

    public Manifest manifest() {
        return manifest;
    }

    public boolean equals(Object o) {
        if (o instanceof MacroDefinition) {
            return (((MacroDefinition) o).time == time) && mainClass().hashCode() == ((MacroDefinition) o).mainClass().hashCode();
        }
        return false;
    }
}