package com.minibot.bot.macro;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Tim Dekker
 * @since 5/14/15.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Manifest {

    String name();
    String author();
    String version();
    String description();
}