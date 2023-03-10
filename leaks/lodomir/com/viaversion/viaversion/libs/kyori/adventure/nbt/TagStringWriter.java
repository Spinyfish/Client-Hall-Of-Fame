/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ByteArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ByteArrayBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ByteBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.DoubleBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.FloatBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntArrayBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IntBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ListBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongArrayBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongArrayBinaryTagImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.LongBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.ShortBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.StringBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.Tokens;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

final class TagStringWriter
implements AutoCloseable {
    private final Appendable out;
    private final String indent;
    private int level;
    private boolean needsSeparator;
    private boolean legacy;

    TagStringWriter(Appendable out, String indent) {
        this.out = out;
        this.indent = indent;
    }

    public TagStringWriter legacy(boolean legacy) {
        this.legacy = legacy;
        return this;
    }

    public TagStringWriter writeTag(BinaryTag tag) throws IOException {
        BinaryTagType<? extends BinaryTag> type = tag.type();
        if (type == BinaryTagTypes.COMPOUND) {
            return this.writeCompound((CompoundBinaryTag)tag);
        }
        if (type == BinaryTagTypes.LIST) {
            return this.writeList((ListBinaryTag)tag);
        }
        if (type == BinaryTagTypes.BYTE_ARRAY) {
            return this.writeByteArray((ByteArrayBinaryTag)tag);
        }
        if (type == BinaryTagTypes.INT_ARRAY) {
            return this.writeIntArray((IntArrayBinaryTag)tag);
        }
        if (type == BinaryTagTypes.LONG_ARRAY) {
            return this.writeLongArray((LongArrayBinaryTag)tag);
        }
        if (type == BinaryTagTypes.STRING) {
            return this.value(((StringBinaryTag)tag).value(), '\u0000');
        }
        if (type == BinaryTagTypes.BYTE) {
            return this.value(Byte.toString(((ByteBinaryTag)tag).value()), 'b');
        }
        if (type == BinaryTagTypes.SHORT) {
            return this.value(Short.toString(((ShortBinaryTag)tag).value()), 's');
        }
        if (type == BinaryTagTypes.INT) {
            return this.value(Integer.toString(((IntBinaryTag)tag).value()), 'i');
        }
        if (type == BinaryTagTypes.LONG) {
            return this.value(Long.toString(((LongBinaryTag)tag).value()), Character.toUpperCase('l'));
        }
        if (type == BinaryTagTypes.FLOAT) {
            return this.value(Float.toString(((FloatBinaryTag)tag).value()), 'f');
        }
        if (type == BinaryTagTypes.DOUBLE) {
            return this.value(Double.toString(((DoubleBinaryTag)tag).value()), 'd');
        }
        throw new IOException("Unknown tag type: " + type);
    }

    private TagStringWriter writeCompound(CompoundBinaryTag tag) throws IOException {
        this.beginCompound();
        for (Map.Entry entry : tag) {
            this.key((String)entry.getKey());
            this.writeTag((BinaryTag)entry.getValue());
        }
        this.endCompound();
        return this;
    }

    private TagStringWriter writeList(ListBinaryTag tag) throws IOException {
        this.beginList();
        int idx = 0;
        boolean lineBreaks = this.prettyPrinting() && this.breakListElement(tag.elementType());
        for (BinaryTag el : tag) {
            this.printAndResetSeparator(!lineBreaks);
            if (lineBreaks) {
                this.newlineIndent();
            }
            if (this.legacy) {
                this.out.append(String.valueOf(idx++));
                this.appendSeparator(':');
            }
            this.writeTag(el);
        }
        this.endList(lineBreaks);
        return this;
    }

    private TagStringWriter writeByteArray(ByteArrayBinaryTag tag) throws IOException {
        if (this.legacy) {
            throw new IOException("Legacy Mojangson only supports integer arrays!");
        }
        this.beginArray('b');
        char byteArrayType = Character.toUpperCase('b');
        byte[] value = ByteArrayBinaryTagImpl.value(tag);
        int length = value.length;
        for (int i = 0; i < length; ++i) {
            this.printAndResetSeparator(true);
            this.value(Byte.toString(value[i]), byteArrayType);
        }
        this.endArray();
        return this;
    }

    private TagStringWriter writeIntArray(IntArrayBinaryTag tag) throws IOException {
        if (this.legacy) {
            this.beginList();
        } else {
            this.beginArray('i');
        }
        int[] value = IntArrayBinaryTagImpl.value(tag);
        int length = value.length;
        for (int i = 0; i < length; ++i) {
            this.printAndResetSeparator(true);
            this.value(Integer.toString(value[i]), 'i');
        }
        this.endArray();
        return this;
    }

    private TagStringWriter writeLongArray(LongArrayBinaryTag tag) throws IOException {
        if (this.legacy) {
            throw new IOException("Legacy Mojangson only supports integer arrays!");
        }
        this.beginArray('l');
        long[] value = LongArrayBinaryTagImpl.value(tag);
        int length = value.length;
        for (int i = 0; i < length; ++i) {
            this.printAndResetSeparator(true);
            this.value(Long.toString(value[i]), 'l');
        }
        this.endArray();
        return this;
    }

    public TagStringWriter beginCompound() throws IOException {
        this.printAndResetSeparator(false);
        ++this.level;
        this.out.append('{');
        return this;
    }

    public TagStringWriter endCompound() throws IOException {
        --this.level;
        this.newlineIndent();
        this.out.append('}');
        this.needsSeparator = true;
        return this;
    }

    public TagStringWriter key(String key) throws IOException {
        this.printAndResetSeparator(false);
        this.newlineIndent();
        this.writeMaybeQuoted(key, false);
        this.appendSeparator(':');
        return this;
    }

    public TagStringWriter value(String value, char valueType) throws IOException {
        if (valueType == '\u0000') {
            this.writeMaybeQuoted(value, true);
        } else {
            this.out.append(value);
            if (valueType != 'i') {
                this.out.append(valueType);
            }
        }
        this.needsSeparator = true;
        return this;
    }

    public TagStringWriter beginList() throws IOException {
        this.printAndResetSeparator(false);
        ++this.level;
        this.out.append('[');
        return this;
    }

    public TagStringWriter endList(boolean lineBreak) throws IOException {
        --this.level;
        if (lineBreak) {
            this.newlineIndent();
        }
        this.out.append(']');
        this.needsSeparator = true;
        return this;
    }

    private TagStringWriter beginArray(char type) throws IOException {
        this.beginList().out.append(Character.toUpperCase(type)).append(';');
        if (this.prettyPrinting()) {
            this.out.append(' ');
        }
        return this;
    }

    private TagStringWriter endArray() throws IOException {
        return this.endList(false);
    }

    private void writeMaybeQuoted(String content, boolean requireQuotes) throws IOException {
        if (!requireQuotes) {
            for (int i = 0; i < content.length(); ++i) {
                if (Tokens.id(content.charAt(i))) continue;
                requireQuotes = true;
                break;
            }
        }
        if (requireQuotes) {
            this.out.append('\"');
            this.out.append(TagStringWriter.escape(content, '\"'));
            this.out.append('\"');
        } else {
            this.out.append(content);
        }
    }

    private static String escape(String content, char quoteChar) {
        StringBuilder output = new StringBuilder(content.length());
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            if (c == quoteChar || c == '\\') {
                output.append('\\');
            }
            output.append(c);
        }
        return output.toString();
    }

    private void printAndResetSeparator(boolean pad) throws IOException {
        if (this.needsSeparator) {
            this.out.append(',');
            if (pad && this.prettyPrinting()) {
                this.out.append(' ');
            }
            this.needsSeparator = false;
        }
    }

    private boolean breakListElement(BinaryTagType<?> type) {
        return type == BinaryTagTypes.COMPOUND || type == BinaryTagTypes.LIST || type == BinaryTagTypes.BYTE_ARRAY || type == BinaryTagTypes.INT_ARRAY || type == BinaryTagTypes.LONG_ARRAY;
    }

    private boolean prettyPrinting() {
        return this.indent.length() > 0;
    }

    private void newlineIndent() throws IOException {
        if (this.prettyPrinting()) {
            this.out.append(Tokens.NEWLINE);
            for (int i = 0; i < this.level; ++i) {
                this.out.append(this.indent);
            }
        }
    }

    private Appendable appendSeparator(char separatorChar) throws IOException {
        this.out.append(separatorChar);
        if (this.prettyPrinting()) {
            this.out.append(' ');
        }
        return this.out;
    }

    @Override
    public void close() throws IOException {
        if (this.level != 0) {
            throw new IllegalStateException("Document finished with unbalanced start and end objects");
        }
        if (this.out instanceof Writer) {
            ((Writer)this.out).flush();
        }
    }
}

