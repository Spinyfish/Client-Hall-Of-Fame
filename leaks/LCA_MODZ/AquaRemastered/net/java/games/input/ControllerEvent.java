package net.java.games.input;

public class ControllerEvent {
   private Controller controller;

   public ControllerEvent(Controller c) {
      this.controller = c;
   }

   public Controller getController() {
      return this.controller;
   }
}
