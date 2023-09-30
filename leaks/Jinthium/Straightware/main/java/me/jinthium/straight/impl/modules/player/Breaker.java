package me.jinthium.straight.impl.modules.player;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.event.render.Render3DEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import me.jinthium.straight.impl.utils.vector.Vector3d;
import me.jinthium.straight.impl.utils.vector.Vector4d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.checkerframework.checker.units.qual.A;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Breaker extends Module {
    private static final EnumFacing[] DIRECTIONS = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST};

    private final NumberSetting distance = new NumberSetting("Distance", 5, 1, 5, 1);
    private final BooleanSetting aim = new BooleanSetting("Aim", true);
    private final BooleanSetting destroy = new BooleanSetting("Destroy", true);
    private final BooleanSetting use = new BooleanSetting("Use", false);
    private final BooleanSetting swing = new BooleanSetting("Swing", false);
    private final BooleanSetting instant = new BooleanSetting("Instant", false);
    private Vector3d home;

    private BlockPos pos = null;
    private BlockPos oldPos = null;
    private int blockHitDelay = 0;
    private final TimerUtil switchTimer = new TimerUtil();
    private float currentDamage = 0F;

    public Breaker() {
        super("Breaker", Category.PLAYER);
        this.addSettings(distance, aim, destroy, use, swing, instant);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if (event.isPre()) {
            if(home != null && mc.thePlayer.getDistanceSq(home.getX(), home.getY(), home.getZ()) < 35 * 35)
                return;

            if (pos != null) {
                if (mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > this.distance.getValue()) {
                    mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), pos, -1);
                    pos = null;
                    oldPos = null;
                    blockHitDelay = 0;
                    switchTimer.reset();
                    currentDamage = 0;
                }
            }

            if (pos == null || (mc.theWorld.getBlockState(pos).getBlock() != Blocks.bed || mc.theWorld.getBlockState(pos).getBlock() != Blocks.cake || mc.theWorld.getBlockState(pos).getBlock() != Blocks.dragon_egg) ||
                    mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > this.distance.getValue()) {
                pos = find();
            }

            if (pos == null) {
                currentDamage = 0F;
                return;
            }

            BlockPos currentPos = pos;

            if (oldPos != null && oldPos != currentPos) {
                if (currentDamage <= 0) {
                    currentDamage = 0F;
                    switchTimer.reset();
                }
            }

            oldPos = currentPos;

            if (blockHitDelay > 0) {
                blockHitDelay--;
                return;
            }
            Block block = mc.theWorld.getBlockState(currentPos).getBlock();

            if (this.aim.isEnabled()) {
                RotationUtils.setRotations(event, getBlockRotation(block, pos), 15);
//                event.setPitch(getBlockRotation(block, pos).getY());
//                event.setYaw(getBlockRotation(block, pos).getX());
            }

            if (this.destroy.isEnabled()) {
                if (this.instant.isEnabled()) {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, EnumFacing.SOUTH));

                    if (this.swing.isEnabled()) {
                        mc.thePlayer.swingItem();
                    }

                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.SOUTH));
                    currentDamage = 0F;
                    return;
                }

                if (block == null) return;

                if (currentDamage == 0F && pos != null) {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, EnumFacing.SOUTH));

                    if (mc.thePlayer.capabilities.isCreativeMode || block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos) >= 1.0F) {
                        if (this.swing.isEnabled()) {
                            mc.thePlayer.swingItem();
                        }

                        mc.playerController.onPlayerDestroyBlock(pos, EnumFacing.SOUTH);

                        currentDamage = 0F;
                        pos = null;
                        return;
                    }
                }

                if (this.swing.isEnabled()) {
                    mc.thePlayer.swingItem();
                }

                currentDamage += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, currentPos);
                mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), currentPos, (int) ((currentDamage * 10F) - 1));

                if (currentDamage >= 1F) {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.DOWN));
                    mc.playerController.onPlayerDestroyBlock(currentPos, EnumFacing.DOWN);
                    mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), currentPos, -1);
                    blockHitDelay = 4;
                    currentDamage = 0F;
                    pos = null;
                }
            }

            if (this.use.isEnabled()) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, EnumFacing.DOWN, new Vec3(currentPos.getX(), currentPos.getY(), currentPos.getZ()))) {
                    if (this.swing.isEnabled()) {
                        mc.thePlayer.swingItem();
                    }

                    blockHitDelay = 4;
                    currentDamage = 0F;
                    pos = null;
                }
            }
        }
    };

    private BlockPos find() {
        double nearestBlockDistance = Double.MAX_VALUE;
        BlockPos nearestBlock = null;

        for (double x = -this.distance.getValue(); x < this.distance.getValue(); ++x) {
            for (double y = this.distance.getValue(); y > -this.distance.getValue(); --y) {
                for (double z = -this.distance.getValue(); z < this.distance.getValue(); ++z) {
                    BlockPos blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                    Block block = mc.theWorld.getBlockState(blockPos).getBlock();

                    if (block == null) continue;

                    if (block != Blocks.cake && block != Blocks.bed) {
                        continue;
                    }

                    if(!isOpen(blockPos)){
                        for (EnumFacing direction : DIRECTIONS) {
                            BlockPos toCheck = blockPos.offset(direction);
                            if (!(mc.theWorld.getBlockState(toCheck).getBlock() instanceof BlockAir)) {
                                BlockPos preventingBlock = blockPos.offset(direction);
                                double distance = mc.thePlayer.getDistance(preventingBlock.getX() + 0.5, preventingBlock.getY() + 0.5, preventingBlock.getZ() + 0.5);
                                if (distance > this.distance.getValue()) continue;
                                if (nearestBlockDistance < distance) continue;
                                nearestBlockDistance = distance;
                                nearestBlock = preventingBlock;
                            }
                        }
                    }else {
                            double distance = mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
                            if (distance > this.distance.getValue()) continue;
                            if (nearestBlockDistance < distance) continue;
                            nearestBlockDistance = distance;
                            nearestBlock = blockPos;
                    }
                }
            }
        }

        return nearestBlock;
    }


    private boolean isOpen(BlockPos blockPos) {
        for (EnumFacing direction : DIRECTIONS) {
            BlockPos toCheck = blockPos.offset(direction);
            if (mc.theWorld.getBlockState(toCheck).getBlock() instanceof BlockAir)
                return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (pos != null) {
            mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), pos, -1);
            pos = null;
        }
        oldPos = null;
        blockHitDelay = 0;
        switchTimer.reset();
        currentDamage = 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        pos = null;
        oldPos = null;
        blockHitDelay = 0;
        switchTimer.reset();
        currentDamage = 0;
    }

    private Vector2f getBlockRotation(Block block, BlockPos blockPos) {
        double py = (180 / Math.PI);
        Vec3 vector = new Vec3(
                blockPos.getX() + 0.5 - mc.thePlayer.posX,
                (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()),
                blockPos.getZ() + 0.5 - mc.thePlayer.posZ
        );

        AxisAlignedBB axisAlignedBB = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()), block.getDefaultState()).expand(0, -0.3, 0);
        double yDistance;
        if (vector.yCoord > axisAlignedBB.maxY) {
            yDistance = axisAlignedBB.maxY - vector.yCoord;
        } else if (vector.yCoord < axisAlignedBB.minY) {
            yDistance = axisAlignedBB.minY - vector.yCoord;
        } else {
            yDistance = 0;
        }

        double squareRoot = MathHelper.sqrt_double((vector.xCoord * vector.xCoord) + (vector.zCoord * vector.zCoord));

        float yaw = (float) (Math.atan2(vector.zCoord, vector.xCoord) * py) - 90;
        float pitch = (float) -(Math.atan2(yDistance, squareRoot) * py);
        return new Vector2f(yaw, pitch);
    }


    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> {

        final double distance = mc.thePlayer.getDistance(event.getPosX(), event.getPosY(), event.getPosZ());
        if (distance > 40) {
            home = new Vector3d(event.getPosX(), event.getPosY(), event.getPosZ());
        }
    };
}