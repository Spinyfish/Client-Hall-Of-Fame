package me.uncodable.srt.impl.events.events.render;

import me.uncodable.srt.impl.events.api.Event;

public class EventGameRender extends Event {
   private float partialTicks;
   private long nanoTime;

   public EventGameRender(float partialTicks, long nanoTime) {
      this.partialTicks = partialTicks;
      this.nanoTime = nanoTime;
   }

   public void setPartialTicks(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public void setNanoTime(long nanoTime) {
      this.nanoTime = nanoTime;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public long getNanoTime() {
      return this.nanoTime;
   }
}
