package wtf.automn.events.events;

public interface Cancellable {

  boolean isCancelled();

  void setCancelled(boolean state);

}
