package events;

import net.minecraft.network.EnumPacketDirection;

public class Event<T> {
   public boolean cancelled;
   public EventType type;
   public EventDirection direction;
   public EnumPacketDirection direction2;

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public EventType getType() {
      return this.type;
   }

   public void setType(EventType type) {
      this.type = type;
   }

   public EnumPacketDirection getDirection() {
      return this.direction2;
   }

   public void setDirection(EventDirection direction) {
      this.direction = direction;
   }

   public boolean isPre() {
      if (this.type == null) {
         return false;
      } else {
         return this.type == EventType.PRE;
      }
   }

   public boolean isPost() {
      if (this.type == null) {
         return false;
      } else {
         return this.type == EventType.POST;
      }
   }

   public boolean isIncoming() {
      if (this.direction == null) {
         return false;
      } else {
         return this.direction == EventDirection.INCOMING;
      }
   }

   public boolean isOutgoing() {
      if (this.direction == null) {
         return false;
      } else {
         return this.direction == EventDirection.OUTGOING;
      }
   }
}
