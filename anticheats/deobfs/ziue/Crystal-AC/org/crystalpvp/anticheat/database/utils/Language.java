package org.crystalpvp.anticheat.database.utils;

import org.jetbrains.annotations.NonNls;

public @interface Language {
    @NonNls
    String value();

    @NonNls
    String prefix() default "";

    @NonNls
    String suffix() default "";
}
