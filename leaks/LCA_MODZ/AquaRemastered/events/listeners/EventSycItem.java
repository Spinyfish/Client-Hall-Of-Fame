package events.listeners;

import events.Event;

public class EventSycItem extends Event {
   public int slot;

   public EventSycItem(int slot) {
      this.slot = slot;
   }
}
