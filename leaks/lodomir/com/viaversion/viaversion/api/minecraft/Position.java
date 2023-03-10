/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft;

import com.viaversion.viaversion.api.minecraft.BlockFace;

public class Position {
    private final int x;
    private final int y;
    private final int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Deprecated
    public Position(int x, short y, int z) {
        this(x, (int)y, z);
    }

    @Deprecated
    public Position(Position toCopy) {
        this(toCopy.x(), toCopy.y(), toCopy.z());
    }

    public Position getRelative(BlockFace face) {
        return new Position(this.x + face.modX(), (short)(this.y + face.modY()), this.z + face.modZ());
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int z() {
        return this.z;
    }

    @Deprecated
    public int getX() {
        return this.x;
    }

    @Deprecated
    public int getY() {
        return this.y;
    }

    @Deprecated
    public int getZ() {
        return this.z;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position)o;
        if (this.x != position.x) {
            return false;
        }
        if (this.y != position.y) {
            return false;
        }
        return this.z == position.z;
    }

    public int hashCode() {
        int result = this.x;
        result = 31 * result + this.y;
        result = 31 * result + this.z;
        return result;
    }

    public String toString() {
        return "Position{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }
}

