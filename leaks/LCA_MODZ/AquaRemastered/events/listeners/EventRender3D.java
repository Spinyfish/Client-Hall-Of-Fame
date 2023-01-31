package events.listeners;

import events.Event;

public class EventRender3D extends Event<EventRender3D> {
   private final float partialTicks;
   public static EventRender3D INSTANCE;

   public EventRender3D(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }
}
