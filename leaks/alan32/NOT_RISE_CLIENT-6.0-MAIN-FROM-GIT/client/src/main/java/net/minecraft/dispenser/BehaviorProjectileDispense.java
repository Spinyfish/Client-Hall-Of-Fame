package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack dispenseStack(final IBlockSource source, final ItemStack stack) {
        final World world = source.getWorld();
        final IPosition iposition = BlockDispenser.getDispensePosition(source);
        final EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
        final IProjectile iprojectile = this.getProjectileEntity(world, iposition);
        iprojectile.setThrowableHeading(enumfacing.getFrontOffsetX(), (float) enumfacing.getFrontOffsetY() + 0.1F, enumfacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
        world.spawnEntityInWorld((Entity) iprojectile);
        stack.splitStack(1);
        return stack;
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(final IBlockSource source) {
        source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
    }

    /**
     * Return the projectile entity spawned by this dispense behavior.
     */
    protected abstract IProjectile getProjectileEntity(World worldIn, IPosition position);

    protected float func_82498_a() {
        return 6.0F;
    }

    protected float func_82500_b() {
        return 1.1F;
    }
}
