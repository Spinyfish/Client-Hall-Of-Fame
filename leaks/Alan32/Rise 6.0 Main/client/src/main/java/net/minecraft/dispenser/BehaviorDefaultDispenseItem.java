package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
    /**
     * Dispenses the specified ItemStack from a dispenser.
     */
    public final ItemStack dispense(final IBlockSource source, final ItemStack stack) {
        final ItemStack itemstack = this.dispenseStack(source, stack);
        this.playDispenseSound(source);
        this.spawnDispenseParticles(source, BlockDispenser.getFacing(source.getBlockMetadata()));
        return itemstack;
    }

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    protected ItemStack dispenseStack(final IBlockSource source, final ItemStack stack) {
        final EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
        final IPosition iposition = BlockDispenser.getDispensePosition(source);
        final ItemStack itemstack = stack.splitStack(1);
        doDispense(source.getWorld(), itemstack, 6, enumfacing, iposition);
        return stack;
    }

    public static void doDispense(final World worldIn, final ItemStack stack, final int speed, final EnumFacing facing, final IPosition position) {
        final double d0 = position.getX();
        double d1 = position.getY();
        final double d2 = position.getZ();

        if (facing.getAxis() == EnumFacing.Axis.Y) {
            d1 = d1 - 0.125D;
        } else {
            d1 = d1 - 0.15625D;
        }

        final EntityItem entityitem = new EntityItem(worldIn, d0, d1, d2, stack);
        final double d3 = worldIn.rand.nextDouble() * 0.1D + 0.2D;
        entityitem.motionX = (double) facing.getFrontOffsetX() * d3;
        entityitem.motionY = 0.20000000298023224D;
        entityitem.motionZ = (double) facing.getFrontOffsetZ() * d3;
        entityitem.motionX += worldIn.rand.nextGaussian() * 0.007499999832361937D * (double) speed;
        entityitem.motionY += worldIn.rand.nextGaussian() * 0.007499999832361937D * (double) speed;
        entityitem.motionZ += worldIn.rand.nextGaussian() * 0.007499999832361937D * (double) speed;
        worldIn.spawnEntityInWorld(entityitem);
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(final IBlockSource source) {
        source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
    }

    /**
     * Order clients to display dispense particles from the specified block and facing.
     */
    protected void spawnDispenseParticles(final IBlockSource source, final EnumFacing facingIn) {
        source.getWorld().playAuxSFX(2000, source.getBlockPos(), this.func_82488_a(facingIn));
    }

    private int func_82488_a(final EnumFacing facingIn) {
        return facingIn.getFrontOffsetX() + 1 + (facingIn.getFrontOffsetZ() + 1) * 3;
    }
}
