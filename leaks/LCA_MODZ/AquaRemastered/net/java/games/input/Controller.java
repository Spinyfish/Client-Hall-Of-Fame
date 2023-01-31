package net.java.games.input;

public interface Controller {
   Controller[] getControllers();

   Controller.Type getType();

   Component[] getComponents();

   Component getComponent(Component.Identifier var1);

   Rumbler[] getRumblers();

   boolean poll();

   void setEventQueueSize(int var1);

   EventQueue getEventQueue();

   Controller.PortType getPortType();

   int getPortNumber();

   String getName();

   public static final class PortType {
      private final String name;
      public static final Controller.PortType UNKNOWN = new Controller.PortType("Unknown");
      public static final Controller.PortType USB = new Controller.PortType("USB port");
      public static final Controller.PortType GAME = new Controller.PortType("Game port");
      public static final Controller.PortType NETWORK = new Controller.PortType("Network port");
      public static final Controller.PortType SERIAL = new Controller.PortType("Serial port");
      public static final Controller.PortType I8042 = new Controller.PortType("i8042 (PS/2)");
      public static final Controller.PortType PARALLEL = new Controller.PortType("Parallel port");

      protected PortType(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }
   }

   public static class Type {
      private final String name;
      public static final Controller.Type UNKNOWN = new Controller.Type("Unknown");
      public static final Controller.Type MOUSE = new Controller.Type("Mouse");
      public static final Controller.Type KEYBOARD = new Controller.Type("Keyboard");
      public static final Controller.Type FINGERSTICK = new Controller.Type("Fingerstick");
      public static final Controller.Type GAMEPAD = new Controller.Type("Gamepad");
      public static final Controller.Type HEADTRACKER = new Controller.Type("Headtracker");
      public static final Controller.Type RUDDER = new Controller.Type("Rudder");
      public static final Controller.Type STICK = new Controller.Type("Stick");
      public static final Controller.Type TRACKBALL = new Controller.Type("Trackball");
      public static final Controller.Type TRACKPAD = new Controller.Type("Trackpad");
      public static final Controller.Type WHEEL = new Controller.Type("Wheel");

      protected Type(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }
   }
}
