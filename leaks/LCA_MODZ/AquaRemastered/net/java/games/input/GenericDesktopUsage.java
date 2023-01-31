package net.java.games.input;

final class GenericDesktopUsage implements Usage {
   private static final GenericDesktopUsage[] map = new GenericDesktopUsage[255];
   public static final GenericDesktopUsage POINTER = new GenericDesktopUsage(1);
   public static final GenericDesktopUsage MOUSE = new GenericDesktopUsage(2);
   public static final GenericDesktopUsage JOYSTICK = new GenericDesktopUsage(4);
   public static final GenericDesktopUsage GAME_PAD = new GenericDesktopUsage(5);
   public static final GenericDesktopUsage KEYBOARD = new GenericDesktopUsage(6);
   public static final GenericDesktopUsage KEYPAD = new GenericDesktopUsage(7);
   public static final GenericDesktopUsage MULTI_AXIS_CONTROLLER = new GenericDesktopUsage(8);
   public static final GenericDesktopUsage X = new GenericDesktopUsage(48);
   public static final GenericDesktopUsage Y = new GenericDesktopUsage(49);
   public static final GenericDesktopUsage Z = new GenericDesktopUsage(50);
   public static final GenericDesktopUsage RX = new GenericDesktopUsage(51);
   public static final GenericDesktopUsage RY = new GenericDesktopUsage(52);
   public static final GenericDesktopUsage RZ = new GenericDesktopUsage(53);
   public static final GenericDesktopUsage SLIDER = new GenericDesktopUsage(54);
   public static final GenericDesktopUsage DIAL = new GenericDesktopUsage(55);
   public static final GenericDesktopUsage WHEEL = new GenericDesktopUsage(56);
   public static final GenericDesktopUsage HATSWITCH = new GenericDesktopUsage(57);
   public static final GenericDesktopUsage COUNTED_BUFFER = new GenericDesktopUsage(58);
   public static final GenericDesktopUsage BYTE_COUNT = new GenericDesktopUsage(59);
   public static final GenericDesktopUsage MOTION_WAKEUP = new GenericDesktopUsage(60);
   public static final GenericDesktopUsage START = new GenericDesktopUsage(61);
   public static final GenericDesktopUsage SELECT = new GenericDesktopUsage(62);
   public static final GenericDesktopUsage VX = new GenericDesktopUsage(64);
   public static final GenericDesktopUsage VY = new GenericDesktopUsage(65);
   public static final GenericDesktopUsage VZ = new GenericDesktopUsage(66);
   public static final GenericDesktopUsage VBRX = new GenericDesktopUsage(67);
   public static final GenericDesktopUsage VBRY = new GenericDesktopUsage(68);
   public static final GenericDesktopUsage VBRZ = new GenericDesktopUsage(69);
   public static final GenericDesktopUsage VNO = new GenericDesktopUsage(70);
   public static final GenericDesktopUsage SYSTEM_CONTROL = new GenericDesktopUsage(128);
   public static final GenericDesktopUsage SYSTEM_POWER_DOWN = new GenericDesktopUsage(129);
   public static final GenericDesktopUsage SYSTEM_SLEEP = new GenericDesktopUsage(130);
   public static final GenericDesktopUsage SYSTEM_WAKE_UP = new GenericDesktopUsage(131);
   public static final GenericDesktopUsage SYSTEM_CONTEXT_MENU = new GenericDesktopUsage(132);
   public static final GenericDesktopUsage SYSTEM_MAIN_MENU = new GenericDesktopUsage(133);
   public static final GenericDesktopUsage SYSTEM_APP_MENU = new GenericDesktopUsage(134);
   public static final GenericDesktopUsage SYSTEM_MENU_HELP = new GenericDesktopUsage(135);
   public static final GenericDesktopUsage SYSTEM_MENU_EXIT = new GenericDesktopUsage(136);
   public static final GenericDesktopUsage SYSTEM_MENU = new GenericDesktopUsage(137);
   public static final GenericDesktopUsage SYSTEM_MENU_RIGHT = new GenericDesktopUsage(138);
   public static final GenericDesktopUsage SYSTEM_MENU_LEFT = new GenericDesktopUsage(139);
   public static final GenericDesktopUsage SYSTEM_MENU_UP = new GenericDesktopUsage(140);
   public static final GenericDesktopUsage SYSTEM_MENU_DOWN = new GenericDesktopUsage(141);
   public static final GenericDesktopUsage DPAD_UP = new GenericDesktopUsage(144);
   public static final GenericDesktopUsage DPAD_DOWN = new GenericDesktopUsage(145);
   public static final GenericDesktopUsage DPAD_RIGHT = new GenericDesktopUsage(146);
   public static final GenericDesktopUsage DPAD_LEFT = new GenericDesktopUsage(147);
   private final int usage_id;

   public static final GenericDesktopUsage map(int usage_id) {
      return usage_id >= 0 && usage_id < map.length ? map[usage_id] : null;
   }

   private GenericDesktopUsage(int usage_id) {
      map[usage_id] = this;
      this.usage_id = usage_id;
   }

   public final String toString() {
      return "GenericDesktopUsage (0x" + Integer.toHexString(this.usage_id) + ")";
   }

   public final Component.Identifier getIdentifier() {
      if (this == X) {
         return Component.Identifier.Axis.X;
      } else if (this == Y) {
         return Component.Identifier.Axis.Y;
      } else if (this == Z || this == WHEEL) {
         return Component.Identifier.Axis.Z;
      } else if (this == RX) {
         return Component.Identifier.Axis.RX;
      } else if (this == RY) {
         return Component.Identifier.Axis.RY;
      } else if (this == RZ) {
         return Component.Identifier.Axis.RZ;
      } else if (this == SLIDER) {
         return Component.Identifier.Axis.SLIDER;
      } else if (this == HATSWITCH) {
         return Component.Identifier.Axis.POV;
      } else {
         return this == SELECT ? Component.Identifier.Button.SELECT : null;
      }
   }
}
