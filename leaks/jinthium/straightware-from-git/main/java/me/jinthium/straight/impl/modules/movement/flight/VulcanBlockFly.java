package me.jinthium.straight.impl.modules.movement.flight;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.event.Event;
import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.game.SpoofItemEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.InventoryUtils;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModeInfo(name = "Vulcan Block", parent = Flight.class)
public class VulcanBlockFly extends ModuleMode<Flight> {

    private boolean placedBlock;
    private int spoofSlot = -1, oldSlot = -1;

    @Override
    public void onEnable() {
        spoofSlot = -1;
        oldSlot = mc.thePlayer != null ? mc.thePlayer.inventory.currentItem : -1;
        placedBlock = false;
//        if (mc.thePlayer != null) {
//            if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
//                Client.INSTANCE.getNotificationManager().post(this.getName(), "You must be holding a block", NotificationType.ERROR);
//                this.getParent().toggle();
//            }
//        }
        super.onEnable();
    }

    @Callback
    final EventCallback<PlayerMoveEvent> playerMoveEventEventCallback = event -> {
        if (placedBlock) {
            if (mc.thePlayer.onGround)
                event.setY(mc.thePlayer.motionY = 4.3f);

            if (FallDistanceComponent.distance > 0 && mc.thePlayer.ticksExisted % 2 == 0) {
                event.setY(mc.thePlayer.motionY = -0.09701);
            } else if (FallDistanceComponent.distance > 0) {
                event.setY(mc.thePlayer.motionY *= 1);
            }
        }
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if (event.isPre()) {
            if (FallDistanceComponent.distance > 0 && mc.thePlayer.ticksExisted % 16 == 0) {
                event.setOnGround(true);
                mc.thePlayer.speedInAir = 0.025f;
            } else if (FallDistanceComponent.distance > 0) {
                event.setOnGround(false);
                mc.thePlayer.speedInAir = 0.02f;
            }

            if (!placedBlock) {
                for(int i = 0; i < 9; i++){
                    ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                    if(slot != null && slot.getItem() instanceof ItemBlock && spoofSlot == -1)
                        spoofSlot = i;
                }

                if(spoofSlot == -1)
                    return;

                mc.thePlayer.inventory.currentItem = spoofSlot;

              // PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition().down(), 255, null, 0, -1, 0));

                RotationUtils.setRotations(event, new float[]{event.getYaw(), 45}, 50);
                MovingObjectPosition rayCastedObj = RotationUtils.rayCast(new Vector2f(event.getYaw(), event.getPitch()), mc.playerController.getBlockReachDistance());

                if (rayCastedObj == null)
                    return;

                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(),
                        rayCastedObj.getBlockPos(), rayCastedObj.sideHit, rayCastedObj.hitVec)) {

                    PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                    placedBlock = true;
                    mc.thePlayer.inventory.currentItem = oldSlot;
                }
            }
        }
    };
}
