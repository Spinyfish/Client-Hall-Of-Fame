package net.java.games.input;

import java.io.IOException;

class RawKeyboardInfo extends RawDeviceInfo {
   private final RawDevice device;
   private final int type;
   private final int sub_type;
   private final int keyboard_mode;
   private final int num_function_keys;
   private final int num_indicators;
   private final int num_keys_total;

   public RawKeyboardInfo(RawDevice device, int type, int sub_type, int keyboard_mode, int num_function_keys, int num_indicators, int num_keys_total) {
      this.device = device;
      this.type = type;
      this.sub_type = sub_type;
      this.keyboard_mode = keyboard_mode;
      this.num_function_keys = num_function_keys;
      this.num_indicators = num_indicators;
      this.num_keys_total = num_keys_total;
   }

   public final int getUsage() {
      return 6;
   }

   public final int getUsagePage() {
      return 1;
   }

   public final long getHandle() {
      return this.device.getHandle();
   }

   public final Controller createControllerFromDevice(RawDevice device, SetupAPIDevice setupapi_device) throws IOException {
      return new RawKeyboard(setupapi_device.getName(), device, new Controller[0], new Rumbler[0]);
   }
}
