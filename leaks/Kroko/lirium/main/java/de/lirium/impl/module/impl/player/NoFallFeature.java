package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.misc.ServerUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@ModuleFeature.Info(name = "No Fall", description = "Prevents getting fall damage", category = ModuleFeature.Category.PLAYER)
public class NoFallFeature extends ModuleFeature {

    @Value(name = "Mode")
    final ComboBox<String> mode = new ComboBox<>("Sentinel", new String[]{"WatchDog", "MLG", "Vanilla"});

    private boolean hasSet, placed;
    private float lastPitch;
    private int lastSlot;

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        setSuffix(mode.getValue());
        switch (mode.getValue()) {
            case "MLG":
                if (mc.player.onGround) {
                    if (hasSet) {
                        mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                        mc.player.rotationPitch = lastPitch;
                        mc.player.inventory.currentItem = lastSlot;
                        hasSet = false;
                        placed = false;
                    }
                } else {
                    if (mc.player.fallDistance < 5) return;
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, mc.player.motionY * 3.0, 0)).isEmpty()) {
                        int slot = -1;
                        for (int i = 0; i < 9; i++) {
                            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
                            if (stack == ItemStack.field_190927_a) continue;
                            if (stack.getItem() != Items.WATER_BUCKET) continue;
                            slot = i;
                            break;
                        }
                        if (slot == -1) return;
                        if (!hasSet) {
                            lastSlot = mc.player.inventory.currentItem;
                            lastPitch = mc.player.rotationPitch;
                            placed = false;
                            hasSet = true;
                        }
                        mc.player.inventory.currentItem = slot;
                        mc.player.rotationPitch = 90;
                        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, mc.player.motionY * 2.0, 0)).isEmpty()) {
                            mc.player.setPosition(mc.player.lastTickPosX + (((int) getX() + 0.5) - getX()),
                                    getY(), mc.player.lastTickPosZ + (((int) getZ() + 0.5) - getZ()));
                        }
                        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, mc.player.motionY * 1.5, 0)).isEmpty() && !placed) {
                            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                            mc.player.motionX = mc.player.motionZ = 0;
                            placed = true;
                        }
                    }
                }
                break;
            case "Sentinel":
                doSentinel();
                break;
        }
    };

    @EventHandler
    public final Listener<PacketEvent> packetEvent = e -> {
        switch (mode.getValue()) {
            case "Vanilla":
                if (e.packet instanceof CPacketPlayer) {
                    final CPacketPlayer packet = (CPacketPlayer) e.packet;
                    packet.onGround = true;
                    getPlayer().fallDistance = 0;
                }
                break;
            case "WatchDog":
                if (getPlayer().fallDistance > 2 && getPlayer().ticksExisted > 20 && !getPlayer().onGround) {
                    if (e.packet instanceof CPacketPlayer) {
                        final CPacketPlayer packetPlayer = (CPacketPlayer) e.packet;
                        packetPlayer.onGround = true;
                        getPlayer().fallDistance = 0;
                    }
                    if (e.packet instanceof CPacketConfirmTransaction) {
                        e.setCancelled(true);
                    }
                }
                break;
        }
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doSentinel() {
        if (!ServerUtil.isCubeCraft())
            return;
        if (mc.player.fallDistance > 2) {
            if (!(mc.player.inventory.getCurrentItem().getItem() instanceof ItemBlock))
                return;
            //mc.player.motionX = 0;
            //mc.player.motionZ = 0;

            sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            sendPacket(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player.posX, mc.player.posY + mc.player.motionY, mc.player.posZ), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0F, 0.94F, 0F));
            //addPosition(0,-mc.player.motionY * 0.2,0);
            mc.player.motionY *= 0.95;
            sendPacket(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player.posX, mc.player.posY + mc.player.motionY, mc.player.posZ), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0F, 0.94F, 0F));
            sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            mc.player.fallDistance = 0;
        }
    }
}
