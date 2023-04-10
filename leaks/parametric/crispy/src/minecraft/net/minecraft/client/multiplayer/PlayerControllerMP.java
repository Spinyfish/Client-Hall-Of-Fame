package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class PlayerControllerMP
{
    /** The Minecraft instance. */
    private final Minecraft mc;
    private final NetHandlerPlayClient netClientHandler;
    private BlockPos currentBlock = new BlockPos(-1, -1, -1);

    /** The Item currently being used to destroy a block */
    private ItemStack currentItemHittingBlock;

    /** Current block damage (MP) */
    private float curBlockDamageMP;

    /**
     * Tick counter, when it hits 4 it resets back to 0 and plays the step sound
     */
    private float stepSoundTickCounter;

    /**
     * Delays the first damage on the block after the first click on the block
     */
    public int blockHitDelay;

    /** Tells if the player is hitting a block */
    private boolean isHittingBlock;

    /** Current game type for the player */
    private WorldSettings.GameType currentGameType;

    /** Index of the current item held by the player in the inventory hotbar */
    private int currentPlayerItem;

    public PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient p_i45062_2_)
    {
        this.currentGameType = WorldSettings.GameType.SURVIVAL;
        this.mc = mcIn;
        this.netClientHandler = p_i45062_2_;
    }

    public static void clickBlockCreative(Minecraft mcIn, PlayerControllerMP p_178891_1_, BlockPos p_178891_2_, EnumFacing p_178891_3_)
    {
        if (!mcIn.theWorld.extinguishFire(mcIn.thePlayer, p_178891_2_, p_178891_3_))
        {
            p_178891_1_.onPlayerDestroyBlock(p_178891_2_, p_178891_3_);
        }
    }

    /**
     * Sets player capabilities depending on current gametype. params: player
     */
    public void setPlayerCapabilities(EntityPlayer p_78748_1_)
    {
        this.currentGameType.configurePlayerCapabilities(p_78748_1_.capabilities);
    }

    /**
     * None
     */
    public boolean isSpectator()
    {
        return this.currentGameType == WorldSettings.GameType.SPECTATOR;
    }

    /**
     * Sets the game type for the player.
     */
    public void setGameType(WorldSettings.GameType p_78746_1_)
    {
        this.currentGameType = p_78746_1_;
        this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
    }

    /**
     * Flips the player around.
     */
    public void flipPlayer(EntityPlayer playerIn)
    {
        playerIn.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    /**
     * Called when a player completes the destruction of a block
     *  
     * @param pos The block's coordinates
     * @param side The side it was destroyed from
     */
    public boolean onPlayerDestroyBlock(BlockPos pos, EnumFacing side)
    {
        if (this.currentGameType.isAdventure())
        {
            if (this.currentGameType == WorldSettings.GameType.SPECTATOR)
            {
                return false;
            }

            if (!this.mc.thePlayer.isAllowEdit())
            {
                Block var3 = this.mc.theWorld.getBlockState(pos).getBlock();
                ItemStack var4 = this.mc.thePlayer.getCurrentEquippedItem();

                if (var4 == null)
                {
                    return false;
                }

                if (!var4.canDestroy(var3))
                {
                    return false;
                }
            }
        }

        if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
        {
            return false;
        }
        else
        {
            WorldClient var8 = this.mc.theWorld;
            IBlockState var9 = var8.getBlockState(pos);
            Block var5 = var9.getBlock();

            if (var5.getMaterial() == Material.air)
            {
                return false;
            }
            else
            {
                var8.playAuxSFX(2001, pos, Block.getStateId(var9));
                boolean var6 = var8.setBlockToAir(pos);

                if (var6)
                {
                    var5.onBlockDestroyedByPlayer(var8, pos, var9);
                }

                this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());

                if (!this.currentGameType.isCreative())
                {
                    ItemStack var7 = this.mc.thePlayer.getCurrentEquippedItem();

                    if (var7 != null)
                    {
                        var7.onBlockDestroyed(var8, var5, pos, this.mc.thePlayer);

                        if (var7.stackSize == 0)
                        {
                            this.mc.thePlayer.destroyCurrentEquippedItem();
                        }
                    }
                }

                return var6;
            }
        }
    }

    /**
     * Called when the player is hitting a block with an item.
     *  
     * @param loc location of the block being clicked
     * @param face Blockface being clicked
     */
    public boolean clickBlock(BlockPos loc, EnumFacing face)
    {
        Block var3;

        if (this.currentGameType.isAdventure())
        {
            if (this.currentGameType == WorldSettings.GameType.SPECTATOR)
            {
                return false;
            }

            if (!this.mc.thePlayer.isAllowEdit())
            {
                var3 = this.mc.theWorld.getBlockState(loc).getBlock();
                ItemStack var4 = this.mc.thePlayer.getCurrentEquippedItem();

                if (var4 == null)
                {
                    return false;
                }

                if (!var4.canDestroy(var3))
                {
                    return false;
                }
            }
        }

        if (!this.mc.theWorld.getWorldBorder().contains(loc))
        {
            return false;
        }
        else
        {
            if (this.currentGameType.isCreative())
            {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
                clickBlockCreative(this.mc, this, loc, face);
                this.blockHitDelay = 5;
            }
            else if (!this.isHittingBlock || !this.isHittingPosition(loc))
            {
                if (this.isHittingBlock)
                {
                    this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
                }

                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
                var3 = this.mc.theWorld.getBlockState(loc).getBlock();
                boolean var5 = var3.getMaterial() != Material.air;

                if (var5 && this.curBlockDamageMP == 0.0F)
                {
                    var3.onBlockClicked(this.mc.theWorld, loc, this.mc.thePlayer);
                }

                if (var5 && var3.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, loc) >= 1.0F)
                {
                    this.onPlayerDestroyBlock(loc, face);
                }
                else
                {
                    this.isHittingBlock = true;
                    this.currentBlock = loc;
                    this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
                }
            }

            return true;
        }
    }

    /**
     * Resets current block damage and isHittingBlock
     */
    public void resetBlockRemoving()
    {
        if (this.isHittingBlock)
        {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0F;
            this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, -1);
        }
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing)
    {
        this.syncCurrentPlayItem();

        if (this.blockHitDelay > 0)
        {
            --this.blockHitDelay;
            return true;
        }
        else if (this.currentGameType.isCreative() && this.mc.theWorld.getWorldBorder().contains(posBlock))
        {
            this.blockHitDelay = 5;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
            clickBlockCreative(this.mc, this, posBlock, directionFacing);
            return true;
        }
        else if (this.isHittingPosition(posBlock))
        {
            Block var3 = this.mc.theWorld.getBlockState(posBlock).getBlock();

            if (var3.getMaterial() == Material.air)
            {
                this.isHittingBlock = false;
                return false;
            }
            else
            {
                this.curBlockDamageMP += var3.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, posBlock);

                if (this.stepSoundTickCounter % 4.0F == 0.0F)
                {
                    this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(var3.stepSound.getStepSound()), (var3.stepSound.getVolume() + 1.0F) / 8.0F, var3.stepSound.getFrequency() * 0.5F, (float)posBlock.getX() + 0.5F, (float)posBlock.getY() + 0.5F, (float)posBlock.getZ() + 0.5F));
                }

                ++this.stepSoundTickCounter;

                if (this.curBlockDamageMP >= 1.0F)
                {
                    this.isHittingBlock = false;
                    this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
                    this.onPlayerDestroyBlock(posBlock, directionFacing);
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                }

                this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
                return true;
            }
        }
        else
        {
            return this.clickBlock(posBlock, directionFacing);
        }
    }

    /**
     * player reach distance = 4F
     */
    public float getBlockReachDistance()
    {
      return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }

    public void updateController()
    {
        this.syncCurrentPlayItem();

        if (this.netClientHandler.getNetworkManager().isChannelOpen())
        {
            this.netClientHandler.getNetworkManager().processReceivedPackets();
        }
        else
        {
            this.netClientHandler.getNetworkManager().checkDisconnected();
        }
    }

    private boolean isHittingPosition(BlockPos pos)
    {
        ItemStack var2 = this.mc.thePlayer.getHeldItem();
        boolean var3 = this.currentItemHittingBlock == null && var2 == null;

        if (this.currentItemHittingBlock != null && var2 != null)
        {
            var3 = var2.getItem() == this.currentItemHittingBlock.getItem() && ItemStack.areItemStackTagsEqual(var2, this.currentItemHittingBlock) && (var2.isItemStackDamageable() || var2.getMetadata() == this.currentItemHittingBlock.getMetadata());
        }

        return pos.equals(this.currentBlock) && var3;
    }

    /**
     * Syncs the current player item with the server
     */
    private void syncCurrentPlayItem()
    {
        int var1 = this.mc.thePlayer.inventory.currentItem;

        if (var1 != this.currentPlayerItem)
        {
            this.currentPlayerItem = var1;
            this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
        }
    }

    public boolean onPlayerRightClick(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos hitPos, EnumFacing side, Vec3 hitVec)
    {
        this.syncCurrentPlayItem();
        float var7 = (float)(hitVec.xCoord - (double)hitPos.getX());
        float var8 = (float)(hitVec.yCoord - (double)hitPos.getY());
        float var9 = (float)(hitVec.zCoord - (double)hitPos.getZ());
        boolean var10 = false;

        if (!this.mc.theWorld.getWorldBorder().contains(hitPos))
        {
            return false;
        }
        else
        {
            if (this.currentGameType != WorldSettings.GameType.SPECTATOR)
            {
                IBlockState var11 = worldIn.getBlockState(hitPos);

                if ((!player.isSneaking() || player.getHeldItem() == null) && var11.getBlock().onBlockActivated(worldIn, hitPos, var11, player, side, var7, var8, var9))
                {
                    var10 = true;
                }

                if (!var10 && heldStack != null && heldStack.getItem() instanceof ItemBlock)
                {
                    ItemBlock var12 = (ItemBlock)heldStack.getItem();

                    if (!var12.canPlaceBlockOnSide(worldIn, hitPos, side, player, heldStack))
                    {
                        return false;
                    }
                }
            }

            this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(hitPos, side.getIndex(), player.inventory.getCurrentItem(), var7, var8, var9));

            if (!var10 && this.currentGameType != WorldSettings.GameType.SPECTATOR)
            {
                if (heldStack == null)
                {
                    return false;
                }
                else if (this.currentGameType.isCreative())
                {
                    int var14 = heldStack.getMetadata();
                    int var15 = heldStack.stackSize;
                    boolean var13 = heldStack.onItemUse(player, worldIn, hitPos, side, var7, var8, var9);
                    heldStack.setItemDamage(var14);
                    heldStack.stackSize = var15;
                    return var13;
                }
                else
                {
                    return heldStack.onItemUse(player, worldIn, hitPos, side, var7, var8, var9);
                }
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Notifies the server of things like consuming food, etc...
     */
    public boolean sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn)
    {
        if (this.currentGameType == WorldSettings.GameType.SPECTATOR)
        {
            return false;
        }
        else
        {
            this.syncCurrentPlayItem();
            this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(playerIn.inventory.getCurrentItem()));
            int var4 = itemStackIn.stackSize;
            ItemStack var5 = itemStackIn.useItemRightClick(worldIn, playerIn);

            if (var5 == itemStackIn && (var5 == null || var5.stackSize == var4))
            {
                return false;
            }
            else
            {
                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = var5;

                if (var5.stackSize == 0)
                {
                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                }

                return true;
            }
        }
    }

    public EntityPlayerSP func_178892_a(World worldIn, StatFileWriter p_178892_2_)
    {
        return new EntityPlayerSP(this.mc, worldIn, this.netClientHandler, p_178892_2_);
    }

    /**
     * Attacks an entity
     */
    public void attackEntity(EntityPlayer playerIn, Entity targetEntity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));

        if (this.currentGameType != WorldSettings.GameType.SPECTATOR)
        {
            playerIn.attackTargetEntityWithCurrentItem(targetEntity);
        }
    }


    public void attackEntity2(EntityPlayer playerIn, Entity targetEntity)
    {
        this.syncCurrentPlayItem();
        if (this.currentGameType != WorldSettings.GameType.SPECTATOR)
        {
            playerIn.attackTargetEntityWithCurrentItem(targetEntity);
        }
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));
    }


    /**
     * Send packet to server - player is interacting with another entity (left click)
     */
    public boolean interactWithEntitySendPacket(EntityPlayer playerIn, Entity targetEntity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.INTERACT));
        return this.currentGameType != WorldSettings.GameType.SPECTATOR && playerIn.interactWith(targetEntity);
    }

    public boolean func_178894_a(EntityPlayer p_178894_1_, Entity p_178894_2_, MovingObjectPosition p_178894_3_)
    {
        this.syncCurrentPlayItem();
        Vec3 var4 = new Vec3(p_178894_3_.hitVec.xCoord - p_178894_2_.posX, p_178894_3_.hitVec.yCoord - p_178894_2_.posY, p_178894_3_.hitVec.zCoord - p_178894_2_.posZ);
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(p_178894_2_, var4));
        return this.currentGameType != WorldSettings.GameType.SPECTATOR && p_178894_2_.interactAt(p_178894_1_, var4);
    }

    /**
     * Handles slot clicks sends a packet to the server.
     */
    public ItemStack windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn)
    {
        short var6 = playerIn.openContainer.getNextTransactionID(playerIn.inventory);
        ItemStack var7 = playerIn.openContainer.slotClick(slotId, mouseButtonClicked, mode, playerIn);
        this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, mode, var7, var6));
        return var7;
    }

    /**
     * GuiEnchantment uses this during multiplayer to tell PlayerControllerMP to send a packet indicating the
     * enchantment action the player has taken.
     */
    public void sendEnchantPacket(int p_78756_1_, int p_78756_2_)
    {
        this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(p_78756_1_, p_78756_2_));
    }

    /**
     * Used in PlayerControllerMP to update the server with an ItemStack in a slot.
     */
    public void sendSlotPacket(ItemStack itemStackIn, int slotId)
    {
        if (this.currentGameType.isCreative())
        {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(slotId, itemStackIn));
        }
    }

    /**
     * Sends a Packet107 to the server to drop the item on the ground
     */
    public void sendPacketDropItem(ItemStack itemStackIn)
    {
        if (this.currentGameType.isCreative() && itemStackIn != null)
        {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, itemStackIn));
        }
    }

    public void onStoppedUsingItem(EntityPlayer playerIn)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        playerIn.stopUsingItem();
    }

    public boolean gameIsSurvivalOrAdventure()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    /**
     * Checks if the player is not creative, used for checking if it should break a block instantly
     */
    public boolean isNotCreative()
    {
        return !this.currentGameType.isCreative();
    }

    /**
     * returns true if player is in creative mode
     */
    public boolean isInCreativeMode()
    {
        return this.currentGameType.isCreative();
    }

    /**
     * true for hitting entities far away.
     */
    public boolean extendedReach()
    {
        return this.currentGameType.isCreative();
    }

    /**
     * Checks if the player is riding a horse, used to chose the GUI to open
     */
    public boolean isRidingHorse()
    {
        return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
    }

    public boolean isSpectatorMode()
    {
        return this.currentGameType == WorldSettings.GameType.SPECTATOR;
    }

    public WorldSettings.GameType getCurrentGameType()
    {
        return this.currentGameType;
    }
}
