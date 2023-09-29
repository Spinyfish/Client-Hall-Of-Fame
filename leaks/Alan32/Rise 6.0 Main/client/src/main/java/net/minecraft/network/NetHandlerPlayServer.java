package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.*;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class NetHandlerPlayServer implements INetHandlerPlayServer, ITickable {
    private static final Logger logger = LogManager.getLogger();
    public final NetworkManager netManager;
    private final MinecraftServer serverController;
    public EntityPlayerMP playerEntity;
    private int networkTickCount;
    private int field_175090_f;

    /**
     * Used to keep track of how the player is floating while gamerules should prevent that. Surpassing 80 ticks means
     * kick
     */
    private int floatingTickCount;
    private boolean field_147366_g;
    private int field_147378_h;
    private long lastPingTime;
    private long lastSentPingPacket;

    /**
     * Incremented by 20 each time a user sends a chat message, decreased by one every tick. Non-ops kicked when over
     * 200
     */
    private int chatSpamThresholdCount;
    private int itemDropThreshold;
    private final IntHashMap<Short> field_147372_n = new IntHashMap();
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private boolean hasMoved = true;

    public NetHandlerPlayServer(final MinecraftServer server, final NetworkManager networkManagerIn, final EntityPlayerMP playerIn) {
        this.serverController = server;
        this.netManager = networkManagerIn;
        networkManagerIn.setNetHandler(this);
        this.playerEntity = playerIn;
        playerIn.playerNetServerHandler = this;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        this.field_147366_g = false;
        ++this.networkTickCount;
        this.serverController.theProfiler.startSection("keepAlive");

        if ((long) this.networkTickCount - this.lastSentPingPacket > 40L) {
            this.lastSentPingPacket = this.networkTickCount;
            this.lastPingTime = this.currentTimeMillis();
            this.field_147378_h = (int) this.lastPingTime;
            this.sendPacket(new S00PacketKeepAlive(this.field_147378_h));
        }

        this.serverController.theProfiler.endSection();

        if (this.chatSpamThresholdCount > 0) {
            --this.chatSpamThresholdCount;
        }

        if (this.itemDropThreshold > 0) {
            --this.itemDropThreshold;
        }

        if (this.playerEntity.getLastActiveTime() > 0L && this.serverController.getMaxPlayerIdleMinutes() > 0 && MinecraftServer.getCurrentTimeMillis() - this.playerEntity.getLastActiveTime() > (long) (this.serverController.getMaxPlayerIdleMinutes() * 1000 * 60)) {
            this.kickPlayerFromServer("You have been idle for too long!");
        }
    }

    public NetworkManager getNetworkManager() {
        return this.netManager;
    }

    /**
     * Kick a player from the server with a reason
     */
    public void kickPlayerFromServer(final String reason) {
        final ChatComponentText chatcomponenttext = new ChatComponentText(reason);
        this.netManager.sendPacket(new S40PacketDisconnect(chatcomponenttext), new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(final Future<? super Void> p_operationComplete_1_) throws Exception {
                NetHandlerPlayServer.this.netManager.closeChannel(chatcomponenttext);
            }
        });
        this.netManager.disableAutoRead();
        Futures.getUnchecked(this.serverController.addScheduledTask(new Runnable() {
            public void run() {
                NetHandlerPlayServer.this.netManager.checkDisconnected();
            }
        }));
    }

    /**
     * Processes player movement input. Includes walking, strafing, jumping, sneaking; excludes riding and toggling
     * flying/sprinting
     */
    public void processInput(final C0CPacketInput packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.setEntityActionState(packetIn.getStrafeSpeed(), packetIn.getForwardSpeed(), packetIn.isJumping(), packetIn.isSneaking());
    }

    private boolean func_183006_b(final C03PacketPlayer p_183006_1_) {
        return !Doubles.isFinite(p_183006_1_.getPositionX()) || !Doubles.isFinite(p_183006_1_.getPositionY()) || !Doubles.isFinite(p_183006_1_.getPositionZ()) || !Floats.isFinite(p_183006_1_.getPitch()) || !Floats.isFinite(p_183006_1_.getYaw());
    }

    /**
     * Processes clients perspective on player positioning and/or orientation
     */
    public void processPlayer(final C03PacketPlayer packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (this.func_183006_b(packetIn)) {
            this.kickPlayerFromServer("Invalid move packet received");
        } else {
            final WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
            this.field_147366_g = true;

            if (!this.playerEntity.playerConqueredTheEnd) {
                final double d0 = this.playerEntity.posX;
                final double d1 = this.playerEntity.posY;
                final double d2 = this.playerEntity.posZ;
                double d3 = 0.0D;
                final double d4 = packetIn.getPositionX() - this.lastPosX;
                final double d5 = packetIn.getPositionY() - this.lastPosY;
                final double d6 = packetIn.getPositionZ() - this.lastPosZ;

                if (packetIn.isMoving()) {
                    d3 = d4 * d4 + d5 * d5 + d6 * d6;

                    if (!this.hasMoved && d3 < 0.25D) {
                        this.hasMoved = true;
                    }
                }

                if (this.hasMoved) {
                    this.field_175090_f = this.networkTickCount;

                    if (this.playerEntity.ridingEntity != null) {
                        float f4 = this.playerEntity.rotationYaw;
                        float f = this.playerEntity.rotationPitch;
                        this.playerEntity.ridingEntity.updateRiderPosition();
                        final double d16 = this.playerEntity.posX;
                        final double d17 = this.playerEntity.posY;
                        final double d18 = this.playerEntity.posZ;

                        if (packetIn.getRotating()) {
                            f4 = packetIn.getYaw();
                            f = packetIn.getPitch();
                        }

                        this.playerEntity.onGround = packetIn.isOnGround();
                        this.playerEntity.onUpdateEntity();
                        this.playerEntity.setPositionAndRotation(d16, d17, d18, f4, f);

                        if (this.playerEntity.ridingEntity != null) {
                            this.playerEntity.ridingEntity.updateRiderPosition();
                        }

                        this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);

                        if (this.playerEntity.ridingEntity != null) {
                            if (d3 > 4.0D) {
                                final Entity entity = this.playerEntity.ridingEntity;
                                this.playerEntity.playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(entity));
                                this.setPlayerLocation(this.playerEntity.posX, this.playerEntity.posY, this.playerEntity.posZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                            }

                            this.playerEntity.ridingEntity.isAirBorne = true;
                        }

                        if (this.hasMoved) {
                            this.lastPosX = this.playerEntity.posX;
                            this.lastPosY = this.playerEntity.posY;
                            this.lastPosZ = this.playerEntity.posZ;
                        }

                        worldserver.updateEntity(this.playerEntity);
                        return;
                    }

                    if (this.playerEntity.isPlayerSleeping()) {
                        this.playerEntity.onUpdateEntity();
                        this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                        worldserver.updateEntity(this.playerEntity);
                        return;
                    }

                    final double d7 = this.playerEntity.posY;
                    this.lastPosX = this.playerEntity.posX;
                    this.lastPosY = this.playerEntity.posY;
                    this.lastPosZ = this.playerEntity.posZ;
                    double d8 = this.playerEntity.posX;
                    double d9 = this.playerEntity.posY;
                    double d10 = this.playerEntity.posZ;
                    float f1 = this.playerEntity.rotationYaw;
                    float f2 = this.playerEntity.rotationPitch;

                    if (packetIn.isMoving() && packetIn.getPositionY() == -999.0D) {
                        packetIn.setMoving(false);
                    }

                    if (packetIn.isMoving()) {
                        d8 = packetIn.getPositionX();
                        d9 = packetIn.getPositionY();
                        d10 = packetIn.getPositionZ();

                        if (Math.abs(packetIn.getPositionX()) > 3.0E7D || Math.abs(packetIn.getPositionZ()) > 3.0E7D) {
                            this.kickPlayerFromServer("Illegal position");
                            return;
                        }
                    }

                    if (packetIn.getRotating()) {
                        f1 = packetIn.getYaw();
                        f2 = packetIn.getPitch();
                    }

                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, f1, f2);

                    if (!this.hasMoved) {
                        return;
                    }

                    double d11 = d8 - this.playerEntity.posX;
                    double d12 = d9 - this.playerEntity.posY;
                    double d13 = d10 - this.playerEntity.posZ;
                    final double d14 = this.playerEntity.motionX * this.playerEntity.motionX + this.playerEntity.motionY * this.playerEntity.motionY + this.playerEntity.motionZ * this.playerEntity.motionZ;
                    double d15 = d11 * d11 + d12 * d12 + d13 * d13;

                    if (d15 - d14 > 100.0D && (!this.serverController.isSinglePlayer() || !this.serverController.getServerOwner().equals(this.playerEntity.getCommandSenderName()))) {
                        logger.warn(this.playerEntity.getCommandSenderName() + " moved too quickly! " + d11 + "," + d12 + "," + d13 + " (" + d11 + ", " + d12 + ", " + d13 + ")");
                        this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                        return;
                    }

                    final float f3 = 0.0625F;
                    final boolean flag = worldserver.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(f3, f3, f3)).isEmpty();

                    if (this.playerEntity.onGround && !packetIn.isOnGround() && d12 > 0.0D) {
                        this.playerEntity.jump();
                    }

                    this.playerEntity.moveEntity(d11, d12, d13);
                    this.playerEntity.onGround = packetIn.isOnGround();
                    d11 = d8 - this.playerEntity.posX;
                    d12 = d9 - this.playerEntity.posY;

                    if (d12 > -0.5D || d12 < 0.5D) {
                        d12 = 0.0D;
                    }

                    d13 = d10 - this.playerEntity.posZ;
                    d15 = d11 * d11 + d12 * d12 + d13 * d13;
                    boolean flag1 = false;

                    if (d15 > 0.0625D && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative()) {
                        flag1 = true;
                        logger.warn(this.playerEntity.getCommandSenderName() + " moved wrongly!");
                    }

                    this.playerEntity.setPositionAndRotation(d8, d9, d10, f1, f2);
                    this.playerEntity.addMovementStat(this.playerEntity.posX - d0, this.playerEntity.posY - d1, this.playerEntity.posZ - d2);

                    if (!this.playerEntity.noClip) {
                        final boolean flag2 = worldserver.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(f3, f3, f3)).isEmpty();

                        if (flag && (flag1 || !flag2) && !this.playerEntity.isPlayerSleeping()) {
                            this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, f1, f2);
                            return;
                        }
                    }

                    final AxisAlignedBB axisalignedbb = this.playerEntity.getEntityBoundingBox().expand(f3, f3, f3).addCoord(0.0D, -0.55D, 0.0D);

                    if (!this.serverController.isFlightAllowed() && !this.playerEntity.capabilities.allowFlying && !worldserver.checkBlockCollision(axisalignedbb)) {
                        if (d12 >= -0.03125D) {
                            ++this.floatingTickCount;

                            if (this.floatingTickCount > 80) {
                                logger.warn(this.playerEntity.getCommandSenderName() + " was kicked for floating too long!");
                                this.kickPlayerFromServer("Flying is not enabled on this server");
                                return;
                            }
                        }
                    } else {
                        this.floatingTickCount = 0;
                    }

                    this.playerEntity.onGround = packetIn.isOnGround();
                    this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                    this.playerEntity.handleFalling(this.playerEntity.posY - d7, packetIn.isOnGround());
                } else if (this.networkTickCount - this.field_175090_f > 20) {
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                }
            }
        }
    }

    public void setPlayerLocation(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.setPlayerLocation(x, y, z, yaw, pitch, Collections.emptySet());
    }

    public void setPlayerLocation(final double x, final double y, final double z, final float yaw, final float pitch, final Set<S08PacketPlayerPosLook.EnumFlags> relativeSet) {
        this.hasMoved = false;
        this.lastPosX = x;
        this.lastPosY = y;
        this.lastPosZ = z;

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.X)) {
            this.lastPosX += this.playerEntity.posX;
        }

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
            this.lastPosY += this.playerEntity.posY;
        }

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
            this.lastPosZ += this.playerEntity.posZ;
        }

        float f = yaw;
        float f1 = pitch;

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
            f = yaw + this.playerEntity.rotationYaw;
        }

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
            f1 = pitch + this.playerEntity.rotationPitch;
        }

        this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, f, f1);
        this.playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(x, y, z, yaw, pitch, relativeSet));
    }

    /**
     * Processes the player initiating/stopping digging on a particular spot, as well as a player dropping items?. (0:
     * initiated, 1: reinitiated, 2? , 3-4 drop item (respectively without or with player control), 5: stopped; x,y,z,
     * side clicked on;)
     */
    public void processPlayerDigging(final C07PacketPlayerDigging packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        final WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        final BlockPos blockpos = packetIn.getPosition();
        this.playerEntity.markPlayerActive();

        switch (packetIn.getStatus()) {
            case DROP_ITEM:
                if (!this.playerEntity.isSpectator()) {
                    this.playerEntity.dropOneItem(false);
                }

                return;

            case DROP_ALL_ITEMS:
                if (!this.playerEntity.isSpectator()) {
                    this.playerEntity.dropOneItem(true);
                }

                return;

            case RELEASE_USE_ITEM:
                this.playerEntity.stopUsingItem();
                return;

            case START_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
                final double d0 = this.playerEntity.posX - ((double) blockpos.getX() + 0.5D);
                final double d1 = this.playerEntity.posY - ((double) blockpos.getY() + 0.5D) + 1.5D;
                final double d2 = this.playerEntity.posZ - ((double) blockpos.getZ() + 0.5D);
                final double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > 36.0D) {
                    return;
                } else if (blockpos.getY() >= this.serverController.getBuildLimit()) {
                    return;
                } else {
                    if (packetIn.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                        if (!this.serverController.isBlockProtected(worldserver, blockpos, this.playerEntity) && worldserver.getWorldBorder().contains(blockpos)) {
                            this.playerEntity.theItemInWorldManager.onBlockClicked(blockpos, packetIn.getFacing());
                        } else {
                            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldserver, blockpos));
                        }
                    } else {
                        if (packetIn.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                            this.playerEntity.theItemInWorldManager.blockRemoving(blockpos);
                        } else if (packetIn.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                            this.playerEntity.theItemInWorldManager.cancelDestroyingBlock();
                        }

                        if (worldserver.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldserver, blockpos));
                        }
                    }

                    return;
                }

            default:
                throw new IllegalArgumentException("Invalid player action");
        }
    }

    /**
     * Processes block placement and block activation (anvil, furnace, etc.)
     */
    public void processPlayerBlockPlacement(final C08PacketPlayerBlockPlacement packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        final WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        ItemStack itemstack = this.playerEntity.inventory.getCurrentItem();
        boolean flag = false;
        final BlockPos blockpos = packetIn.getPosition();
        final EnumFacing enumfacing = EnumFacing.getFront(packetIn.getPlacedBlockDirection());
        this.playerEntity.markPlayerActive();

        if (packetIn.getPlacedBlockDirection() == 255) {
            if (itemstack == null) {
                return;
            }

            this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, worldserver, itemstack);
        } else if (blockpos.getY() < this.serverController.getBuildLimit() - 1 || enumfacing != EnumFacing.UP && blockpos.getY() < this.serverController.getBuildLimit()) {
            if (this.hasMoved && this.playerEntity.getDistanceSq((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D) < 64.0D && !this.serverController.isBlockProtected(worldserver, blockpos, this.playerEntity) && worldserver.getWorldBorder().contains(blockpos)) {
                this.playerEntity.theItemInWorldManager.activateBlockOrUseItem(this.playerEntity, worldserver, itemstack, blockpos, enumfacing, packetIn.getPlacedBlockOffsetX(), packetIn.getPlacedBlockOffsetY(), packetIn.getPlacedBlockOffsetZ());
            }

            flag = true;
        } else {
            final ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("build.tooHigh", Integer.valueOf(this.serverController.getBuildLimit()));
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            this.playerEntity.playerNetServerHandler.sendPacket(new S02PacketChat(chatcomponenttranslation));
            flag = true;
        }

        if (flag) {
            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldserver, blockpos));
            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldserver, blockpos.offset(enumfacing)));
        }

        itemstack = this.playerEntity.inventory.getCurrentItem();

        if (itemstack != null && itemstack.stackSize == 0) {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
            itemstack = null;
        }

        if (itemstack == null || itemstack.getMaxItemUseDuration() == 0) {
            this.playerEntity.isChangingQuantityOnly = true;
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
            final Slot slot = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
            this.playerEntity.openContainer.detectAndSendChanges();
            this.playerEntity.isChangingQuantityOnly = false;

            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packetIn.getStack())) {
                this.sendPacket(new S2FPacketSetSlot(this.playerEntity.openContainer.windowId, slot.slotNumber, this.playerEntity.inventory.getCurrentItem()));
            }
        }
    }

    public void handleSpectate(final C18PacketSpectate packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (this.playerEntity.isSpectator()) {
            Entity entity = null;

            for (final WorldServer worldserver : this.serverController.worldServers) {
                if (worldserver != null) {
                    entity = packetIn.getEntity(worldserver);

                    if (entity != null) {
                        break;
                    }
                }
            }

            if (entity != null) {
                this.playerEntity.setSpectatingEntity(this.playerEntity);
                this.playerEntity.mountEntity(null);

                if (entity.worldObj != this.playerEntity.worldObj) {
                    final WorldServer worldserver1 = this.playerEntity.getServerForPlayer();
                    final WorldServer worldserver2 = (WorldServer) entity.worldObj;
                    this.playerEntity.dimension = entity.dimension;
                    this.sendPacket(new S07PacketRespawn(this.playerEntity.dimension, worldserver1.getDifficulty(), worldserver1.getWorldInfo().getTerrainType(), this.playerEntity.theItemInWorldManager.getGameType()));
                    worldserver1.removePlayerEntityDangerously(this.playerEntity);
                    this.playerEntity.isDead = false;
                    this.playerEntity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);

                    if (this.playerEntity.isEntityAlive()) {
                        worldserver1.updateEntityWithOptionalForce(this.playerEntity, false);
                        worldserver2.spawnEntityInWorld(this.playerEntity);
                        worldserver2.updateEntityWithOptionalForce(this.playerEntity, false);
                    }

                    this.playerEntity.setWorld(worldserver2);
                    this.serverController.getConfigurationManager().preparePlayer(this.playerEntity, worldserver1);
                    this.playerEntity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
                    this.playerEntity.theItemInWorldManager.setWorld(worldserver2);
                    this.serverController.getConfigurationManager().updateTimeAndWeatherForPlayer(this.playerEntity, worldserver2);
                    this.serverController.getConfigurationManager().syncPlayerInventory(this.playerEntity);
                } else {
                    this.playerEntity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
                }
            }
        }
    }

    public void handleResourcePackStatus(final C19PacketResourcePackStatus packetIn) {
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(final IChatComponent reason) {
        logger.info(this.playerEntity.getCommandSenderName() + " lost connection: " + reason);
        this.serverController.refreshStatusNextTick();
        final ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.left", this.playerEntity.getDisplayName());
        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        this.serverController.getConfigurationManager().sendChatMsg(chatcomponenttranslation);
        this.playerEntity.mountEntityAndWakeUp();
        this.serverController.getConfigurationManager().playerLoggedOut(this.playerEntity);

        if (this.serverController.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.serverController.getServerOwner())) {
            logger.info("Stopping singleplayer server as player logged out");
            this.serverController.initiateShutdown();
        }
    }

    public void sendPacket(final Packet packetIn) {
        if (packetIn instanceof S02PacketChat) {
            final S02PacketChat s02packetchat = (S02PacketChat) packetIn;
            final EntityPlayer.EnumChatVisibility entityplayer$enumchatvisibility = this.playerEntity.getChatVisibility();

            if (entityplayer$enumchatvisibility == EntityPlayer.EnumChatVisibility.HIDDEN) {
                return;
            }

            if (entityplayer$enumchatvisibility == EntityPlayer.EnumChatVisibility.SYSTEM && !s02packetchat.isChat()) {
                return;
            }
        }

        try {
            this.netManager.sendPacket(packetIn);
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Sending packet");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Packet being sent");
            crashreportcategory.addCrashSectionCallable("Packet class", new Callable<String>() {
                public String call() throws Exception {
                    return packetIn.getClass().getCanonicalName();
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Updates which quickbar slot is selected
     */
    public void processHeldItemChange(final C09PacketHeldItemChange packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (packetIn.getSlotId() >= 0 && packetIn.getSlotId() < InventoryPlayer.getHotbarSize()) {
            this.playerEntity.inventory.currentItem = packetIn.getSlotId();
            this.playerEntity.markPlayerActive();
        } else {
            logger.warn(this.playerEntity.getCommandSenderName() + " tried to set an invalid carried item");
        }
    }

    /**
     * Process chat messages (broadcast back to clients) and commands (executes)
     */
    public void processChatMessage(final C01PacketChatMessage packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (this.playerEntity.getChatVisibility() == EntityPlayer.EnumChatVisibility.HIDDEN) {
            final ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("chat.cannotSend");
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            this.sendPacket(new S02PacketChat(chatcomponenttranslation));
        } else {
            this.playerEntity.markPlayerActive();
            String s = packetIn.getMessage();
            s = StringUtils.normalizeSpace(s);

            for (int i = 0; i < s.length(); ++i) {
                if (!ChatAllowedCharacters.isAllowedCharacter(s.charAt(i))) {
                    this.kickPlayerFromServer("Illegal characters in chat");
                    return;
                }
            }

            if (s.startsWith("/")) {
                this.handleSlashCommand(s);
            } else {
                final IChatComponent ichatcomponent = new ChatComponentTranslation("chat.type.text", this.playerEntity.getDisplayName(), s);
                this.serverController.getConfigurationManager().sendChatMsgImpl(ichatcomponent, false);
            }

            this.chatSpamThresholdCount += 20;

            if (this.chatSpamThresholdCount > 200 && !this.serverController.getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile())) {
                this.kickPlayerFromServer("disconnect.spam");
            }
        }
    }

    /**
     * Handle commands that start with a /
     */
    private void handleSlashCommand(final String command) {
        this.serverController.getCommandManager().executeCommand(this.playerEntity, command);
    }

    public void handleAnimation(final C0APacketAnimation packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();
        this.playerEntity.swingItem();
    }

    /**
     * Processes a range of action-types: sneaking, sprinting, waking from sleep, opening the inventory or setting jump
     * height of the horse the player is riding
     */
    public void processEntityAction(final C0BPacketEntityAction packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();

        switch (packetIn.getAction()) {
            case START_SNEAKING:
                this.playerEntity.setSneaking(true);
                break;

            case STOP_SNEAKING:
                this.playerEntity.setSneaking(false);
                break;

            case START_SPRINTING:
                this.playerEntity.setSprinting(true);
                break;

            case STOP_SPRINTING:
                this.playerEntity.setSprinting(false);
                break;

            case STOP_SLEEPING:
                this.playerEntity.wakeUpPlayer(false, true, true);
                this.hasMoved = false;
                break;

            case RIDING_JUMP:
                if (this.playerEntity.ridingEntity instanceof EntityHorse) {
                    ((EntityHorse) this.playerEntity.ridingEntity).setJumpPower(packetIn.getAuxData());
                }

                break;

            case OPEN_INVENTORY:
                if (this.playerEntity.ridingEntity instanceof EntityHorse) {
                    ((EntityHorse) this.playerEntity.ridingEntity).openGUI(this.playerEntity);
                }

                break;

            default:
                throw new IllegalArgumentException("Invalid client command!");
        }
    }

    /**
     * Processes interactions ((un)leashing, opening command block GUI) and attacks on an entity with players currently
     * equipped item
     */
    public void processUseEntity(final C02PacketUseEntity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        final WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        final Entity entity = packetIn.getEntityFromWorld(worldserver);
        this.playerEntity.markPlayerActive();

        if (entity != null) {
            final boolean flag = this.playerEntity.canEntityBeSeen(entity);
            double d0 = 36.0D;

            if (!flag) {
                d0 = 9.0D;
            }

            if (this.playerEntity.getDistanceSqToEntity(entity) < d0) {
                if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT) {
                    this.playerEntity.interactWith(entity);
                } else if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT_AT) {
                    entity.interactAt(this.playerEntity, packetIn.getHitVec());
                } else if (packetIn.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity == this.playerEntity) {
                        this.kickPlayerFromServer("Attempting to attack an invalid entity");
                        this.serverController.logWarning("Player " + this.playerEntity.getCommandSenderName() + " tried to attack an invalid entity");
                        return;
                    }

                    this.playerEntity.attackTargetEntityWithCurrentItem(entity);
                }
            }
        }
    }

    /**
     * Processes the client status updates: respawn attempt from player, opening statistics or achievements, or
     * acquiring 'open inventory' achievement
     */
    public void processClientStatus(final C16PacketClientStatus packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();
        final C16PacketClientStatus.EnumState c16packetclientstatus$enumstate = packetIn.getStatus();

        switch (c16packetclientstatus$enumstate) {
            case PERFORM_RESPAWN:
                if (this.playerEntity.playerConqueredTheEnd) {
                    this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, true);
                } else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled()) {
                    if (this.serverController.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.serverController.getServerOwner())) {
                        this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
                        this.serverController.deleteWorldAndStopServer();
                    } else {
                        final UserListBansEntry userlistbansentry = new UserListBansEntry(this.playerEntity.getGameProfile(), null, "(You just lost the game)", null, "Death in Hardcore");
                        this.serverController.getConfigurationManager().getBannedPlayers().addEntry(userlistbansentry);
                        this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
                    }
                } else {
                    if (this.playerEntity.getHealth() > 0.0F) {
                        return;
                    }

                    this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, false);
                }

                break;

            case REQUEST_STATS:
                this.playerEntity.getStatFile().func_150876_a(this.playerEntity);
                break;

            case OPEN_INVENTORY_ACHIEVEMENT:
                this.playerEntity.triggerAchievement(AchievementList.openInventory);
        }
    }

    /**
     * Processes the client closing windows (container)
     */
    public void processCloseWindow(final C0DPacketCloseWindow packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.closeContainer();
    }

    /**
     * Executes a container/inventory slot manipulation as indicated by the packet. Sends the serverside result if they
     * didn't match the indicated result and prevents further manipulation by the player until he confirms that it has
     * the same open container/inventory
     */
    public void processClickWindow(final C0EPacketClickWindow packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();

        if (this.playerEntity.openContainer.windowId == packetIn.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity)) {
            if (this.playerEntity.isSpectator()) {
                final List<ItemStack> list = Lists.newArrayList();

                for (int i = 0; i < this.playerEntity.openContainer.inventorySlots.size(); ++i) {
                    list.add(this.playerEntity.openContainer.inventorySlots.get(i).getStack());
                }

                this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, list);
            } else {
                final ItemStack itemstack = this.playerEntity.openContainer.slotClick(packetIn.getSlotId(), packetIn.getUsedButton(), packetIn.getMode(), this.playerEntity);

                if (ItemStack.areItemStacksEqual(packetIn.getClickedItem(), itemstack)) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
                    this.playerEntity.isChangingQuantityOnly = true;
                    this.playerEntity.openContainer.detectAndSendChanges();
                    this.playerEntity.updateHeldItem();
                    this.playerEntity.isChangingQuantityOnly = false;
                } else {
                    this.field_147372_n.addKey(this.playerEntity.openContainer.windowId, Short.valueOf(packetIn.getActionNumber()));
                    this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), false));
                    this.playerEntity.openContainer.setCanCraft(this.playerEntity, false);
                    final List<ItemStack> list1 = Lists.newArrayList();

                    for (int j = 0; j < this.playerEntity.openContainer.inventorySlots.size(); ++j) {
                        list1.add(this.playerEntity.openContainer.inventorySlots.get(j).getStack());
                    }

                    this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, list1);
                }
            }
        }
    }

    /**
     * Enchants the item identified by the packet given some convoluted conditions (matching window, which
     * should/shouldn't be in use?)
     */
    public void processEnchantItem(final C11PacketEnchantItem packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();

        if (this.playerEntity.openContainer.windowId == packetIn.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity) && !this.playerEntity.isSpectator()) {
            this.playerEntity.openContainer.enchantItem(this.playerEntity, packetIn.getButton());
            this.playerEntity.openContainer.detectAndSendChanges();
        }
    }

    /**
     * Update the server with an ItemStack in a slot.
     */
    public void processCreativeInventoryAction(final C10PacketCreativeInventoryAction packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (this.playerEntity.theItemInWorldManager.isCreative()) {
            final boolean flag = packetIn.getSlotId() < 0;
            final ItemStack itemstack = packetIn.getStack();

            if (itemstack != null && itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("BlockEntityTag", 10)) {
                final NBTTagCompound nbttagcompound = itemstack.getTagCompound().getCompoundTag("BlockEntityTag");

                if (nbttagcompound.hasKey("x") && nbttagcompound.hasKey("y") && nbttagcompound.hasKey("z")) {
                    final BlockPos blockpos = new BlockPos(nbttagcompound.getInteger("x"), nbttagcompound.getInteger("y"), nbttagcompound.getInteger("z"));
                    final TileEntity tileentity = this.playerEntity.worldObj.getTileEntity(blockpos);

                    if (tileentity != null) {
                        final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                        tileentity.writeToNBT(nbttagcompound1);
                        nbttagcompound1.removeTag("x");
                        nbttagcompound1.removeTag("y");
                        nbttagcompound1.removeTag("z");
                        itemstack.setTagInfo("BlockEntityTag", nbttagcompound1);
                    }
                }
            }

            final boolean flag1 = packetIn.getSlotId() >= 1 && packetIn.getSlotId() < 36 + InventoryPlayer.getHotbarSize();
            final boolean flag2 = itemstack == null || itemstack.getItem() != null;
            final boolean flag3 = itemstack == null || itemstack.getMetadata() >= 0 && itemstack.stackSize <= 64 && itemstack.stackSize > 0;

            if (flag1 && flag2 && flag3) {
                this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), itemstack);
                this.playerEntity.inventoryContainer.setCanCraft(this.playerEntity, true);
            } else if (flag && flag2 && flag3 && this.itemDropThreshold < 200) {
                this.itemDropThreshold += 20;
                final EntityItem entityitem = this.playerEntity.dropPlayerItemWithRandomChoice(itemstack, true);

                if (entityitem != null) {
                    entityitem.setAgeToCreativeDespawnTime();
                }
            }
        }
    }

    /**
     * Received in response to the server requesting to confirm that the client-side open container matches the servers'
     * after a mismatched container-slot manipulation. It will unlock the player's ability to manipulate the container
     * contents
     */
    public void processConfirmTransaction(final C0FPacketConfirmTransaction packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        final Short oshort = this.field_147372_n.lookup(this.playerEntity.openContainer.windowId);

        if (oshort != null && packetIn.getUid() == oshort.shortValue() && this.playerEntity.openContainer.windowId == packetIn.getWindowId() && !this.playerEntity.openContainer.getCanCraft(this.playerEntity) && !this.playerEntity.isSpectator()) {
            this.playerEntity.openContainer.setCanCraft(this.playerEntity, true);
        }
    }

    public void processUpdateSign(final C12PacketUpdateSign packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();
        final WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        final BlockPos blockpos = packetIn.getPosition();

        if (worldserver.isBlockLoaded(blockpos)) {
            final TileEntity tileentity = worldserver.getTileEntity(blockpos);

            if (!(tileentity instanceof TileEntitySign)) {
                return;
            }

            final TileEntitySign tileentitysign = (TileEntitySign) tileentity;

            if (!tileentitysign.getIsEditable() || tileentitysign.getPlayer() != this.playerEntity) {
                this.serverController.logWarning("Player " + this.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
                return;
            }

            final IChatComponent[] aichatcomponent = packetIn.getLines();

            for (int i = 0; i < aichatcomponent.length; ++i) {
                tileentitysign.signText[i] = new ChatComponentText(EnumChatFormatting.getTextWithoutFormattingCodes(aichatcomponent[i].getUnformattedText()));
            }

            tileentitysign.markDirty();
            worldserver.markBlockForUpdate(blockpos);
        }
    }

    /**
     * Updates a players' ping statistics
     */
    public void processKeepAlive(final C00PacketKeepAlive packetIn) {
        if (packetIn.getKey() == this.field_147378_h) {
            final int i = (int) (this.currentTimeMillis() - this.lastPingTime);
            this.playerEntity.ping = (this.playerEntity.ping * 3 + i) / 4;
        }
    }

    private long currentTimeMillis() {
        return System.nanoTime() / 1000000L;
    }

    /**
     * Processes a player starting/stopping flying
     */
    public void processPlayerAbilities(final C13PacketPlayerAbilities packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.capabilities.isFlying = packetIn.isFlying() && this.playerEntity.capabilities.allowFlying;
    }

    /**
     * Retrieves possible tab completions for the requested command string and sends them to the client
     */
    public void processTabComplete(final C14PacketTabComplete packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        final List<String> list = Lists.newArrayList();

        for (final String s : this.serverController.getTabCompletions(this.playerEntity, packetIn.getMessage(), packetIn.getTargetBlock())) {
            list.add(s);
        }

        this.playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete(list.toArray(new String[list.size()])));
    }

    /**
     * Updates serverside copy of client settings: language, render distance, chat visibility, chat colours, difficulty,
     * and whether to show the cape
     */
    public void processClientSettings(final C15PacketClientSettings packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.handleClientSettings(packetIn);
    }

    /**
     * Synchronizes serverside and clientside book contents and signing
     */
    public void processVanilla250Packet(final C17PacketCustomPayload packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if ("MC|BEdit".equals(packetIn.getChannelName())) {
            final PacketBuffer packetbuffer3 = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

            try {
                final ItemStack itemstack1 = packetbuffer3.readItemStackFromBuffer();

                if (itemstack1 != null) {
                    if (!ItemWritableBook.isNBTValid(itemstack1.getTagCompound())) {
                        throw new IOException("Invalid book tag!");
                    }

                    final ItemStack itemstack3 = this.playerEntity.inventory.getCurrentItem();

                    if (itemstack3 == null) {
                        return;
                    }

                    if (itemstack1.getItem() == Items.writable_book && itemstack1.getItem() == itemstack3.getItem()) {
                        itemstack3.setTagInfo("pages", itemstack1.getTagCompound().getTagList("pages", 8));
                    }

                    return;
                }
            } catch (final Exception exception3) {
                logger.error("Couldn't handle book info", exception3);
                return;
            } finally {
                packetbuffer3.release();
            }

            return;
        } else if ("MC|BSign".equals(packetIn.getChannelName())) {
            final PacketBuffer packetbuffer2 = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

            try {
                final ItemStack itemstack = packetbuffer2.readItemStackFromBuffer();

                if (itemstack != null) {
                    if (!ItemEditableBook.validBookTagContents(itemstack.getTagCompound())) {
                        throw new IOException("Invalid book tag!");
                    }

                    final ItemStack itemstack2 = this.playerEntity.inventory.getCurrentItem();

                    if (itemstack2 == null) {
                        return;
                    }

                    if (itemstack.getItem() == Items.written_book && itemstack2.getItem() == Items.writable_book) {
                        itemstack2.setTagInfo("author", new NBTTagString(this.playerEntity.getCommandSenderName()));
                        itemstack2.setTagInfo("title", new NBTTagString(itemstack.getTagCompound().getString("title")));
                        itemstack2.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
                        itemstack2.setItem(Items.written_book);
                    }

                    return;
                }
            } catch (final Exception exception4) {
                logger.error("Couldn't sign book", exception4);
                return;
            } finally {
                packetbuffer2.release();
            }

            return;
        } else if ("MC|TrSel".equals(packetIn.getChannelName())) {
            try {
                final int i = packetIn.getBufferData().readInt();
                final Container container = this.playerEntity.openContainer;

                if (container instanceof ContainerMerchant) {
                    ((ContainerMerchant) container).setCurrentRecipeIndex(i);
                }
            } catch (final Exception exception2) {
                logger.error("Couldn't select trade", exception2);
            }
        } else if ("MC|AdvCdm".equals(packetIn.getChannelName())) {
            if (!this.serverController.isCommandBlockEnabled()) {
                this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled"));
            } else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode) {
                final PacketBuffer packetbuffer = packetIn.getBufferData();

                try {
                    final int j = packetbuffer.readByte();
                    CommandBlockLogic commandblocklogic = null;

                    if (j == 0) {
                        final TileEntity tileentity = this.playerEntity.worldObj.getTileEntity(new BlockPos(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt()));

                        if (tileentity instanceof TileEntityCommandBlock) {
                            commandblocklogic = ((TileEntityCommandBlock) tileentity).getCommandBlockLogic();
                        }
                    } else if (j == 1) {
                        final Entity entity = this.playerEntity.worldObj.getEntityByID(packetbuffer.readInt());

                        if (entity instanceof EntityMinecartCommandBlock) {
                            commandblocklogic = ((EntityMinecartCommandBlock) entity).getCommandBlockLogic();
                        }
                    }

                    final String s1 = packetbuffer.readStringFromBuffer(packetbuffer.readableBytes());
                    final boolean flag = packetbuffer.readBoolean();

                    if (commandblocklogic != null) {
                        commandblocklogic.setCommand(s1);
                        commandblocklogic.setTrackOutput(flag);

                        if (!flag) {
                            commandblocklogic.setLastOutput(null);
                        }

                        commandblocklogic.updateCommand();
                        this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", s1));
                    }
                } catch (final Exception exception1) {
                    logger.error("Couldn't set command block", exception1);
                } finally {
                    packetbuffer.release();
                }
            } else {
                this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed"));
            }
        } else if ("MC|Beacon".equals(packetIn.getChannelName())) {
            if (this.playerEntity.openContainer instanceof ContainerBeacon) {
                try {
                    final PacketBuffer packetbuffer1 = packetIn.getBufferData();
                    final int k = packetbuffer1.readInt();
                    final int l = packetbuffer1.readInt();
                    final ContainerBeacon containerbeacon = (ContainerBeacon) this.playerEntity.openContainer;
                    final Slot slot = containerbeacon.getSlot(0);

                    if (slot.getHasStack()) {
                        slot.decrStackSize(1);
                        final IInventory iinventory = containerbeacon.func_180611_e();
                        iinventory.setField(1, k);
                        iinventory.setField(2, l);
                        iinventory.markDirty();
                    }
                } catch (final Exception exception) {
                    logger.error("Couldn't set beacon", exception);
                }
            }
        } else if ("MC|ItemName".equals(packetIn.getChannelName()) && this.playerEntity.openContainer instanceof ContainerRepair) {
            final ContainerRepair containerrepair = (ContainerRepair) this.playerEntity.openContainer;

            if (packetIn.getBufferData() != null && packetIn.getBufferData().readableBytes() >= 1) {
                final String s = ChatAllowedCharacters.filterAllowedCharacters(packetIn.getBufferData().readStringFromBuffer(32767));

                if (s.length() <= 30) {
                    containerrepair.updateItemName(s);
                }
            } else {
                containerrepair.updateItemName("");
            }
        }
    }
}
