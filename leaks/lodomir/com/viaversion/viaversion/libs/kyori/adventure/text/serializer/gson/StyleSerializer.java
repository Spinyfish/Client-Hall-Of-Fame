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
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.SerializerFactory;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.TextColorWrapper;
import com.viaversion.viaversion.libs.kyori.adventure.util.Codec;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.EnumSet;
import org.jetbrains.annotations.Nullable;

final class StyleSerializer
extends TypeAdapter<Style> {
    private static final TextDecoration[] DECORATIONS = new TextDecoration[]{TextDecoration.BOLD, TextDecoration.ITALIC, TextDecoration.UNDERLINED, TextDecoration.STRIKETHROUGH, TextDecoration.OBFUSCATED};
    static final String FONT = "font";
    static final String COLOR = "color";
    static final String INSERTION = "insertion";
    static final String CLICK_EVENT = "clickEvent";
    static final String CLICK_EVENT_ACTION = "action";
    static final String CLICK_EVENT_VALUE = "value";
    static final String HOVER_EVENT = "hoverEvent";
    static final String HOVER_EVENT_ACTION = "action";
    static final String HOVER_EVENT_CONTENTS = "contents";
    @Deprecated
    static final String HOVER_EVENT_VALUE = "value";
    private final LegacyHoverEventSerializer legacyHover;
    private final boolean emitLegacyHover;
    private final Gson gson;

    static TypeAdapter<Style> create(@Nullable LegacyHoverEventSerializer legacyHover, boolean emitLegacyHover, Gson gson) {
        return new StyleSerializer(legacyHover, emitLegacyHover, gson).nullSafe();
    }

    private StyleSerializer(@Nullable LegacyHoverEventSerializer legacyHover, boolean emitLegacyHover, Gson gson) {
        this.legacyHover = legacyHover;
        this.emitLegacyHover = emitLegacyHover;
        this.gson = gson;
    }

    @Override
    public Style read(JsonReader in) throws IOException {
        in.beginObject();
        Style.Builder style = Style.style();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equals(FONT)) {
                style.font((Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE));
                continue;
            }
            if (fieldName.equals(COLOR)) {
                TextColorWrapper color = (TextColorWrapper)this.gson.fromJson(in, SerializerFactory.COLOR_WRAPPER_TYPE);
                if (color.color != null) {
                    style.color(color.color);
                    continue;
                }
                if (color.decoration == null) continue;
                style.decoration(color.decoration, TextDecoration.State.TRUE);
                continue;
            }
            if (TextDecoration.NAMES.keys().contains(fieldName)) {
                style.decoration(TextDecoration.NAMES.value(fieldName), this.readBoolean(in));
                continue;
            }
            if (fieldName.equals(INSERTION)) {
                style.insertion(in.nextString());
                continue;
            }
            if (fieldName.equals(CLICK_EVENT)) {
                in.beginObject();
                ClickEvent.Action action = null;
                String value = null;
                while (in.hasNext()) {
                    String clickEventField = in.nextName();
                    if (clickEventField.equals("action")) {
                        action = (ClickEvent.Action)((Object)this.gson.fromJson(in, SerializerFactory.CLICK_ACTION_TYPE));
                        continue;
                    }
                    if (clickEventField.equals("value")) {
                        value = in.peek() == JsonToken.NULL ? null : in.nextString();
                        continue;
                    }
                    in.skipValue();
                }
                if (action != null && action.readable() && value != null) {
                    style.clickEvent(ClickEvent.clickEvent(action, value));
                }
                in.endObject();
                continue;
            }
            if (fieldName.equals(HOVER_EVENT)) {
                Object value;
                Object rawValue;
                HoverEvent.Action action;
                JsonPrimitive serializedAction;
                JsonObject hoverEventObject = (JsonObject)this.gson.fromJson(in, (Type)((Object)JsonObject.class));
                if (hoverEventObject == null || (serializedAction = hoverEventObject.getAsJsonPrimitive("action")) == null || !(action = this.gson.fromJson((JsonElement)serializedAction, SerializerFactory.HOVER_ACTION_TYPE)).readable()) continue;
                if (hoverEventObject.has(HOVER_EVENT_CONTENTS)) {
                    rawValue = hoverEventObject.get(HOVER_EVENT_CONTENTS);
                    Class actionType = action.type();
                    value = SerializerFactory.COMPONENT_TYPE.isAssignableFrom(actionType) ? this.gson.fromJson((JsonElement)rawValue, SerializerFactory.COMPONENT_TYPE) : (SerializerFactory.SHOW_ITEM_TYPE.isAssignableFrom(actionType) ? this.gson.fromJson((JsonElement)rawValue, SerializerFactory.SHOW_ITEM_TYPE) : (SerializerFactory.SHOW_ENTITY_TYPE.isAssignableFrom(actionType) ? this.gson.fromJson((JsonElement)rawValue, SerializerFactory.SHOW_ENTITY_TYPE) : null));
                } else if (hoverEventObject.has("value")) {
                    rawValue = this.gson.fromJson(hoverEventObject.get("value"), SerializerFactory.COMPONENT_TYPE);
                    value = this.legacyHoverEventContents(action, (Component)rawValue);
                } else {
                    value = null;
                }
                if (value == null) continue;
                style.hoverEvent(HoverEvent.hoverEvent(action, value));
                continue;
            }
            in.skipValue();
        }
        in.endObject();
        return style.build();
    }

    private boolean readBoolean(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.BOOLEAN) {
            return in.nextBoolean();
        }
        if (peek == JsonToken.STRING || peek == JsonToken.NUMBER) {
            return Boolean.parseBoolean(in.nextString());
        }
        throw new JsonParseException("Token of type " + (Object)((Object)peek) + " cannot be interpreted as a boolean");
    }

    private Object legacyHoverEventContents(HoverEvent.Action<?> action, Component rawValue) {
        if (action == HoverEvent.Action.SHOW_TEXT) {
            return rawValue;
        }
        if (this.legacyHover != null) {
            try {
                if (action == HoverEvent.Action.SHOW_ENTITY) {
                    return this.legacyHover.deserializeShowEntity(rawValue, this.decoder());
                }
                if (action == HoverEvent.Action.SHOW_ITEM) {
                    return this.legacyHover.deserializeShowItem(rawValue);
                }
            }
            catch (IOException ex) {
                throw new JsonParseException(ex);
            }
        }
        throw new UnsupportedOperationException();
    }

    private Codec.Decoder<Component, String, JsonParseException> decoder() {
        return string -> this.gson.fromJson((String)string, SerializerFactory.COMPONENT_TYPE);
    }

    private Codec.Encoder<Component, String, JsonParseException> encoder() {
        return component -> this.gson.toJson(component, SerializerFactory.COMPONENT_TYPE);
    }

    @Override
    public void write(JsonWriter out, Style value) throws IOException {
        Key font;
        HoverEvent<?> hoverEvent;
        ClickEvent clickEvent;
        String insertion;
        out.beginObject();
        for (TextDecoration decoration : DECORATIONS) {
            TextDecoration.State state = value.decoration(decoration);
            if (state == TextDecoration.State.NOT_SET) continue;
            String name = TextDecoration.NAMES.key(decoration);
            assert (name != null);
            out.name(name);
            out.value(state == TextDecoration.State.TRUE);
        }
        @Nullable TextColor color = value.color();
        if (color != null) {
            out.name(COLOR);
            this.gson.toJson((Object)color, SerializerFactory.COLOR_TYPE, out);
        }
        if ((insertion = value.insertion()) != null) {
            out.name(INSERTION);
            out.value(insertion);
        }
        if ((clickEvent = value.clickEvent()) != null) {
            out.name(CLICK_EVENT);
            out.beginObject();
            out.name("action");
            this.gson.toJson((Object)clickEvent.action(), SerializerFactory.CLICK_ACTION_TYPE, out);
            out.name("value");
            out.value(clickEvent.value());
            out.endObject();
        }
        if ((hoverEvent = value.hoverEvent()) != null) {
            out.name(HOVER_EVENT);
            out.beginObject();
            out.name("action");
            HoverEvent.Action<?> action = hoverEvent.action();
            this.gson.toJson(action, SerializerFactory.HOVER_ACTION_TYPE, out);
            out.name(HOVER_EVENT_CONTENTS);
            if (action == HoverEvent.Action.SHOW_ITEM) {
                this.gson.toJson(hoverEvent.value(), SerializerFactory.SHOW_ITEM_TYPE, out);
            } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                this.gson.toJson(hoverEvent.value(), SerializerFactory.SHOW_ENTITY_TYPE, out);
            } else if (action == HoverEvent.Action.SHOW_TEXT) {
                this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
            } else {
                throw new JsonParseException("Don't know how to serialize " + hoverEvent.value());
            }
            if (this.emitLegacyHover) {
                out.name("value");
                this.serializeLegacyHoverEvent(hoverEvent, out);
            }
            out.endObject();
        }
        if ((font = value.font()) != null) {
            out.name(FONT);
            this.gson.toJson((Object)font, SerializerFactory.KEY_TYPE, out);
        }
        out.endObject();
    }

    private void serializeLegacyHoverEvent(HoverEvent<?> hoverEvent, JsonWriter out) throws IOException {
        if (hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) {
            this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
        } else if (this.legacyHover != null) {
            Component serialized = null;
            try {
                if (hoverEvent.action() == HoverEvent.Action.SHOW_ENTITY) {
                    serialized = this.legacyHover.serializeShowEntity((HoverEvent.ShowEntity)hoverEvent.value(), this.encoder());
                } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
                    serialized = this.legacyHover.serializeShowItem((HoverEvent.ShowItem)hoverEvent.value());
                }
            }
            catch (IOException ex) {
                throw new JsonSyntaxException(ex);
            }
            if (serialized != null) {
                this.gson.toJson((Object)serialized, SerializerFactory.COMPONENT_TYPE, out);
            } else {
                out.nullValue();
            }
        } else {
            out.nullValue();
        }
    }

    static {
        EnumSet<TextDecoration> knownDecorations = EnumSet.allOf(TextDecoration.class);
        for (TextDecoration decoration : DECORATIONS) {
            knownDecorations.remove(decoration);
        }
        if (!knownDecorations.isEmpty()) {
            throw new IllegalStateException("Gson serializer is missing some text decorations: " + knownDecorations);
        }
    }
}

