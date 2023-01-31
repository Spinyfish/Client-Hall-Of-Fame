package net.java.games.input;

import java.lang.reflect.Method;

final class UsagePage {
   private static final UsagePage[] map = new UsagePage[255];
   public static final UsagePage UNDEFINED = new UsagePage(0);
   public static final UsagePage GENERIC_DESKTOP = new UsagePage(1, GenericDesktopUsage.class);
   public static final UsagePage SIMULATION = new UsagePage(2);
   public static final UsagePage VR = new UsagePage(3);
   public static final UsagePage SPORT = new UsagePage(4);
   public static final UsagePage GAME = new UsagePage(5);
   public static final UsagePage KEYBOARD_OR_KEYPAD = new UsagePage(7, KeyboardUsage.class);
   public static final UsagePage LEDS = new UsagePage(8);
   public static final UsagePage BUTTON = new UsagePage(9, ButtonUsage.class);
   public static final UsagePage ORDINAL = new UsagePage(10);
   public static final UsagePage TELEPHONY = new UsagePage(11);
   public static final UsagePage CONSUMER = new UsagePage(12);
   public static final UsagePage DIGITIZER = new UsagePage(13);
   public static final UsagePage PID = new UsagePage(15);
   public static final UsagePage UNICODE = new UsagePage(16);
   public static final UsagePage ALPHANUMERIC_DISPLAY = new UsagePage(20);
   public static final UsagePage POWER_DEVICE = new UsagePage(132);
   public static final UsagePage BATTERY_SYSTEM = new UsagePage(133);
   public static final UsagePage BAR_CODE_SCANNER = new UsagePage(140);
   public static final UsagePage SCALE = new UsagePage(141);
   public static final UsagePage CAMERACONTROL = new UsagePage(144);
   public static final UsagePage ARCADE = new UsagePage(145);
   private final Class usage_class;
   private final int usage_page_id;

   public static final UsagePage map(int page_id) {
      return page_id >= 0 && page_id < map.length ? map[page_id] : null;
   }

   private UsagePage(int page_id, Class usage_class) {
      map[page_id] = this;
      this.usage_class = usage_class;
      this.usage_page_id = page_id;
   }

   private UsagePage(int page_id) {
      this(page_id, null);
   }

   public final String toString() {
      return "UsagePage (0x" + Integer.toHexString(this.usage_page_id) + ")";
   }

   public final Usage mapUsage(int usage_id) {
      if (this.usage_class == null) {
         return null;
      } else {
         try {
            Method map_method = this.usage_class.getMethod("map", Integer.TYPE);
            Object result = map_method.invoke(null, new Integer(usage_id));
            return (Usage)result;
         } catch (Exception var4) {
            throw new Error(var4);
         }
      }
   }
}
