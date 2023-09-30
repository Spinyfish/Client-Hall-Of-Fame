package net.minecraftforge.client.model;

import net.optifine.util.Matrix4f;
import net.minecraft.util.EnumFacing;

public interface ITransformation
{
    Matrix4f getMatrix();

    EnumFacing rotate(EnumFacing var1);

    int rotate(EnumFacing var1, int var2);
}
