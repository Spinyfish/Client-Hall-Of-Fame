/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.flattener.ComponentFlattener;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.ComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializerImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyFormat;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LegacyComponentSerializer
extends ComponentSerializer<Component, TextComponent, String>,
Buildable<LegacyComponentSerializer, Builder> {
    public static final char SECTION_CHAR = '\u00a7';
    public static final char AMPERSAND_CHAR = '&';
    public static final char HEX_CHAR = '#';

    @NotNull
    public static LegacyComponentSerializer legacySection() {
        return LegacyComponentSerializerImpl.Instances.SECTION;
    }

    @NotNull
    public static LegacyComponentSerializer legacyAmpersand() {
        return LegacyComponentSerializerImpl.Instances.AMPERSAND;
    }

    @NotNull
    public static LegacyComponentSerializer legacy(char legacyCharacter) {
        if (legacyCharacter == '\u00a7') {
            return LegacyComponentSerializer.legacySection();
        }
        if (legacyCharacter == '&') {
            return LegacyComponentSerializer.legacyAmpersand();
        }
        return LegacyComponentSerializer.builder().character(legacyCharacter).build();
    }

    @Nullable
    public static LegacyFormat parseChar(char character) {
        return LegacyComponentSerializerImpl.legacyFormat(character);
    }

    @NotNull
    public static Builder builder() {
        return new LegacyComponentSerializerImpl.BuilderImpl();
    }

    @Override
    @NotNull
    public TextComponent deserialize(@NotNull String var1);

    @Override
    @NotNull
    public String serialize(@NotNull Component var1);

    @ApiStatus.Internal
    public static interface Provider {
        @ApiStatus.Internal
        @NotNull
        public LegacyComponentSerializer legacyAmpersand();

        @ApiStatus.Internal
        @NotNull
        public LegacyComponentSerializer legacySection();

        @ApiStatus.Internal
        @NotNull
        public Consumer<Builder> legacy();
    }

    public static interface Builder
    extends Buildable.Builder<LegacyComponentSerializer> {
        @NotNull
        public Builder character(char var1);

        @NotNull
        public Builder hexCharacter(char var1);

        @NotNull
        public Builder extractUrls();

        @NotNull
        public Builder extractUrls(@NotNull Pattern var1);

        @NotNull
        public Builder extractUrls(@Nullable Style var1);

        @NotNull
        public Builder extractUrls(@NotNull Pattern var1, @Nullable Style var2);

        @NotNull
        public Builder hexColors();

        @NotNull
        public Builder useUnusualXRepeatedCharacterHexFormat();

        @NotNull
        public Builder flattener(@NotNull ComponentFlattener var1);

        @Override
        @NotNull
        public LegacyComponentSerializer build();
    }
}

