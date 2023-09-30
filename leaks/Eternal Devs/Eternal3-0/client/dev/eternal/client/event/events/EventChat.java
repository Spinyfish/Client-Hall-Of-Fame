package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.IChatComponent;

@Getter
@Setter
@AllArgsConstructor
public class EventChat extends AbstractEvent {
  private IChatComponent chatComponent;
  private final boolean sending;
}
