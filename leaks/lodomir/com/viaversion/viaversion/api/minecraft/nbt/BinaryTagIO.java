/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.viaversion.viaversion.api.minecraft.nbt;

import com.viaversion.viaversion.api.minecraft.nbt.CharBuffer;
import com.viaversion.viaversion.api.minecraft.nbt.StringTagParseException;
import com.viaversion.viaversion.api.minecraft.nbt.TagStringReader;
import com.viaversion.viaversion.api.minecraft.nbt.TagStringWriter;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class BinaryTagIO {
    private BinaryTagIO() {
    }

    public static @NonNull CompoundTag readPath(@NonNull Path path) throws IOException {
        return BinaryTagIO.readInputStream(Files.newInputStream(path, new OpenOption[0]));
    }

    public static @NonNull CompoundTag readInputStream(@NonNull InputStream input) throws IOException {
        try (DataInputStream dis = new DataInputStream(input);){
            CompoundTag compoundTag = BinaryTagIO.readDataInput(dis);
            return compoundTag;
        }
    }

    public static @NonNull CompoundTag readCompressedPath(@NonNull Path path) throws IOException {
        return BinaryTagIO.readCompressedInputStream(Files.newInputStream(path, new OpenOption[0]));
    }

    public static @NonNull CompoundTag readCompressedInputStream(@NonNull InputStream input) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(input)));){
            CompoundTag compoundTag = BinaryTagIO.readDataInput(dis);
            return compoundTag;
        }
    }

    public static @NonNull CompoundTag readDataInput(@NonNull DataInput input) throws IOException {
        byte type = input.readByte();
        if (type != 10) {
            throw new IOException(String.format("Expected root tag to be a CompoundTag, was %s", type));
        }
        input.skipBytes(input.readUnsignedShort());
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.read(input);
        return compoundTag;
    }

    public static void writePath(@NonNull CompoundTag tag, @NonNull Path path) throws IOException {
        BinaryTagIO.writeOutputStream(tag, Files.newOutputStream(path, new OpenOption[0]));
    }

    public static void writeOutputStream(@NonNull CompoundTag tag, @NonNull OutputStream output) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(output);){
            BinaryTagIO.writeDataOutput(tag, dos);
        }
    }

    public static void writeCompressedPath(@NonNull CompoundTag tag, @NonNull Path path) throws IOException {
        BinaryTagIO.writeCompressedOutputStream(tag, Files.newOutputStream(path, new OpenOption[0]));
    }

    public static void writeCompressedOutputStream(@NonNull CompoundTag tag, @NonNull OutputStream output) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(output));){
            BinaryTagIO.writeDataOutput(tag, dos);
        }
    }

    public static void writeDataOutput(@NonNull CompoundTag tag, @NonNull DataOutput output) throws IOException {
        output.writeByte(10);
        output.writeUTF("");
        tag.write(output);
    }

    public static @NonNull CompoundTag readString(@NonNull String input) throws IOException {
        try {
            CharBuffer buffer = new CharBuffer(input);
            TagStringReader parser = new TagStringReader(buffer);
            CompoundTag tag = parser.compound();
            if (buffer.skipWhitespace().hasMore()) {
                throw new IOException("Document had trailing content after first CompoundTag");
            }
            return tag;
        }
        catch (StringTagParseException ex) {
            throw new IOException(ex);
        }
    }

    public static @NonNull String writeString(@NonNull CompoundTag tag) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (TagStringWriter emit = new TagStringWriter(sb);){
            emit.writeTag(tag);
        }
        return sb.toString();
    }
}

