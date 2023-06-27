package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.util.*;
import net.minecraft.block.material.*;

public abstract class BlockRotatedPillar extends Block
{
    public static final PropertyEnum<EnumFacing.Axis> AXIS;
    
    protected BlockRotatedPillar(final Material materialIn) {
        super(materialIn, materialIn.getMaterialMapColor());
    }
    
    protected BlockRotatedPillar(final Material p_i46385_1_, final MapColor p_i46385_2_) {
        super(p_i46385_1_, p_i46385_2_);
    }
    
    static {
        AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    }
}
