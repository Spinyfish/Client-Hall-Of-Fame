package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public abstract class BlockRotatedPillar extends Block {
   public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);

   protected BlockRotatedPillar() {
      super(Material.wood, Material.wood.getMaterialMapColor());
   }

   protected BlockRotatedPillar(MapColor p_i46385_2_) {
      super(Material.grass, p_i46385_2_);
   }
}
