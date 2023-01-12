package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@AllArgsConstructor
@Getter
@Setter
public class EventIgnoredPacket extends AbstractEvent {

  private Packet<?> packet;
  private final EventPacket.Direction direction;

  @SuppressWarnings("unchecked")
  public <T extends Packet<?>> T getPacket() {
    return (T) packet;
  }

}
