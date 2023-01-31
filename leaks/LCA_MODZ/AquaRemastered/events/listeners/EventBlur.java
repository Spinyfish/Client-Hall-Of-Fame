package events.listeners;

import events.Event;

public class EventBlur extends Event<EventBlur> {
   private final Runnable blurredFunction;

   public EventBlur(Runnable blurredFunction) {
      this.blurredFunction = blurredFunction;
   }

   public Runnable getBlurredFunction() {
      return this.blurredFunction;
   }
}
