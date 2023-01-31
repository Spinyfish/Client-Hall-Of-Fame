package net.java.games.input;

public class WinTabCursorComponent extends WinTabComponent {
   private int index;

   protected WinTabCursorComponent(WinTabContext context, int parentDevice, String name, Component.Identifier id, int index) {
      super(context, parentDevice, name, id);
      this.index = index;
   }

   public Event processPacket(WinTabPacket packet) {
      Event newEvent = null;
      if (packet.PK_CURSOR == this.index && this.lastKnownValue == 0.0F) {
         this.lastKnownValue = 1.0F;
         newEvent = new Event();
         newEvent.set(this, this.lastKnownValue, packet.PK_TIME * 1000L);
      } else if (packet.PK_CURSOR != this.index && this.lastKnownValue == 1.0F) {
         this.lastKnownValue = 0.0F;
         newEvent = new Event();
         newEvent.set(this, this.lastKnownValue, packet.PK_TIME * 1000L);
      }

      return newEvent;
   }
}
