package events.listeners;

import events.Event;

public class EventRenderNameTags extends Event<EventRenderNameTags> {
   private final float partialTicks;
   public static EventRenderNameTags INSTANCE;

   public EventRenderNameTags(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }
}
