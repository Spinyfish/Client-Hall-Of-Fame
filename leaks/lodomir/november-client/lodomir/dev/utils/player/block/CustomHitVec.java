/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.player.block;

public class CustomHitVec {
    private final float x;
    private final float y;
    private final float z;
    private final float scale;

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getScale() {
        return this.scale;
    }

    public CustomHitVec(float x, float y, float z, float scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
    }
}

