/*
 * Decompiled with CFR 0.152.
 */
package dev.quickprotect;

import dev.quickprotect.Strategy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Inclusion {
    public Strategy[] value() default {Strategy.NO_STRATEGY};
}

