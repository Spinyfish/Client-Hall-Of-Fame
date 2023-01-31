package events.listeners;

import events.Event;

public class EventGlowESP extends Event {
   private final Runnable runnable;
   public boolean cancelled;

   public Runnable getRunnable() {
      return this.runnable;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof EventGlowESP)) {
         return false;
      } else {
         EventGlowESP other = (EventGlowESP)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.isCancelled() != other.isCancelled()) {
            return false;
         } else {
            Object this$runnable = this.getRunnable();
            Object other$runnable = other.getRunnable();
            return this$runnable == null ? other$runnable == null : this$runnable.equals(other$runnable);
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof EventGlowESP;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isCancelled() ? 79 : 97);
      Object $runnable = this.getRunnable();
      return result * 59 + ($runnable == null ? 43 : $runnable.hashCode());
   }

   @Override
   public String toString() {
      return "EventGlowESP(runnable=" + this.getRunnable() + ", cancelled=" + this.isCancelled() + ")";
   }

   public EventGlowESP(Runnable runnable) {
      this.runnable = runnable;
   }
}
