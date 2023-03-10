/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponentImpl;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScopedComponent;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.regex.Matcher;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface BlockNBTComponent
extends NBTComponent<BlockNBTComponent, Builder>,
ScopedComponent<BlockNBTComponent> {
    @NotNull
    public Pos pos();

    @Contract(pure=true)
    @NotNull
    public BlockNBTComponent pos(@NotNull Pos var1);

    @Contract(pure=true)
    @NotNull
    default public BlockNBTComponent localPos(double left, double up, double forwards) {
        return this.pos(LocalPos.of(left, up, forwards));
    }

    @Contract(pure=true)
    @NotNull
    default public BlockNBTComponent worldPos(@NotNull WorldPos.Coordinate x, @NotNull WorldPos.Coordinate y, @NotNull WorldPos.Coordinate z) {
        return this.pos(WorldPos.of(x, y, z));
    }

    @Contract(pure=true)
    @NotNull
    default public BlockNBTComponent absoluteWorldPos(int x, int y, int z) {
        return this.worldPos(WorldPos.Coordinate.absolute(x), WorldPos.Coordinate.absolute(y), WorldPos.Coordinate.absolute(z));
    }

    @Contract(pure=true)
    @NotNull
    default public BlockNBTComponent relativeWorldPos(int x, int y, int z) {
        return this.worldPos(WorldPos.Coordinate.relative(x), WorldPos.Coordinate.relative(y), WorldPos.Coordinate.relative(z));
    }

    public static interface WorldPos
    extends Pos {
        @NotNull
        public static WorldPos of(@NotNull Coordinate x, @NotNull Coordinate y, @NotNull Coordinate z) {
            return new BlockNBTComponentImpl.WorldPosImpl(x, y, z);
        }

        @NotNull
        public Coordinate x();

        @NotNull
        public Coordinate y();

        @NotNull
        public Coordinate z();

        public static interface Coordinate
        extends Examinable {
            @NotNull
            public static Coordinate absolute(int value) {
                return Coordinate.of(value, Type.ABSOLUTE);
            }

            @NotNull
            public static Coordinate relative(int value) {
                return Coordinate.of(value, Type.RELATIVE);
            }

            @NotNull
            public static Coordinate of(int value, @NotNull Type type) {
                return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type);
            }

            public int value();

            @NotNull
            public Type type();

            public static enum Type {
                ABSOLUTE,
                RELATIVE;

            }
        }
    }

    public static interface LocalPos
    extends Pos {
        @NotNull
        public static LocalPos of(double left, double up, double forwards) {
            return new BlockNBTComponentImpl.LocalPosImpl(left, up, forwards);
        }

        public double left();

        public double up();

        public double forwards();
    }

    public static interface Pos
    extends Examinable {
        @NotNull
        public static Pos fromString(@NotNull String input) throws IllegalArgumentException {
            Matcher localMatch = BlockNBTComponentImpl.Tokens.LOCAL_PATTERN.matcher(input);
            if (localMatch.matches()) {
                return LocalPos.of(Double.parseDouble(localMatch.group(1)), Double.parseDouble(localMatch.group(3)), Double.parseDouble(localMatch.group(5)));
            }
            Matcher worldMatch = BlockNBTComponentImpl.Tokens.WORLD_PATTERN.matcher(input);
            if (worldMatch.matches()) {
                return WorldPos.of(BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(1), worldMatch.group(2)), BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(3), worldMatch.group(4)), BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(5), worldMatch.group(6)));
            }
            throw new IllegalArgumentException("Cannot convert position specification '" + input + "' into a position");
        }

        @NotNull
        public String asString();
    }

    public static interface Builder
    extends NBTComponentBuilder<BlockNBTComponent, Builder> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder pos(@NotNull Pos var1);

        @Contract(value="_, _, _ -> this")
        @NotNull
        default public Builder localPos(double left, double up, double forwards) {
            return this.pos(LocalPos.of(left, up, forwards));
        }

        @Contract(value="_, _, _ -> this")
        @NotNull
        default public Builder worldPos(@NotNull WorldPos.Coordinate x, @NotNull WorldPos.Coordinate y, @NotNull WorldPos.Coordinate z) {
            return this.pos(WorldPos.of(x, y, z));
        }

        @Contract(value="_, _, _ -> this")
        @NotNull
        default public Builder absoluteWorldPos(int x, int y, int z) {
            return this.worldPos(WorldPos.Coordinate.absolute(x), WorldPos.Coordinate.absolute(y), WorldPos.Coordinate.absolute(z));
        }

        @Contract(value="_, _, _ -> this")
        @NotNull
        default public Builder relativeWorldPos(int x, int y, int z) {
            return this.worldPos(WorldPos.Coordinate.relative(x), WorldPos.Coordinate.relative(y), WorldPos.Coordinate.relative(z));
        }
    }
}

