package me.jinthium.straight.impl.modules.movement.noslowdown;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.modules.movement.NoSlowDown;
import me.jinthium.straight.impl.modules.player.Scaffold;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModeInfo(name = "Watchdog", parent = NoSlowDown.class)
public class WatchdogNoSlow extends ModuleMode<NoSlowDown> {

    private boolean usingItem;

    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> {
        usingItem = false;
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        if(event.isPre()){
            if(mc.thePlayer.isUsingItem() && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)){
                event.setPitch(90);
            }
        }
        if (event.isPost()) {
            KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
            if (mc.thePlayer.isUsingItem() || killAura.target != null) {
                if (PlayerUtil.isHoldingSword()) {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
                    }
                    usingItem = killAura.target != null;
                }
            } else if (usingItem) {
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                usingItem = false;
            }
        }
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if (event.getPacketState() == PacketEvent.PacketState.SENDING && mc.thePlayer != null) {
            KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
            if (event.getPacket() instanceof C08PacketPlayerBlockPlacement && PlayerUtil.isHoldingSword()
                    && (mc.objectMouseOver.getBlockPos() != null &&
                    !(MovementUtil.block(mc.objectMouseOver.getBlockPos()) instanceof BlockEnderChest
                            || MovementUtil.block(mc.objectMouseOver.getBlockPos()) instanceof BlockChest || MovementUtil.block(mc.objectMouseOver.getBlockPos()) instanceof BlockSign))
                    && !Client.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled())

                event.cancel();
            else if (event.getPacket() instanceof C07PacketPlayerDigging c07 && killAura.target != null)
                if(c07.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)
                    event.cancel();
        }
    };

}
