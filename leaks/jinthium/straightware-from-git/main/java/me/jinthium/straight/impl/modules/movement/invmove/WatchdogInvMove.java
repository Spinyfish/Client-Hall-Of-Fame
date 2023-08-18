package me.jinthium.straight.impl.modules.movement.invmove;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.movement.InventoryMove;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.BlockPos;

import java.util.concurrent.ConcurrentLinkedQueue;

@ModeInfo(name = "Watchdog", parent = InventoryMove.class)
public class WatchdogInvMove extends ModuleMode<InventoryMove> {

    private C08PacketPlayerBlockPlacement chestPacket;
    private boolean active, exempt;
    private final ConcurrentLinkedQueue<Packet<?>> invPackets = new ConcurrentLinkedQueue<>();
    private int delay;

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        Packet<?> p = event.getPacket();
        switch (event.getPacketState()) {
            case SENDING -> {
                if(p instanceof C03PacketPlayer) {
                    if(delay > 0){
                        event.cancel();
                        invPackets.add(p);
                        delay--;
                    }

                    while(delay == 0 && invPackets.size() > 0)
                        PacketUtil.sendPacketNoEvent(invPackets.poll());
                }
                if (p instanceof C08PacketPlayerBlockPlacement wrapper) {
                    BlockPos position = wrapper.getPosition();
                    if (wrapper.getPlacedBlockDirection() != 255 && MovementUtil.block(position) instanceof BlockChest) {
                        this.chestPacket = wrapper;
                        this.exempt = false;
                    }
                }

                if (p instanceof C0EPacketClickWindow) {
                    if (mc.currentScreen instanceof GuiChest) {
                        if (this.chestPacket != null) {
                            delay = 7;
                        }
                    } else {

                        PacketUtil.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));

                        PacketUtil.sendPacketNoEvent(p);
                        event.cancel();

                        PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));

                    }
                }

                if (p instanceof C0DPacketCloseWindow) {
                    this.active = false;
                    this.exempt = true;
                }
            }
            case RECEIVING -> {
                if (p instanceof S2DPacketOpenWindow wrapper) {
                    if (wrapper.getWindowTitle().getUnformattedText().contains("Chest") || wrapper.getWindowTitle().getUnformattedText().contains("LOW")) {
                        if (this.exempt) {
                            event.cancel();
                            return;
                        }

                        if (this.active) {
                            event.cancel();
                        } else {
                            this.active = true;
                        }
                    }
                }

                if (p instanceof S2EPacketCloseWindow) {
                    this.active = false;
                }
            }
        }
    };

    @Callback
    final EventCallback<WorldEvent> worldEventEventCallback = event -> {
        delay = 0;
        invPackets.clear();
    };
}
