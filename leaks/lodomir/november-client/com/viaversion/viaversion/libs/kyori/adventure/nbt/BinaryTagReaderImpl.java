/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagIO;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagType;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.IOStreamUtil;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.TrackingDataInput;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

final class BinaryTagReaderImpl
implements BinaryTagIO.Reader {
    private final long maxBytes;
    static final BinaryTagIO.Reader UNLIMITED = new BinaryTagReaderImpl(-1L);
    static final BinaryTagIO.Reader DEFAULT_LIMIT = new BinaryTagReaderImpl(131082L);

    BinaryTagReaderImpl(long maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Override
    @NotNull
    public CompoundBinaryTag read(@NotNull Path path, @NotNull BinaryTagIO.Compression compression) throws IOException {
        try (InputStream is = Files.newInputStream(path, new OpenOption[0]);){
            CompoundBinaryTag compoundBinaryTag = this.read(is, compression);
            return compoundBinaryTag;
        }
    }

    @Override
    @NotNull
    public CompoundBinaryTag read(@NotNull InputStream input, @NotNull BinaryTagIO.Compression compression) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(compression.decompress(IOStreamUtil.closeShield(input))));){
            CompoundBinaryTag compoundBinaryTag = this.read(dis);
            return compoundBinaryTag;
        }
    }

    @Override
    @NotNull
    public CompoundBinaryTag read(@NotNull DataInput input) throws IOException {
        if (!(input instanceof TrackingDataInput)) {
            input = new TrackingDataInput(input, this.maxBytes);
        }
        BinaryTagType<BinaryTag> type = BinaryTagType.of(input.readByte());
        BinaryTagReaderImpl.requireCompound(type);
        input.skipBytes(input.readUnsignedShort());
        return BinaryTagTypes.COMPOUND.read(input);
    }

    @Override
    public  @NotNull Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull Path path, @NotNull BinaryTagIO.Compression compression) throws IOException {
        try (InputStream is = Files.newInputStream(path, new OpenOption[0]);){
            Map.Entry<String, CompoundBinaryTag> entry = this.readNamed(is, compression);
            return entry;
        }
    }

    @Override
    public  @NotNull Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull InputStream input, @NotNull BinaryTagIO.Compression compression) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(compression.decompress(IOStreamUtil.closeShield(input))));){
            Map.Entry<String, CompoundBinaryTag> entry = this.readNamed(dis);
            return entry;
        }
    }

    @Override
    public  @NotNull Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull DataInput input) throws IOException {
        BinaryTagType<BinaryTag> type = BinaryTagType.of(input.readByte());
        BinaryTagReaderImpl.requireCompound(type);
        String name = input.readUTF();
        return new AbstractMap.SimpleImmutableEntry<String, CompoundBinaryTag>(name, BinaryTagTypes.COMPOUND.read(input));
    }

    private static void requireCompound(BinaryTagType<? extends BinaryTag> type) throws IOException {
        if (type != BinaryTagTypes.COMPOUND) {
            throw new IOException(String.format("Expected root tag to be a %s, was %s", BinaryTagTypes.COMPOUND, type));
        }
    }
}

