package me.uncodable.srt.impl.events.events.peripheral;

import me.uncodable.srt.impl.events.api.Event;

public class EventKeyPress extends Event {
   private final int key;

   public EventKeyPress(int key) {
      this.key = key;
   }

   public int getKey() {
      return this.key;
   }
}
