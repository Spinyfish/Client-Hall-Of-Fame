package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.vector.Vec2f;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;

@ModuleInfo(name = "Breaker", category = Category.PLAYER)
public class BreakerModule extends Module {
    private final NumberSetting distance = new NumberSetting("Distance", 5, 1, 2, 1);
    private final BooleanSetting aim = new BooleanSetting("Aim", true);
    private final BooleanSetting destroy = new BooleanSetting("Destroy", true);
    private final BooleanSetting use = new BooleanSetting("Use", false);
    private final BooleanSetting swing = new BooleanSetting("Swing", false);
    private final BooleanSetting instant = new BooleanSetting("Instant", false);

    private BlockPos pos = null;
    private BlockPos oldPos = null;
    private int blockHitDelay = 0;
    private final TimeHelper switchTimer = new TimeHelper();
    private float currentDamage = 0F;

    public BreakerModule() {
        this.registerSettings(
                this.distance,
                this.aim,
                this.destroy,
                this.use,
                this.swing,
                this.instant
        );
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (pos != null) {
                if (mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > this.distance.getValue().doubleValue()) {
                    mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), pos, -1);
                    pos = null;
                    oldPos = null;
                    blockHitDelay = 0;
                    switchTimer.reset();
                    currentDamage = 0;
                }
            }

            if (pos == null || (mc.theWorld.getBlockState(pos).getBlock() != Blocks.bed || mc.theWorld.getBlockState(pos).getBlock() != Blocks.cake || mc.theWorld.getBlockState(pos).getBlock() != Blocks.dragon_egg) ||
                    mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > this.distance.getValue().doubleValue()) {
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

            if (this.aim.getValue()) {
                event.setPitch(getBlockRotation(block, pos).getY());
                event.setYaw(getBlockRotation(block, pos).getX());
            }

            if (this.destroy.getValue()) {
                if (this.instant.getValue()) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, EnumFacing.SOUTH));

                    if (this.swing.getValue()) {
                        mc.thePlayer.swingItem();
                    }

                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.SOUTH));
                    currentDamage = 0F;
                    return;
                }

                if (block == null) return;

                if (currentDamage == 0F && pos != null) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, EnumFacing.SOUTH));

                    if (mc.thePlayer.capabilities.isCreativeMode || block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos) >= 1.0F) {
                        if (this.swing.getValue()) {
                            mc.thePlayer.swingItem();
                        }

                        mc.playerController.onPlayerDestroyBlock(pos, EnumFacing.SOUTH);

                        currentDamage = 0F;
                        pos = null;
                        return;
                    }
                }

                if (this.swing.getValue()) {
                    mc.thePlayer.swingItem();
                }

                currentDamage += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, currentPos);
                mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), currentPos, (int) ((currentDamage * 10F) - 1));

                if (currentDamage >= 1F) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.DOWN));
                    mc.playerController.onPlayerDestroyBlock(currentPos, EnumFacing.DOWN);
                    mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), currentPos, -1);
                    blockHitDelay = 4;
                    currentDamage = 0F;
                    pos = null;
                }
            }

            if (this.use.getValue()) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, EnumFacing.DOWN, new Vec3(currentPos.getX(), currentPos.getY(), currentPos.getZ()))) {
                    if (this.swing.getValue()) {
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

        for (double x = -this.distance.getValue().doubleValue(); x < this.distance.getValue().doubleValue(); ++x) {
            for (double y = this.distance.getValue().doubleValue(); y > -this.distance.getValue().doubleValue(); --y) {
                for (double z = -this.distance.getValue().doubleValue(); z < this.distance.getValue().doubleValue(); ++z) {
                    BlockPos blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                    Block block = mc.theWorld.getBlockState(blockPos).getBlock();

                    if (block == null) continue;

                    if (block == Blocks.bed || block == Blocks.cake || block == Blocks.dragon_egg) {
                        double distance = mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
                        if (distance > this.distance.getValue().doubleValue()) continue;
                        if (nearestBlockDistance < distance) continue;
                        nearestBlockDistance = distance;
                        nearestBlock = blockPos;
                    }
                }
            }
        }

        return nearestBlock;
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

    private Vec2f getBlockRotation(Block block, BlockPos blockPos) {
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
        return new Vec2f(yaw, pitch);
    }
}
