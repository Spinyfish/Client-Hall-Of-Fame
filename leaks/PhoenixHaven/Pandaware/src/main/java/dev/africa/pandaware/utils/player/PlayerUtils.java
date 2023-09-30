package dev.africa.pandaware.utils.player;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.event.player.CollisionEvent;
import dev.africa.pandaware.impl.module.movement.TargetStrafeModule;
import dev.africa.pandaware.utils.network.ProtocolUtils;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PlayerUtils implements MinecraftInstance {
    private final OldServerPinger oldServerPinger = new OldServerPinger();

    public int getPing(EntityPlayer player) {
        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(player.getGameProfile().getId());

        if (playerInfo == null) return -1;

        return playerInfo.getResponseTime();
    }

    public boolean isBlockUnder() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty())
                return true;
        }
        return false;
    }

    public boolean isBlockUnderNoCollisions() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            BlockPos blockPos = new BlockPos(mc.thePlayer.posX, offset, mc.thePlayer.posZ);

            if (mc.theWorld.getBlockState(blockPos).getBlock() != Blocks.air) {
                return true;
            }
        }
        return false;
    }

    public String getServerIP() {
        if (mc.getCurrentServerData() != null)
            return mc.getCurrentServerData().serverIP;

        return "Singleplayer";
    }

    public double getJumpBoostMotion() {
        if (mc.thePlayer.isPotionActive(Potion.jump))
            return (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;

        return 0;
    }

    public boolean isBlockAbove(float offset) {
        boolean liquid = false;
        final int y = (int) (mc.thePlayer.getEntityBoundingBox().maxY);
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper
                    .floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!(block instanceof BlockAir)) {
                    liquid = true;
                }
            }
        }
        return liquid;
    }

    public static List<EntityPlayer> getPlayerList() {
        NetHandlerPlayClient var4 = Minecraft.getMinecraft().thePlayer.sendQueue;
        List<EntityPlayer> list = new ArrayList<>();
        List<?> players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(var4.getPlayerInfoMap());
        for (Object o : players) {
            NetworkPlayerInfo info = (NetworkPlayerInfo) o;
            if (info == null) {
                continue;
            }
            list.add(Minecraft.getMinecraft().theWorld.getPlayerEntityByName(info.getGameProfile().getName()));
        }
        return list;
    }

    public boolean isMathGround() {
        return mc.thePlayer.posY % 0.015625 == 0;
    }

    public boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public double getDistance(BlockPos one, BlockPos two) {
        double d0 = one.getX() - two.getX();
        double d1 = one.getY() - two.getY();
        double d2 = one.getZ() - two.getZ();
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public boolean inLiquid() {
        return checkWithLiquid(0);
    }

    public boolean onLiquid() {
        return checkWithLiquid(0.01f);
    }

    public boolean checkWithLiquid(float offset) {
        boolean liquid = false;
        final int y = (int) (mc.thePlayer.getEntityBoundingBox().minY - offset);

        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper
                    .floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();

                if (!(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    liquid = true;
                }
            }
        }

        return liquid;
    }

    public EntityLivingBase rayCast(double reachDistance, float yaw, float pitch) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            Vec3 position = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
            Vec3 lookVector = mc.thePlayer.getVectorForRotation(pitch, yaw);
            Entity pointedEntity = null;

            for (Entity currentEntity : mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().addCoord(lookVector.xCoord * reachDistance, lookVector.yCoord * reachDistance, lookVector.zCoord * reachDistance).expand(1, 1, 1))) {
                if (currentEntity.canBeCollidedWith() && !currentEntity.isEntityEqual(mc.thePlayer)) {
                    MovingObjectPosition objPosition = currentEntity.getEntityBoundingBox().expand(currentEntity.getCollisionBorderSize(), currentEntity.getCollisionBorderSize(), currentEntity.getCollisionBorderSize())
                            .contract(0.1, 0.1, 0.1).calculateIntercept(position, position
                                    .addVector(lookVector.xCoord * reachDistance, lookVector.yCoord * reachDistance, lookVector.zCoord * reachDistance));
                    if (objPosition != null) {
                        double distance = position.distanceTo(objPosition.hitVec);
                        if (distance < reachDistance) {
                            if (currentEntity == mc.thePlayer.ridingEntity &&
                                    !(Reflector.ForgeEntity_canRiderInteract.exists()
                                            && Reflector.callBoolean(currentEntity,
                                            Reflector.ForgeEntity_canRiderInteract)) && reachDistance == 0.0D) {
                                pointedEntity = currentEntity;
                            } else {
                                pointedEntity = currentEntity;
                                reachDistance = distance;
                            }
                        }
                    }
                }
            }

            if (pointedEntity != null && !pointedEntity.isEntityEqual(mc.thePlayer) && (pointedEntity instanceof EntityLivingBase)) {
                return (EntityLivingBase) pointedEntity;
            }
        }

        return null;
    }

    public boolean isStrafing() {
        return TargetStrafeModule.isStrafing() || mc.thePlayer.moveStrafing != 0;
    }

    public static boolean isTeam(EntityPlayer entity) {
        return mc.thePlayer.isOnSameTeam(entity);
//        if (entity.getTeam() != null && mc.thePlayer.getTeam() != null) {
//            char c1 = entity.getDisplayName().getFormattedText().charAt(1);
//            char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
//            return c1 == c2;
//        } else {
//            return false;
//        }
    }

    public void setCollisionGround(CollisionEvent event) {
        if (event.getBlock() == Blocks.air) {
            event.setCollisionBox(new AxisAlignedBB(-100, -2, -100, 100, 1, 100)
                    .offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));
        }
    }

    public void attackEntityProtocol(Entity entity, boolean swing, boolean keepSprint) {
        if (ProtocolUtils.isOneDotEight() && swing) {
            mc.thePlayer.swingItem();
        }

        if (keepSprint) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C02PacketUseEntity(
                    entity, C02PacketUseEntity.Action.ATTACK
            ));
        } else {
            mc.playerController.attackEntity(mc.thePlayer, entity);
        }

        if (!ProtocolUtils.isOneDotEight() && swing) {
            mc.thePlayer.swingItem();
        }
    }

    public double findGround() {
        double y = mc.thePlayer.posY;
        AxisAlignedBB playerBoundingBox = mc.thePlayer.getEntityBoundingBox();
        double blockHeight = 1.0;
        for (double ground = y; ground > 0.0; ground -= blockHeight) {
            AxisAlignedBB customBox = new AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight,
                    playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ);
            if (!mc.theWorld.checkBlockCollision(customBox)) continue;
            if (blockHeight <= 0.05) {
                return ground + blockHeight;
            }
            ground += blockHeight;
            blockHeight = 0.05;
        }
        return 0.0;
    }

    public static List<EntityPlayer> getPlayers() {
        List<NetworkPlayerInfo> list = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
        List<EntityPlayer> players = new ArrayList<>();
        for (NetworkPlayerInfo player : list) {
            if (player != null) {
                players.add(mc.theWorld.getPlayerEntityByName(player.getGameProfile().getName()));
            }
        }
        return players;
    }
}
