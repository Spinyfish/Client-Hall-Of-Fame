package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block {
    public static final PropertyBool EXPLODE = PropertyBool.create("explode");

    public BlockTNT() {
        super(Material.tnt);
        this.setDefaultState(this.blockState.getBaseState().withProperty(EXPLODE, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);

        if (worldIn.isBlockPowered(pos)) {
            this.onBlockDestroyedByPlayer(worldIn, pos, state.withProperty(EXPLODE, Boolean.valueOf(true)));
            worldIn.setBlockToAir(pos);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (worldIn.isBlockPowered(pos)) {
            this.onBlockDestroyedByPlayer(worldIn, pos, state.withProperty(EXPLODE, Boolean.valueOf(true)));
            worldIn.setBlockToAir(pos);
        }
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    public void onBlockDestroyedByExplosion(final World worldIn, final BlockPos pos, final Explosion explosionIn) {
        if (!worldIn.isRemote) {
            final EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(worldIn, (float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F, explosionIn.getExplosivePlacedBy());
            entitytntprimed.fuse = worldIn.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
            worldIn.spawnEntityInWorld(entitytntprimed);
        }
    }

    /**
     * Called when a player destroys this Block
     */
    public void onBlockDestroyedByPlayer(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.explode(worldIn, pos, state, null);
    }

    public void explode(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase igniter) {
        if (!worldIn.isRemote) {
            if (state.getValue(EXPLODE).booleanValue()) {
                final EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(worldIn, (float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F, igniter);
                worldIn.spawnEntityInWorld(entitytntprimed);
                worldIn.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
            }
        }
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (playerIn.getCurrentEquippedItem() != null) {
            final Item item = playerIn.getCurrentEquippedItem().getItem();

            if (item == Items.flint_and_steel || item == Items.fire_charge) {
                this.explode(worldIn, pos, state.withProperty(EXPLODE, Boolean.valueOf(true)), playerIn);
                worldIn.setBlockToAir(pos);

                if (item == Items.flint_and_steel) {
                    playerIn.getCurrentEquippedItem().damageItem(1, playerIn);
                } else if (!playerIn.capabilities.isCreativeMode) {
                    --playerIn.getCurrentEquippedItem().stackSize;
                }

                return true;
            }
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state, final Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof EntityArrow) {
            final EntityArrow entityarrow = (EntityArrow) entityIn;

            if (entityarrow.isBurning()) {
                this.explode(worldIn, pos, worldIn.getBlockState(pos).withProperty(EXPLODE, Boolean.valueOf(true)), entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) entityarrow.shootingEntity : null);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    /**
     * Return whether this block can drop from an explosion.
     */
    public boolean canDropFromExplosion(final Explosion explosionIn) {
        return false;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(EXPLODE, Boolean.valueOf((meta & 1) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(EXPLODE).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, EXPLODE);
    }
}
