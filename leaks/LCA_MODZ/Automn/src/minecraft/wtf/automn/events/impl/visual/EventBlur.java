package wtf.automn.events.impl.visual;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import wtf.automn.events.events.Event;

@Data
@RequiredArgsConstructor
public class EventBlur implements Event {

  private final Runnable runnable;
  private boolean cancelled;

}
