package net.minecraftforge.client.model;

import org.lwjgl.util.vector.*;
import net.minecraft.util.*;

public interface ITransformation
{
    Matrix4f getMatrix();
    
    EnumFacing rotate(final EnumFacing p0);
    
    int rotate(final EnumFacing p0, final int p1);
}
