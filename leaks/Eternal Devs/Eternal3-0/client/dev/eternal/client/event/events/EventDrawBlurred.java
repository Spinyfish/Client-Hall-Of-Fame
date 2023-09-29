package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class EventDrawBlurred extends AbstractEvent {

  private int x, y, x1, y1;

  private Runnable r;

  public EventDrawBlurred(Runnable r) {
    this.r = r;
  }

}
