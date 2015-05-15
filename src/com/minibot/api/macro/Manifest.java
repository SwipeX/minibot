package com.minibot.api.macro;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tim on 5/14/15.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Manifest {
    String name();
    String author();
    String version();
    String description();
}