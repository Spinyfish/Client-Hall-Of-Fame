/* November.lol © 2023 */
package lol.november.listener.event.net;

import lol.november.listener.bus.Cancelable;
import net.minecraft.network.Packet;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class EventPacket extends Cancelable {

  private final Packet<?> packet;

  public EventPacket(Packet<?> packet) {
    this.packet = packet;
  }

  public <T extends Packet<?>> T get() {
    return (T) packet;
  }

  public static class In extends EventPacket {

    public In(Packet<?> packet) {
      super(packet);
    }
  }

  public static class Out extends EventPacket {

    public Out(Packet<?> packet) {
      super(packet);
    }
  }
}
