/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.translation;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import com.viaversion.viaversion.libs.kyori.adventure.translation.GlobalTranslatorImpl;
import com.viaversion.viaversion.libs.kyori.adventure.translation.Translator;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public interface GlobalTranslator
extends Translator,
Examinable {
    @NotNull
    public static GlobalTranslator get() {
        return GlobalTranslatorImpl.INSTANCE;
    }

    @NotNull
    public static TranslatableComponentRenderer<Locale> renderer() {
        return GlobalTranslatorImpl.INSTANCE.renderer;
    }

    @NotNull
    public static Component render(@NotNull Component component, @NotNull Locale locale) {
        return GlobalTranslator.renderer().render(component, locale);
    }

    @NotNull
    public Iterable<? extends Translator> sources();

    public boolean addSource(@NotNull Translator var1);

    public boolean removeSource(@NotNull Translator var1);
}

