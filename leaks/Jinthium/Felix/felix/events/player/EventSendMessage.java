package felix.events.player;

import felix.events.Cancellable;

public class EventSendMessage extends Cancellable {
	
   private final String message;

   public EventSendMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }
}
