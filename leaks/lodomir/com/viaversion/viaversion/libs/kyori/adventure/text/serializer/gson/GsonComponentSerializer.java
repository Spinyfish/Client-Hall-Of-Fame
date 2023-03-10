/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.ComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializerImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GsonComponentSerializer
extends ComponentSerializer<Component, Component, String>,
Buildable<GsonComponentSerializer, Builder> {
    @NotNull
    public static GsonComponentSerializer gson() {
        return GsonComponentSerializerImpl.Instances.INSTANCE;
    }

    @NotNull
    public static GsonComponentSerializer colorDownsamplingGson() {
        return GsonComponentSerializerImpl.Instances.LEGACY_INSTANCE;
    }

    public static Builder builder() {
        return new GsonComponentSerializerImpl.BuilderImpl();
    }

    @NotNull
    public Gson serializer();

    @NotNull
    public UnaryOperator<GsonBuilder> populator();

    @NotNull
    public Component deserializeFromTree(@NotNull JsonElement var1);

    @NotNull
    public JsonElement serializeToTree(@NotNull Component var1);

    @ApiStatus.Internal
    public static interface Provider {
        @ApiStatus.Internal
        @NotNull
        public GsonComponentSerializer gson();

        @ApiStatus.Internal
        @NotNull
        public GsonComponentSerializer gsonLegacy();

        @ApiStatus.Internal
        @NotNull
        public Consumer<Builder> builder();
    }

    public static interface Builder
    extends Buildable.Builder<GsonComponentSerializer> {
        @NotNull
        public Builder downsampleColors();

        @NotNull
        public Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer var1);

        @NotNull
        public Builder emitLegacyHoverEvent();

        @Override
        @NotNull
        public GsonComponentSerializer build();
    }
}

