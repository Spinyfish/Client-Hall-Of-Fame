/**
 * @project Myth
 * @author CodeMan
 * @at 21.01.23, 14:34
 */
package dev.myth.api.utils;

import dev.myth.api.interfaces.IMethods;
import dev.myth.events.PacketEvent;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;

@UtilityClass
public class PacketUtil implements IMethods {

    public void sillyS30(PacketEvent event) {
        if(event.getPacket() instanceof S30PacketWindowItems) {
            S30PacketWindowItems packet = event.getPacket();
            boolean same = true;
            if(packet.getItemStacks() != null && packet.func_148911_c() == 0) {
                for (int i = 0; i < packet.getItemStacks().length; i++) {
                    ItemStack itemStack = packet.getItemStacks()[i];
                    ItemStack itemStack1 = MC.thePlayer.inventoryContainer.getSlot(i).getStack();

                    if(!ItemStack.areItemStacksEqual(itemStack, itemStack1)) {
                        same = false;
                        break;
                    }
                }
            }
            if(same) {
                event.setCancelled(true);
            }
        }
    }

    public void sillyS2F(PacketEvent event) {
        if (event.getPacket() instanceof S2FPacketSetSlot) {
            S2FPacketSetSlot packet = event.getPacket();
            if(((packet.getSlot() == 0 && packet.getWindowId() == 0) || (packet.getSlot() == -1 && packet.getWindowId() == -1)) && packet.getStack() == null) {
                event.setCancelled(true);
            }
        }
    }

}
