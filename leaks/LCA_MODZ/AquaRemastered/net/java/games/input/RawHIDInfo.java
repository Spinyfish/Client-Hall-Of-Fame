package net.java.games.input;

import java.io.IOException;

class RawHIDInfo extends RawDeviceInfo {
   private final RawDevice device;
   private final int vendor_id;
   private final int product_id;
   private final int version;
   private final int page;
   private final int usage;

   public RawHIDInfo(RawDevice device, int vendor_id, int product_id, int version, int page, int usage) {
      this.device = device;
      this.vendor_id = vendor_id;
      this.product_id = product_id;
      this.version = version;
      this.page = page;
      this.usage = usage;
   }

   public final int getUsage() {
      return this.usage;
   }

   public final int getUsagePage() {
      return this.page;
   }

   public final long getHandle() {
      return this.device.getHandle();
   }

   public final Controller createControllerFromDevice(RawDevice device, SetupAPIDevice setupapi_device) throws IOException {
      return null;
   }
}
