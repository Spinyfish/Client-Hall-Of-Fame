/* November.lol © 2023 */
package lol.november.scripting.wrapper.event;

import java.util.HashMap;
import java.util.Map;
import lol.november.listener.event.net.EventPacket;
import lol.november.protect.DontObfuscate;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
@DontObfuscate
public class LuaEventPacket {

  /**
   * A map of each {@link Packet} to their id
   */
  private static final Map<Class<? extends Packet<?>>, Integer> packetIds =
    new HashMap<>();

  static {
    // you may ask: why not use reflection and just.. do a funny?
    // obfuscation eventually happens so this aids has to happen

    packetIds.put(C0APacketAnimation.class, 0x0A);
    packetIds.put(C0BPacketEntityAction.class, 0x0B);
    packetIds.put(C0CPacketInput.class, 0x0C);
    packetIds.put(C0DPacketCloseWindow.class, 0x0D);
    packetIds.put(C0EPacketClickWindow.class, 0x0E);
    packetIds.put(C0FPacketConfirmTransaction.class, 0x0F);
    packetIds.put(C00PacketKeepAlive.class, 0x00);
    packetIds.put(C01PacketChatMessage.class, 0x01);
    packetIds.put(C02PacketUseEntity.class, 0x02);
    packetIds.put(C03PacketPlayer.class, 0x03);
    packetIds.put(C03PacketPlayer.C04PacketPlayerPosition.class, 0x04);
    packetIds.put(C03PacketPlayer.C05PacketPlayerLook.class, 0x05);
    packetIds.put(C03PacketPlayer.C06PacketPlayerPosLook.class, 0x06);
    packetIds.put(C07PacketPlayerDigging.class, 0x07);
    packetIds.put(C08PacketPlayerBlockPlacement.class, 0x08);
    packetIds.put(C09PacketHeldItemChange.class, 0x09);
    packetIds.put(C10PacketCreativeInventoryAction.class, 0x10);
    packetIds.put(C11PacketEnchantItem.class, 0x11);
    packetIds.put(C12PacketUpdateSign.class, 0x12);
    packetIds.put(C13PacketPlayerAbilities.class, 0x13);
    packetIds.put(C14PacketTabComplete.class, 0x14);
    packetIds.put(C15PacketClientSettings.class, 0x15);
    packetIds.put(C16PacketClientStatus.class, 0x16);
    packetIds.put(C17PacketCustomPayload.class, 0x17);
    packetIds.put(C18PacketSpectate.class, 0x18);
    packetIds.put(C19PacketResourcePackStatus.class, 0x19);
  }

  private final int packet;
  private final String direction;

  public LuaEventPacket(EventPacket event) {
    packet = packetIds.getOrDefault(event.get().getClass(), -1);
    direction = event.get() instanceof EventPacket.Out ? "out" : "in";
  }
}
