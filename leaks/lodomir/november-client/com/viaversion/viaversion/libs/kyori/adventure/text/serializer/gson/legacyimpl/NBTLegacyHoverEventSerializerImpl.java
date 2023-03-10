/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.legacyimpl;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.TagStringIO;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.api.BinaryTagHolder;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.util.Codec;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NBTLegacyHoverEventSerializerImpl
implements LegacyHoverEventSerializer {
    static final NBTLegacyHoverEventSerializerImpl INSTANCE = new NBTLegacyHoverEventSerializerImpl();
    private static final TagStringIO SNBT_IO = TagStringIO.get();
    private static final Codec<CompoundBinaryTag, String, IOException, IOException> SNBT_CODEC = Codec.of(SNBT_IO::asCompound, SNBT_IO::asString);
    static final String ITEM_TYPE = "id";
    static final String ITEM_COUNT = "Count";
    static final String ITEM_TAG = "tag";
    static final String ENTITY_NAME = "name";
    static final String ENTITY_TYPE = "type";
    static final String ENTITY_ID = "id";

    private NBTLegacyHoverEventSerializerImpl() {
    }

    @Override
    public @NotNull HoverEvent.ShowItem deserializeShowItem(@NotNull Component input) throws IOException {
        NBTLegacyHoverEventSerializerImpl.assertTextComponent(input);
        CompoundBinaryTag contents = SNBT_CODEC.decode(((TextComponent)input).content());
        CompoundBinaryTag tag = contents.getCompound(ITEM_TAG);
        return HoverEvent.ShowItem.of(Key.key(contents.getString("id")), (int)contents.getByte(ITEM_COUNT, (byte)1), tag == CompoundBinaryTag.empty() ? null : BinaryTagHolder.encode(tag, SNBT_CODEC));
    }

    @Override
    public @NotNull HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component input, Codec.Decoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
        NBTLegacyHoverEventSerializerImpl.assertTextComponent(input);
        CompoundBinaryTag contents = SNBT_CODEC.decode(((TextComponent)input).content());
        return HoverEvent.ShowEntity.of(Key.key(contents.getString(ENTITY_TYPE)), UUID.fromString(contents.getString("id")), componentCodec.decode(contents.getString(ENTITY_NAME)));
    }

    private static void assertTextComponent(Component component) {
        if (!(component instanceof TextComponent) || !component.children().isEmpty()) {
            throw new IllegalArgumentException("Legacy events must be single Component instances");
        }
    }

    @Override
    @NotNull
    public Component serializeShowItem(@NotNull HoverEvent.ShowItem input) throws IOException {
        CompoundBinaryTag.Builder builder = (CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("id", input.item().asString())).putByte(ITEM_COUNT, (byte)input.count());
        @Nullable BinaryTagHolder nbt = input.nbt();
        if (nbt != null) {
            builder.put(ITEM_TAG, nbt.get(SNBT_CODEC));
        }
        return Component.text(SNBT_CODEC.encode(builder.build()));
    }

    @Override
    @NotNull
    public Component serializeShowEntity(@NotNull HoverEvent.ShowEntity input, Codec.Encoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
        CompoundBinaryTag.Builder builder = (CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("id", input.id().toString())).putString(ENTITY_TYPE, input.type().asString());
        @Nullable Component name = input.name();
        if (name != null) {
            builder.putString(ENTITY_NAME, componentCodec.encode(name));
        }
        return Component.text(SNBT_CODEC.encode(builder.build()));
    }
}

