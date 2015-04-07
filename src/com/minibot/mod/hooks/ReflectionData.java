package com.minibot.mod.hooks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ReflectionData {

    public String className();
}
