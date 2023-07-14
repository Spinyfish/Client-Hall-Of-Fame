/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.vecmath.Matrix4f
 */
package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;
import net.minecraft.util.EnumFacing;

public interface ITransformation {
    public Matrix4f getMatrix();

    public EnumFacing rotate(EnumFacing var1);

    public int rotate(EnumFacing var1, int var2);
}

