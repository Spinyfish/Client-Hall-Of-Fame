package wtf.automn.events.impl.visual;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import wtf.automn.events.events.Event;

@Data
@RequiredArgsConstructor
public class EventRender2D implements Event {

  private final Phase phase;

  public enum Phase {
    PRE, POST
  }

}
