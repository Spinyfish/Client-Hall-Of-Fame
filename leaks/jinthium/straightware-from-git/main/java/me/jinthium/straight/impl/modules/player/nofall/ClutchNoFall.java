package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.BadPacketsComponent;
import me.jinthium.straight.impl.event.game.SpoofItemEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.Render3DEvent;
import me.jinthium.straight.impl.modules.exploit.Spoofer;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.InventoryUtils;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.checkerframework.checker.units.qual.C;

@ModeInfo(name = "Clutch", parent = NoFall.class)
public class ClutchNoFall extends ModuleMode<NoFall> {
    private C08PacketPlayerBlockPlacement placePacket;
    private int customSlot = -1;
    
    @Callback
    final EventCallback<SpoofItemEvent> spoofItemEventEventCallback = event -> {
        if(customSlot != -1)
            event.setCurrentItem(customSlot);
    };
    
    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            final float minRotationSpeed = 8;
            final float maxRotationSpeed = 10;
            final float rotationSpeed = (float) MathUtils.getRandomInRange(minRotationSpeed, maxRotationSpeed);
            RotationUtils.setRotations(event, new Vector2f(mc.thePlayer.rotationYaw, 90), rotationSpeed); 
        }
    };

    @Callback
    final EventCallback<Render3DEvent> render3DEventEventCallback = event -> {

        if (mc.thePlayer.fallDistance > 3 && this.placePacket == null && PlayerUtil.isBlockUnder(15)) {
            final int slot = InventoryUtils.findItem(Items.water_bucket);

            if (slot == -1) {
                return;
            }

            customSlot = slot;

            if (mc.thePlayer.currentEvent.getPitch() > 85 && !BadPacketsComponent.bad()) {
                for (int i = 0; i < 3; i++) {
                    final Block block = MovementUtil.blockRelativeToPlayer(0, -i, 0);

                    if (block.getMaterial() == Material.water) {
                        break;
                    }

                    if (block.isFullBlock()) {
                        final BlockPos position = new BlockPos(mc.thePlayer).down(i);

                        Vec3 hitVec = new Vec3(position.getX() + Math.random(), position.getY() + Math.random(), position.getZ() + Math.random());
                        final MovingObjectPosition movingObjectPosition = 
                                RotationUtils.rayCast(new Vector2f(mc.thePlayer.currentEvent.getYaw(), mc.thePlayer.currentEvent.getPitch()), 
                                        mc.playerController.getBlockReachDistance());
                        if (movingObjectPosition != null && movingObjectPosition.getBlockPos().equals(position)) {
                            hitVec = movingObjectPosition.hitVec;
                        }

                        final float f = (float) (hitVec.xCoord - (double) position.getX());
                        final float f1 = 1.0F;
                        final float f2 = (float) (hitVec.zCoord - (double) position.getZ());

                        PacketUtil.sendPacketNoEvent(this.placePacket = new C08PacketPlayerBlockPlacement(position, EnumFacing.UP.getIndex(), mc.thePlayer.inventoryContainer.getSlot(customSlot).getStack(), f, f1, f2));
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(customSlot).getStack()));
                        customSlot = -1;
                        break;
                    }
                }
            }
        } else if (this.placePacket != null && mc.thePlayer.onGroundTicks > 1) {
            int slot = InventoryUtils.findItem(Items.bucket);

            if (slot == -1) {
                slot = InventoryUtils.findItem(Items.water_bucket);
            }

            if (slot == -1) {
                this.placePacket = null;
                return;
            }

            customSlot = slot;

            if (mc.thePlayer.currentEvent.getPitch() > 85 && !BadPacketsComponent.bad()) {
                PacketUtil.sendPacketNoEvent(this.placePacket);
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(customSlot).getStack()));
            }

            customSlot = -1;
            this.placePacket = null;
        }
    };
}
