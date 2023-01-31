package net.java.games.input;

import java.io.IOException;

abstract class RawDeviceInfo {
   public abstract Controller createControllerFromDevice(RawDevice var1, SetupAPIDevice var2) throws IOException;

   public abstract int getUsage();

   public abstract int getUsagePage();

   public abstract long getHandle();

   public final boolean equals(Object other) {
      if (!(other instanceof RawDeviceInfo)) {
         return false;
      } else {
         RawDeviceInfo other_info = (RawDeviceInfo)other;
         return other_info.getUsage() == this.getUsage() && other_info.getUsagePage() == this.getUsagePage();
      }
   }

   public final int hashCode() {
      return this.getUsage() ^ this.getUsagePage();
   }
}
