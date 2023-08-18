package me.jinthium.straight.impl.components;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.SpoofItemEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.InventoryUtils;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class ItemDamageComponent extends Component {

    public static boolean active;
    private static boolean stop;
    private static int slot, time, currentSlot;
    public static Type type;


    @Callback
    final EventCallback<SpoofItemEvent> spoofItemEventCallback = event -> {
        if(currentSlot != -1 && active)
            event.setCurrentItem(currentSlot);
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {

        if (active) {
            final int bowSlot = InventoryUtils.findItem(Items.bow);
            final int rodSlot = InventoryUtils.findItem(Items.fishing_rod);
            final int snowBall = InventoryUtils.findItem(Items.snowball);
            final int egg = InventoryUtils.findItem(Items.egg);

            if (bowSlot != -1 && arrow()) {
                currentSlot = bowSlot;
                type = Type.BOW;
            } else if (rodSlot != -1) {
                currentSlot = rodSlot;
                type = Type.ROD;
            } else if (snowBall != -1) {
                currentSlot = snowBall;
                type = Type.PROJECTILES;
            } else if (egg != -1) {
                currentSlot = egg;
                type = Type.PROJECTILES;
            }

            if (BadPacketsComponent.bad(true, false, false, false, true)) {
                time = 0;
            }

            if (!BadPacketsComponent.bad(true, false, false, false, true)) {
                time++;

                if (type != null) {
                    switch (type) {
                        case BOW -> {
                            switch (time) {
                                case 3 ->
                                        PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(currentSlot).getStack()));
                                case 13 ->
                                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                case 40 -> active = false;
                            }
                        }
                        case ROD -> {
                            switch (time) {
                                case 3 ->
                                        PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(currentSlot).getStack()));
                                case 165 -> active = false;
                            }
                            if (mc.thePlayer.hurtTime == 9) {
                                PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(currentSlot).getStack()));
                            }
                        }
                        case PROJECTILES -> {
                            switch (time) {
                                case 3, 4, 5, 6 ->
                                        PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(currentSlot).getStack()));
                                case 100 -> active = false;
                            }
                        }
                        default -> active = false;
                    }
                } else {
                    active = false;
                }

                if (mc.thePlayer.hurtTime == 9) {
                    active = false;
                }

                stop = true;
            }
        }
    };

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        if (stop && active) {
            event.setForward(0);
            event.setStrafe(0);
        }

    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if (active && event.isPre())
            RotationUtils.setRotations(event, new Vector2f(mc.thePlayer.rotationYaw, -90), 5);
    };

    public static void damage(final boolean stop) {
        active = true;
        ItemDamageComponent.stop = stop;
        currentSlot = mc.thePlayer.inventory.currentItem;
        time = 0;
        type = null;
    }

    public boolean arrow() {
        for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
            final ItemStack stack = mc.thePlayer.inventory.mainInventory[i];

            if (stack != null && stack.getItem().getUnlocalizedName().contains("arrow")) {
                return true;
            }
        }

        return false;
    }

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(!(event.getPacketState() == PacketEvent.PacketState.RECEIVING))
            return;

        final Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity wrapper) {
            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
                active = false;
            }
        } else if (packet instanceof S27PacketExplosion) {
            active = false;
        }
    };

    public enum Type {
        BOW,
        ROD,
        PROJECTILES
    }
}
