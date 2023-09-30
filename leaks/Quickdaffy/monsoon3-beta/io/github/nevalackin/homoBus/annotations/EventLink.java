/*
 * Decompiled with CFR 0.152.
 */
package io.github.nevalackin.homoBus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface EventLink {
    public byte value() default 2;
}

