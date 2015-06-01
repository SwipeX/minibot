package com.minibot.ui;

import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.MacroDefinition;
import com.minibot.bot.macro.Manifest;
import com.minibot.api.util.filter.Filter;

import java.io.IOException;
import java.lang.reflect.Modifier;
 
/**
 * @author Tyler Sedlar
 * @since 4/30/2015
 */
public abstract class MacroLoader<T> implements Filter<Class<?>> {
 
    public abstract void parse(T t) throws IOException, ClassNotFoundException;
 
    public abstract MacroDefinition[] definitions();
 
    @Override
    public boolean accept(Class<?> c) {
        return !Modifier.isAbstract(c.getModifiers()) && Macro.class.isAssignableFrom(c) &&
                c.isAnnotationPresent(Manifest.class);
    }
}