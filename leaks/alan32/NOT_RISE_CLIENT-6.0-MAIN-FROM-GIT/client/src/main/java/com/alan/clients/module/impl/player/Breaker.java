package com.alan.clients.module.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.util.RayCastUtil;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.player.SlotUtil;
import com.alan.clients.util.rotation.RotationUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ListValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@Rise
@ModuleInfo(name = "module.player.breaker.name", description = "module.player.breaker.description", category = Category.PLAYER)
public class Breaker extends Module {

    public final BooleanValue bed = new BooleanValue("Bed", this, true);

    public final BooleanValue instant = new BooleanValue("Instant", this, true);
    public final BooleanValue throughWalls = new BooleanValue("Through Walls", this, true);
    private final BooleanValue emptySurrounding = new BooleanValue("Empty Surrounding", this, false, () -> !throughWalls.getValue());

    public final BooleanValue rotations = new BooleanValue("Rotate", this, true);
    public final BooleanValue whiteListOwnBed = new BooleanValue("Whitelist Own Bed", this, true);
    private final ListValue<MovementFix> movementCorrection = new ListValue<>("Movement Correction", this);
    private Vector3d block, lastBlock, home;
    private double damage;

    public Breaker() {
        for (MovementFix movementFix : MovementFix.values()) {
            movementCorrection.add(movementFix);
        }

        movementCorrection.setDefault(MovementFix.OFF);
    }

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        lastBlock = block;
        block = this.block();

        if (block == null) {
            return;
        }

        if (this.rotations.getValue()) {
            this.rotate();
        }

        if (lastBlock == null || !lastBlock.equals(block)) {
            damage = 0;
        }

        this.destroy();
    };

    public void rotate() {
        final Vector2f rotations = RotationUtil.calculate(block);
        RotationComponent.setRotations(rotations, 5, movementCorrection.getValue());
    }

    public Vector3d block() {
        if (home != null && mc.thePlayer.getDistanceSq(home.getX(), home.getY(), home.getZ()) < 35 * 35 && whiteListOwnBed.getValue()) {
            return null;
        }

        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {

                    final Block block = PlayerUtil.blockRelativeToPlayer(x, y, z);
                    final Vector3d position = new Vector3d(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);

                    if (!(block instanceof BlockBed)) {
                        continue;
                    }

                    /* Grab moving object position */
                    final MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationUtil.calculate(position), 4.5f);
                    if (movingObjectPosition == null || movingObjectPosition.hitVec.distanceTo(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) > 4.5) {
                        continue;
                    }

                    if (!throughWalls.getValue()) {
                        final BlockPos blockPos = movingObjectPosition.getBlockPos();
                        if (!blockPos.equalsVector(position)) {
                            continue;
                        }
                    } else if (emptySurrounding.getValue()) {
                        Vector3d addVec = position;
                        double hardness = Double.MAX_VALUE;
                        boolean empty = false;

                        for (int addX = -1; addX <= 1; addX++) {
                            for (int addY = 0; addY <= 1; addY++) {
                                for (int addZ = -1; addZ <= 1; addZ++) {
                                    if (empty || (mc.thePlayer.getDistanceSq(position.getX() + addX, position.getY() + addY, position.getZ() + addZ) + 4 > 4.5 * 4.5))
                                        continue;

                                    if (Math.abs(addX) + Math.abs(addY) + Math.abs(addZ) != 1) {
                                        continue;
                                    }

                                    Block possibleBlock = PlayerUtil.block(position.getX() + addX, position.getY() + addY, position.getZ() + addZ);

                                    if (possibleBlock instanceof BlockBed) {
                                        continue;
                                    } else if (possibleBlock instanceof BlockAir) {
                                        empty = true;
                                        continue;
                                    }

                                    double possibleHardness = possibleBlock.getBlockHardness();

                                    if (possibleHardness < hardness) {
                                        hardness = possibleHardness;
                                        addVec = position.add(addX, addY, addZ);
                                    }
                                }
                            }
                        }

                        if (!empty) {
                            if (addVec.equals(position)) {
                                return null;
                            } else {
                                return addVec;
                            }
                        }
                    }

                    return position;
                }
            }
        }

        return null;
    }

    public void updateDamage(final BlockPos blockPos, final double hardness) {
        damage += hardness;
        mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), blockPos, (int) (damage * 10 - 1));
    }

    public void destroy() {
        final BlockPos blockPos = new BlockPos(block.getX(), block.getY(), block.getZ());
        final double hardness = SlotUtil.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos, SlotComponent.getItemIndex());

        if (instant.getValue()) {
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
            mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);

        } else {
            if (damage <= 0) {
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));

                if (hardness >= 1) {
                    mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
                    damage = 0;
                }

                this.updateDamage(blockPos, hardness);
            } else if (damage > 1) {
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
                mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
                damage = 0;
                this.updateDamage(blockPos, hardness);
            } else {
                this.updateDamage(blockPos, hardness);
            }

            mc.thePlayer.swingItem();
        }
    }

    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {

        final double distance = mc.thePlayer.getDistance(event.getPosX(), event.getPosY(), event.getPosZ());

        if (distance > 40) {
            home = new Vector3d(event.getPosX(), event.getPosY(), event.getPosZ());
        }
    };
}