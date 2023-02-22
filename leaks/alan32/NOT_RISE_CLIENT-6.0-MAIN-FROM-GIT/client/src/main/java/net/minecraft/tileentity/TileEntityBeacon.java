package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

import java.util.Arrays;
import java.util.List;

public class TileEntityBeacon extends TileEntityLockable implements ITickable, IInventory {
    /**
     * List of effects that Beacon can apply
     */
    public static final Potion[][] effectsList = new Potion[][]{{Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}};
    private final List<TileEntityBeacon.BeamSegment> beamSegments = Lists.newArrayList();
    private long beamRenderCounter;
    private float field_146014_j;
    private boolean isComplete;

    /**
     * Level of this beacon's pyramid.
     */
    private int levels = -1;

    /**
     * Primary potion effect given by this beacon.
     */
    private int primaryEffect;

    /**
     * Secondary potion effect given by this beacon.
     */
    private int secondaryEffect;

    /**
     * Item given to this beacon as payment.
     */
    private ItemStack payment;
    private String customName;

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        if (this.worldObj.getTotalWorldTime() % 80L == 0L) {
            this.updateBeacon();
        }
    }

    public void updateBeacon() {
        this.updateSegmentColors();
        this.addEffectsToPlayers();
    }

    private void addEffectsToPlayers() {
        if (this.isComplete && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0) {
            final double d0 = this.levels * 10 + 10;
            int i = 0;

            if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
                i = 1;
            }

            final int j = this.pos.getX();
            final int k = this.pos.getY();
            final int l = this.pos.getZ();
            final AxisAlignedBB axisalignedbb = (new AxisAlignedBB(j, k, l, j + 1, k + 1, l + 1)).expand(d0, d0, d0).addCoord(0.0D, this.worldObj.getHeight(), 0.0D);
            final List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

            for (final EntityPlayer entityplayer : list) {
                entityplayer.addPotionEffect(new PotionEffect(this.primaryEffect, 180, i, true, true));
            }

            if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0) {
                for (final EntityPlayer entityplayer1 : list) {
                    entityplayer1.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true, true));
                }
            }
        }
    }

    private void updateSegmentColors() {
        final int i = this.levels;
        final int j = this.pos.getX();
        final int k = this.pos.getY();
        final int l = this.pos.getZ();
        this.levels = 0;
        this.beamSegments.clear();
        this.isComplete = true;
        TileEntityBeacon.BeamSegment tileentitybeacon$beamsegment = new TileEntityBeacon.BeamSegment(EntitySheep.func_175513_a(EnumDyeColor.WHITE));
        this.beamSegments.add(tileentitybeacon$beamsegment);
        boolean flag = true;
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int i1 = k + 1; i1 < 256; ++i1) {
            final IBlockState iblockstate = this.worldObj.getBlockState(blockpos$mutableblockpos.func_181079_c(j, i1, l));
            float[] afloat;

            if (iblockstate.getBlock() == Blocks.stained_glass) {
                afloat = EntitySheep.func_175513_a(iblockstate.getValue(BlockStainedGlass.COLOR));
            } else {
                if (iblockstate.getBlock() != Blocks.stained_glass_pane) {
                    if (iblockstate.getBlock().getLightOpacity() >= 15 && iblockstate.getBlock() != Blocks.bedrock) {
                        this.isComplete = false;
                        this.beamSegments.clear();
                        break;
                    }

                    tileentitybeacon$beamsegment.incrementHeight();
                    continue;
                }

                afloat = EntitySheep.func_175513_a(iblockstate.getValue(BlockStainedGlassPane.COLOR));
            }

            if (!flag) {
                afloat = new float[]{(tileentitybeacon$beamsegment.getColors()[0] + afloat[0]) / 2.0F, (tileentitybeacon$beamsegment.getColors()[1] + afloat[1]) / 2.0F, (tileentitybeacon$beamsegment.getColors()[2] + afloat[2]) / 2.0F};
            }

            if (Arrays.equals(afloat, tileentitybeacon$beamsegment.getColors())) {
                tileentitybeacon$beamsegment.incrementHeight();
            } else {
                tileentitybeacon$beamsegment = new TileEntityBeacon.BeamSegment(afloat);
                this.beamSegments.add(tileentitybeacon$beamsegment);
            }

            flag = false;
        }

        if (this.isComplete) {
            for (int l1 = 1; l1 <= 4; this.levels = l1++) {
                final int i2 = k - l1;

                if (i2 < 0) {
                    break;
                }

                boolean flag1 = true;

                for (int j1 = j - l1; j1 <= j + l1 && flag1; ++j1) {
                    for (int k1 = l - l1; k1 <= l + l1; ++k1) {
                        final Block block = this.worldObj.getBlockState(new BlockPos(j1, i2, k1)).getBlock();

                        if (block != Blocks.emerald_block && block != Blocks.gold_block && block != Blocks.diamond_block && block != Blocks.iron_block) {
                            flag1 = false;
                            break;
                        }
                    }
                }

                if (!flag1) {
                    break;
                }
            }

            if (this.levels == 0) {
                this.isComplete = false;
            }
        }

        if (!this.worldObj.isRemote && this.levels == 4 && i < this.levels) {
            for (final EntityPlayer entityplayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, (new AxisAlignedBB(j, k, l, j, k - 4, l)).expand(10.0D, 5.0D, 10.0D))) {
                entityplayer.triggerAchievement(AchievementList.fullBeacon);
            }
        }
    }

    public List<TileEntityBeacon.BeamSegment> getBeamSegments() {
        return this.beamSegments;
    }

    public float shouldBeamRender() {
        if (!this.isComplete) {
            return 0.0F;
        } else {
            final int i = (int) (this.worldObj.getTotalWorldTime() - this.beamRenderCounter);
            this.beamRenderCounter = this.worldObj.getTotalWorldTime();

            if (i > 1) {
                this.field_146014_j -= (float) i / 40.0F;

                if (this.field_146014_j < 0.0F) {
                    this.field_146014_j = 0.0F;
                }
            }

            this.field_146014_j += 0.025F;

            if (this.field_146014_j > 1.0F) {
                this.field_146014_j = 1.0F;
            }

            return this.field_146014_j;
        }
    }

    /**
     * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
     * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
     */
    public Packet getDescriptionPacket() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.pos, 3, nbttagcompound);
    }

    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    private int func_183001_h(final int p_183001_1_) {
        if (p_183001_1_ >= 0 && p_183001_1_ < Potion.potionTypes.length && Potion.potionTypes[p_183001_1_] != null) {
            final Potion potion = Potion.potionTypes[p_183001_1_];
            return potion != Potion.moveSpeed && potion != Potion.digSpeed && potion != Potion.resistance && potion != Potion.jump && potion != Potion.damageBoost && potion != Potion.regeneration ? 0 : p_183001_1_;
        } else {
            return 0;
        }
    }

    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.primaryEffect = this.func_183001_h(compound.getInteger("Primary"));
        this.secondaryEffect = this.func_183001_h(compound.getInteger("Secondary"));
        this.levels = compound.getInteger("Levels");
    }

    public void writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Primary", this.primaryEffect);
        compound.setInteger("Secondary", this.secondaryEffect);
        compound.setInteger("Levels", this.levels);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return 1;
    }

    /**
     * Returns the stack in the given slot.
     *
     * @param index The slot to retrieve from.
     */
    public ItemStack getStackInSlot(final int index) {
        return index == 0 ? this.payment : null;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *
     * @param index The slot to remove from.
     * @param count The maximum amount of items to remove.
     */
    public ItemStack decrStackSize(final int index, final int count) {
        if (index == 0 && this.payment != null) {
            if (count >= this.payment.stackSize) {
                final ItemStack itemstack = this.payment;
                this.payment = null;
                return itemstack;
            } else {
                this.payment.stackSize -= count;
                return new ItemStack(this.payment.getItem(), count, this.payment.getMetadata());
            }
        } else {
            return null;
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     *
     * @param index The slot to remove a stack from.
     */
    public ItemStack getStackInSlotOnClosing(final int index) {
        if (index == 0 && this.payment != null) {
            final ItemStack itemstack = this.payment;
            this.payment = null;
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        if (index == 0) {
            this.payment = stack;
        }
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName() {
        return this.hasCustomName() ? this.customName : "container.beacon";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setName(final String name) {
        this.customName = name;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit() {
        return 1;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(final EntityPlayer player) {
        return this.worldObj.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public void openInventory(final EntityPlayer player) {
    }

    public void closeInventory(final EntityPlayer player) {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
    }

    public String getGuiID() {
        return "minecraft:beacon";
    }

    public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer playerIn) {
        return new ContainerBeacon(playerInventory, this);
    }

    public int getField(final int id) {
        switch (id) {
            case 0:
                return this.levels;

            case 1:
                return this.primaryEffect;

            case 2:
                return this.secondaryEffect;

            default:
                return 0;
        }
    }

    public void setField(final int id, final int value) {
        switch (id) {
            case 0:
                this.levels = value;
                break;

            case 1:
                this.primaryEffect = this.func_183001_h(value);
                break;

            case 2:
                this.secondaryEffect = this.func_183001_h(value);
        }
    }

    public int getFieldCount() {
        return 3;
    }

    public void clear() {
        this.payment = null;
    }

    public boolean receiveClientEvent(final int id, final int type) {
        if (id == 1) {
            this.updateBeacon();
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    public static class BeamSegment {
        private final float[] colors;
        private int height;

        public BeamSegment(final float[] p_i45669_1_) {
            this.colors = p_i45669_1_;
            this.height = 1;
        }

        protected void incrementHeight() {
            ++this.height;
        }

        public float[] getColors() {
            return this.colors;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
