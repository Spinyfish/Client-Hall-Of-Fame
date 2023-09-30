package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventSlowdown extends AbstractEvent {
  private float multiplier;
}
