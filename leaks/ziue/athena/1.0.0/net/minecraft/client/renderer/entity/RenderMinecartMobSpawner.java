package net.minecraft.client.renderer.entity;

import net.minecraft.entity.ai.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.entity.item.*;

public class RenderMinecartMobSpawner extends RenderMinecart<EntityMinecartMobSpawner>
{
    public RenderMinecartMobSpawner(final RenderManager renderManagerIn) {
        super(renderManagerIn);
    }
    
    @Override
    protected void func_180560_a(final EntityMinecartMobSpawner minecart, final float partialTicks, final IBlockState state) {
        super.func_180560_a(minecart, partialTicks, state);
        if (state.getBlock() == Blocks.mob_spawner) {
            TileEntityMobSpawnerRenderer.renderMob(minecart.func_98039_d(), minecart.posX, minecart.posY, minecart.posZ, partialTicks);
        }
    }
}
