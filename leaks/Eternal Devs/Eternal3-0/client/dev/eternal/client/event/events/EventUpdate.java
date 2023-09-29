package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import dev.eternal.client.util.movement.data.Position;
import dev.eternal.client.util.movement.data.Rotation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventUpdate extends AbstractEvent {

  private final Position position;
  private final Rotation rotation;
  private boolean groundState;
  private final boolean pre;

}
