/* November.lol © 2023 */
package lol.november.management.inventory;

import lol.november.November;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S09PacketHeldItemChange;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class InventoryManager {

  /**
   * The {@link Minecraft} game instance
   */
  private final Minecraft mc = Minecraft.getMinecraft();

  private int slot = -1;

  public InventoryManager() {
    November.bus().subscribe(this);
  }

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.get() instanceof C09PacketHeldItemChange packet) {
      slot = packet.getSlotId();
    }
  };

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event -> {
    if (event.get() instanceof S09PacketHeldItemChange packet) {
      slot = packet.getHeldItemHotbarIndex();
    }
  };

  public int slot() {
    if (slot == -1) return slot = mc.thePlayer.inventory.currentItem;
    return slot;
  }

  public void sync() {
    if (slot != mc.thePlayer.inventory.currentItem) {
      mc.thePlayer.sendQueue.addToSendQueue(
        new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem)
      );
    }
  }

  public ItemStack itemStack() {
    return mc.thePlayer.inventory.getStackInSlot(slot());
  }
}
