/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.EntityNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScoreComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.SelectorComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.StorageNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.SerializerFactory;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

final class ComponentSerializerImpl
extends TypeAdapter<Component> {
    static final String TEXT = "text";
    static final String TRANSLATE = "translate";
    static final String TRANSLATE_WITH = "with";
    static final String SCORE = "score";
    static final String SCORE_NAME = "name";
    static final String SCORE_OBJECTIVE = "objective";
    static final String SCORE_VALUE = "value";
    static final String SELECTOR = "selector";
    static final String KEYBIND = "keybind";
    static final String EXTRA = "extra";
    static final String NBT = "nbt";
    static final String NBT_INTERPRET = "interpret";
    static final String NBT_BLOCK = "block";
    static final String NBT_ENTITY = "entity";
    static final String NBT_STORAGE = "storage";
    static final String SEPARATOR = "separator";
    static final Type COMPONENT_LIST_TYPE = new TypeToken<List<Component>>(){}.getType();
    private final Gson gson;

    static TypeAdapter<Component> create(Gson gson) {
        return new ComponentSerializerImpl(gson).nullSafe();
    }

    private ComponentSerializerImpl(Gson gson) {
        this.gson = gson;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public BuildableComponent<?, ?> read(JsonReader in) throws IOException {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder;
        JsonToken token = in.peek();
        if (token == JsonToken.STRING || token == JsonToken.NUMBER || token == JsonToken.BOOLEAN) {
            return Component.text(ComponentSerializerImpl.readString(in));
        }
        if (token == JsonToken.BEGIN_ARRAY) {
            void var3_4;
            Object var3_3 = null;
            in.beginArray();
            while (in.hasNext()) {
                Object child = this.read(in);
                if (var3_4 == null) {
                    Buildable.Builder builder2 = child.toBuilder();
                    continue;
                }
                var3_4.append((Component)child);
            }
            if (var3_4 == null) {
                throw ComponentSerializerImpl.notSureHowToDeserialize(in.getPath());
            }
            in.endArray();
            return var3_4.build();
        }
        if (token != JsonToken.BEGIN_OBJECT) {
            throw ComponentSerializerImpl.notSureHowToDeserialize(in.getPath());
        }
        JsonObject jsonObject = new JsonObject();
        List extra = Collections.emptyList();
        String text = null;
        String translate = null;
        List translateWith = null;
        String scoreName = null;
        String scoreObjective = null;
        String scoreValue = null;
        String selector = null;
        String keybind = null;
        String nbt = null;
        boolean nbtInterpret = false;
        BlockNBTComponent.Pos nbtBlock = null;
        String nbtEntity = null;
        Key nbtStorage = null;
        Object separator = null;
        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equals(TEXT)) {
                text = ComponentSerializerImpl.readString(in);
                continue;
            }
            if (fieldName.equals(TRANSLATE)) {
                translate = in.nextString();
                continue;
            }
            if (fieldName.equals(TRANSLATE_WITH)) {
                translateWith = (List)this.gson.fromJson(in, COMPONENT_LIST_TYPE);
                continue;
            }
            if (fieldName.equals(SCORE)) {
                in.beginObject();
                while (in.hasNext()) {
                    String scoreFieldName = in.nextName();
                    if (scoreFieldName.equals(SCORE_NAME)) {
                        scoreName = in.nextString();
                        continue;
                    }
                    if (scoreFieldName.equals(SCORE_OBJECTIVE)) {
                        scoreObjective = in.nextString();
                        continue;
                    }
                    if (scoreFieldName.equals(SCORE_VALUE)) {
                        scoreValue = in.nextString();
                        continue;
                    }
                    in.skipValue();
                }
                if (scoreName == null || scoreObjective == null) {
                    throw new JsonParseException("A score component requires a name and objective");
                }
                in.endObject();
                continue;
            }
            if (fieldName.equals(SELECTOR)) {
                selector = in.nextString();
                continue;
            }
            if (fieldName.equals(KEYBIND)) {
                keybind = in.nextString();
                continue;
            }
            if (fieldName.equals(NBT)) {
                nbt = in.nextString();
                continue;
            }
            if (fieldName.equals(NBT_INTERPRET)) {
                nbtInterpret = in.nextBoolean();
                continue;
            }
            if (fieldName.equals(NBT_BLOCK)) {
                nbtBlock = (BlockNBTComponent.Pos)this.gson.fromJson(in, SerializerFactory.BLOCK_NBT_POS_TYPE);
                continue;
            }
            if (fieldName.equals(NBT_ENTITY)) {
                nbtEntity = in.nextString();
                continue;
            }
            if (fieldName.equals(NBT_STORAGE)) {
                nbtStorage = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals(EXTRA)) {
                extra = (List)this.gson.fromJson(in, COMPONENT_LIST_TYPE);
                continue;
            }
            if (fieldName.equals(SEPARATOR)) {
                separator = this.read(in);
                continue;
            }
            jsonObject.add(fieldName, (JsonElement)this.gson.fromJson(in, (Type)((Object)JsonElement.class)));
        }
        if (text != null) {
            builder = Component.text().content(text);
        } else if (translate != null) {
            builder = translateWith != null ? Component.translatable().key(translate).args(translateWith) : Component.translatable().key(translate);
        } else if (scoreName != null && scoreObjective != null) {
            builder = scoreValue == null ? Component.score().name(scoreName).objective(scoreObjective) : Component.score().name(scoreName).objective(scoreObjective).value(scoreValue);
        } else if (selector != null) {
            builder = Component.selector().pattern(selector).separator((ComponentLike)separator);
        } else if (keybind != null) {
            builder = Component.keybind().keybind(keybind);
        } else {
            if (nbt == null) throw ComponentSerializerImpl.notSureHowToDeserialize(in.getPath());
            if (nbtBlock != null) {
                builder = ComponentSerializerImpl.nbt(Component.blockNBT(), nbt, nbtInterpret, separator).pos(nbtBlock);
            } else if (nbtEntity != null) {
                builder = ComponentSerializerImpl.nbt(Component.entityNBT(), nbt, nbtInterpret, separator).selector(nbtEntity);
            } else {
                if (nbtStorage == null) throw ComponentSerializerImpl.notSureHowToDeserialize(in.getPath());
                builder = ComponentSerializerImpl.nbt(Component.storageNBT(), nbt, nbtInterpret, separator).storage(nbtStorage);
            }
        }
        builder.style(this.gson.fromJson((JsonElement)jsonObject, SerializerFactory.STYLE_TYPE)).append(extra);
        in.endObject();
        return builder.build();
    }

    private static String readString(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.STRING || peek == JsonToken.NUMBER) {
            return in.nextString();
        }
        if (peek == JsonToken.BOOLEAN) {
            return String.valueOf(in.nextBoolean());
        }
        throw new JsonParseException("Token of type " + (Object)((Object)peek) + " cannot be interpreted as a string");
    }

    private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(B builder, String nbt, boolean interpret, @Nullable Component separator) {
        return builder.nbtPath(nbt).interpret(interpret).separator(separator);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void write(JsonWriter out, Component value) throws IOException {
        JsonElement style;
        out.beginObject();
        if (value.hasStyling() && (style = this.gson.toJsonTree(value.style(), SerializerFactory.STYLE_TYPE)).isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : style.getAsJsonObject().entrySet()) {
                out.name(entry.getKey());
                this.gson.toJson(entry.getValue(), out);
            }
        }
        if (!value.children().isEmpty()) {
            out.name(EXTRA);
            this.gson.toJson(value.children(), COMPONENT_LIST_TYPE, out);
        }
        if (value instanceof TextComponent) {
            out.name(TEXT);
            out.value(((TextComponent)value).content());
        } else if (value instanceof TranslatableComponent) {
            TranslatableComponent translatable = (TranslatableComponent)value;
            out.name(TRANSLATE);
            out.value(translatable.key());
            if (!translatable.args().isEmpty()) {
                out.name(TRANSLATE_WITH);
                this.gson.toJson(translatable.args(), COMPONENT_LIST_TYPE, out);
            }
        } else if (value instanceof ScoreComponent) {
            ScoreComponent score = (ScoreComponent)value;
            out.name(SCORE);
            out.beginObject();
            out.name(SCORE_NAME);
            out.value(score.name());
            out.name(SCORE_OBJECTIVE);
            out.value(score.objective());
            if (score.value() != null) {
                out.name(SCORE_VALUE);
                out.value(score.value());
            }
            out.endObject();
        } else if (value instanceof SelectorComponent) {
            SelectorComponent selector = (SelectorComponent)value;
            out.name(SELECTOR);
            out.value(selector.pattern());
            this.serializeSeparator(out, selector.separator());
        } else if (value instanceof KeybindComponent) {
            out.name(KEYBIND);
            out.value(((KeybindComponent)value).keybind());
        } else {
            if (!(value instanceof NBTComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(value);
            NBTComponent nbt = (NBTComponent)value;
            out.name(NBT);
            out.value(nbt.nbtPath());
            out.name(NBT_INTERPRET);
            out.value(nbt.interpret());
            this.serializeSeparator(out, nbt.separator());
            if (value instanceof BlockNBTComponent) {
                out.name(NBT_BLOCK);
                this.gson.toJson((Object)((BlockNBTComponent)value).pos(), SerializerFactory.BLOCK_NBT_POS_TYPE, out);
            } else if (value instanceof EntityNBTComponent) {
                out.name(NBT_ENTITY);
                out.value(((EntityNBTComponent)value).selector());
            } else {
                if (!(value instanceof StorageNBTComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(value);
                out.name(NBT_STORAGE);
                this.gson.toJson((Object)((StorageNBTComponent)value).storage(), SerializerFactory.KEY_TYPE, out);
            }
        }
        out.endObject();
    }

    private void serializeSeparator(JsonWriter out, @Nullable Component separator) throws IOException {
        if (separator != null) {
            out.name(SEPARATOR);
            this.write(out, separator);
        }
    }

    static JsonParseException notSureHowToDeserialize(Object element) {
        return new JsonParseException("Don't know how to turn " + element + " into a Component");
    }

    private static IllegalArgumentException notSureHowToSerialize(Component component) {
        return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
    }
}

