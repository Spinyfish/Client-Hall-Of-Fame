/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.renderer;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public interface ComponentRenderer<C> {
    @NotNull
    public Component render(@NotNull Component var1, @NotNull C var2);

    default public <T> ComponentRenderer<T> mapContext(Function<T, C> transformer) {
        return (component, ctx) -> this.render(component, transformer.apply(ctx));
    }
}

