package wtf.automn.events.callables;


import wtf.automn.events.events.Cancellable;
import wtf.automn.events.events.Event;

public abstract class EventCancellable implements Event, Cancellable {

  private boolean cancelled;

  protected EventCancellable() {
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean state) {
    cancelled = state;
  }

}
