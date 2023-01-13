package me.uncodable.srt.impl.events.events.packet;

import me.uncodable.srt.impl.events.api.Event;

public class EventSendChatMessage extends Event {
   private String message;
   private boolean cancelled;

   public EventSendChatMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
