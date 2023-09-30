/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import wtf.monsoon.api.setting.Bind;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface DefaultBind {
    public int code();

    public Bind.Device device();
}

