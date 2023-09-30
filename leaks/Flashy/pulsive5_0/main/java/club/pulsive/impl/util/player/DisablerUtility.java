package club.pulsive.impl.util.player;

import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.util.network.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.concurrent.ConcurrentLinkedQueue;


public class DisablerUtility implements MinecraftUtil {
    boolean isCraftingItem;
    boolean disableInventory = false;
    public ConcurrentLinkedQueue<C0EPacketClickWindow> clickWindowPackets = new ConcurrentLinkedQueue<>();
    public void doInvMove(PacketEvent e) {
        if(e.getEventState() == PacketEvent.EventState.SENDING) {
            if (e.getPacket() instanceof C16PacketClientStatus) {
                C16PacketClientStatus clientStatus = ((C16PacketClientStatus) e.getPacket());
                if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                    e.setCancelled(true);
                }
            }


            if (e.getPacket() instanceof C0DPacketCloseWindow) {
                C0DPacketCloseWindow closeWindow = ((C0DPacketCloseWindow) e.getPacket());
                if (closeWindow.getWindowId() == 0) {
                    if (isCraftingItem) {
                        isCraftingItem = false;
                    }
                    e.setCancelled(true);
                }
            }

            if (e.getPacket() instanceof C0EPacketClickWindow) {
                C0EPacketClickWindow clickWindow = ((C0EPacketClickWindow) e.getPacket());
                if (clickWindow.getWindowId() == 0) {
                    if (!isCraftingItem && clickWindow.getSlotId() >= 1 && clickWindow.getSlotId() <= 4) {
                        isCraftingItem = true;
                    }

                    if (isCraftingItem && clickWindow.getSlotId() == 0 && clickWindow.getClickedItem() != null) {
                        isCraftingItem = false;
                    }


                    e.setCancelled(true);
                    clickWindowPackets.add(clickWindow);
                }
            }

            boolean isDraggingItem = false;

            if (Minecraft.getMinecraft().currentScreen instanceof GuiInventory) {
                if (Minecraft.getMinecraft().thePlayer.inventory.getItemStack() != null) {
                    isDraggingItem = true;
                }
            }

            if (mc.thePlayer.ticksExisted % 5 == 0 && !clickWindowPackets.isEmpty() && !isDraggingItem && !isCraftingItem) {

                PacketUtil.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                for (C0EPacketClickWindow clickWindowPacket : clickWindowPackets) {
                    PacketUtil.sendPacketNoEvent(clickWindowPacket);
                }
                PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(0));
                clickWindowPackets.clear();
                disableInventory = true;

            } else {
                disableInventory = false;
            }
        }
    }

}
