/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.CharBuffer;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.StringTagParseException;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.TagStringReader;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.TagStringWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public final class TagStringIO {
    private static final TagStringIO INSTANCE = new TagStringIO(new Builder());
    private final boolean acceptLegacy;
    private final boolean emitLegacy;
    private final String indent;

    @NotNull
    public static TagStringIO get() {
        return INSTANCE;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private TagStringIO(@NotNull Builder builder) {
        this.acceptLegacy = builder.acceptLegacy;
        this.emitLegacy = builder.emitLegacy;
        this.indent = builder.indent;
    }

    public CompoundBinaryTag asCompound(String input) throws IOException {
        try {
            CharBuffer buffer = new CharBuffer(input);
            TagStringReader parser = new TagStringReader(buffer);
            parser.legacy(this.acceptLegacy);
            CompoundBinaryTag tag = parser.compound();
            if (buffer.skipWhitespace().hasMore()) {
                throw new IOException("Document had trailing content after first CompoundTag");
            }
            return tag;
        }
        catch (StringTagParseException ex) {
            throw new IOException(ex);
        }
    }

    public String asString(CompoundBinaryTag input) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (TagStringWriter emit = new TagStringWriter(sb, this.indent);){
            emit.legacy(this.emitLegacy);
            emit.writeTag(input);
        }
        return sb.toString();
    }

    public void toWriter(CompoundBinaryTag input, Writer dest) throws IOException {
        try (TagStringWriter emit = new TagStringWriter(dest, this.indent);){
            emit.legacy(this.emitLegacy);
            emit.writeTag(input);
        }
    }

    public static class Builder {
        private boolean acceptLegacy = true;
        private boolean emitLegacy = false;
        private String indent = "";

        Builder() {
        }

        @NotNull
        public Builder indent(int spaces) {
            if (spaces == 0) {
                this.indent = "";
            } else if (this.indent.length() > 0 && this.indent.charAt(0) != ' ' || spaces != this.indent.length()) {
                char[] indent = new char[spaces];
                Arrays.fill(indent, ' ');
                this.indent = String.copyValueOf(indent);
            }
            return this;
        }

        @NotNull
        public Builder indentTab(int tabs) {
            if (tabs == 0) {
                this.indent = "";
            } else if (this.indent.length() > 0 && this.indent.charAt(0) != '\t' || tabs != this.indent.length()) {
                char[] indent = new char[tabs];
                Arrays.fill(indent, '\t');
                this.indent = String.copyValueOf(indent);
            }
            return this;
        }

        @NotNull
        public Builder acceptLegacy(boolean legacy) {
            this.acceptLegacy = legacy;
            return this;
        }

        @NotNull
        public Builder emitLegacy(boolean legacy) {
            this.emitLegacy = legacy;
            return this;
        }

        @NotNull
        public TagStringIO build() {
            return new TagStringIO(this);
        }
    }
}

