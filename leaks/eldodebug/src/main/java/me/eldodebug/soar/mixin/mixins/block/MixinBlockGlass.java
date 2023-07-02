package me.eldodebug.soar.mixin.mixins.block;

import org.spongepowered.asm.mixin.Mixin;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.ClearGlassMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

@Mixin(value = {BlockGlass.class, BlockStainedGlass.class})
public class MixinBlockGlass extends Block{
	
    public MixinBlockGlass(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return !Soar.instance.modManager.getModByClass(ClearGlassMod.class).isToggled() && super.shouldSideBeRendered(worldIn, pos, side);
    }
}
