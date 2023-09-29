package wtf.automn.events.callables;

import wtf.automn.events.events.Event;
import wtf.automn.events.events.Typed;

public abstract class EventTyped implements Event, Typed {

  private final byte type;

  protected EventTyped(byte eventType) {
    type = eventType;
  }

  @Override
  public byte getType() {
    return type;
  }

}
