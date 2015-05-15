package com.minibot.api.macro;

/**
 * @author Tyler Sedlar
 * @since 4/30/2015
 */
public class MacroDefinition {
 
    private final Class<? extends Macro> mainClass;
    private final Manifest manifest;
 
    public MacroDefinition(Class<? extends Macro> mainClass) {
        this.mainClass = mainClass;
        this.manifest = mainClass.getAnnotation(Manifest.class);
    }
 
    public Class<? extends Macro> mainClass() {
        return mainClass;
    }
 
    public Manifest manifest() {
        return manifest;
    }
 
}