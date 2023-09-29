package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class WorldManager implements IWorldAccess {
    /**
     * Reference to the MinecraftServer object.
     */
    private final MinecraftServer mcServer;

    /**
     * The WorldServer object.
     */
    private final WorldServer theWorldServer;

    public WorldManager(final MinecraftServer p_i1517_1_, final WorldServer p_i1517_2_) {
        this.mcServer = p_i1517_1_;
        this.theWorldServer = p_i1517_2_;
    }

    public void spawnParticle(final int particleID, final boolean ignoreRange, final double xCoord, final double yCoord, final double zCoord, final double xOffset, final double yOffset, final double zOffset, final int... p_180442_15_) {
    }

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    public void onEntityAdded(final Entity entityIn) {
        this.theWorldServer.getEntityTracker().trackEntity(entityIn);
    }

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    public void onEntityRemoved(final Entity entityIn) {
        this.theWorldServer.getEntityTracker().untrackEntity(entityIn);
        this.theWorldServer.getScoreboard().func_181140_a(entityIn);
    }

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    public void playSound(final String soundName, final double x, final double y, final double z, final float volume, final float pitch) {
        this.mcServer.getConfigurationManager().sendToAllNear(x, y, z, volume > 1.0F ? (double) (16.0F * volume) : 16.0D, this.theWorldServer.provider.getDimensionId(), new S29PacketSoundEffect(soundName, x, y, z, volume, pitch));
    }

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(final EntityPlayer except, final String soundName, final double x, final double y, final double z, final float volume, final float pitch) {
        this.mcServer.getConfigurationManager().sendToAllNearExcept(except, x, y, z, volume > 1.0F ? (double) (16.0F * volume) : 16.0D, this.theWorldServer.provider.getDimensionId(), new S29PacketSoundEffect(soundName, x, y, z, volume, pitch));
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    public void markBlockRangeForRenderUpdate(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
    }

    public void markBlockForUpdate(final BlockPos pos) {
        this.theWorldServer.getPlayerManager().markBlockForUpdate(pos);
    }

    public void notifyLightSet(final BlockPos pos) {
    }

    public void playRecord(final String recordName, final BlockPos blockPosIn) {
    }

    public void playAuxSFX(final EntityPlayer player, final int sfxType, final BlockPos blockPosIn, final int p_180439_4_) {
        this.mcServer.getConfigurationManager().sendToAllNearExcept(player, blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ(), 64.0D, this.theWorldServer.provider.getDimensionId(), new S28PacketEffect(sfxType, blockPosIn, p_180439_4_, false));
    }

    public void broadcastSound(final int p_180440_1_, final BlockPos p_180440_2_, final int p_180440_3_) {
        this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S28PacketEffect(p_180440_1_, p_180440_2_, p_180440_3_, true));
    }

    public void sendBlockBreakProgress(final int breakerId, final BlockPos pos, final int progress) {
        for (final EntityPlayerMP entityplayermp : this.mcServer.getConfigurationManager().func_181057_v()) {
            if (entityplayermp != null && entityplayermp.worldObj == this.theWorldServer && entityplayermp.getEntityId() != breakerId) {
                final double d0 = (double) pos.getX() - entityplayermp.posX;
                final double d1 = (double) pos.getY() - entityplayermp.posY;
                final double d2 = (double) pos.getZ() - entityplayermp.posZ;

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
                    entityplayermp.playerNetServerHandler.sendPacket(new S25PacketBlockBreakAnim(breakerId, pos, progress));
                }
            }
        }
    }
}
