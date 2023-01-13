package me.uncodable.srt.impl.events.events.render;

import me.uncodable.srt.impl.events.api.Event;

public class Event3DRender extends Event {
   private final float partialTicks;

   public Event3DRender(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }
}
