package net.java.games.input;

import java.io.IOException;

final class IDirectInputEffect implements Rumbler {
   private final long address;
   private final DIEffectInfo info;
   private boolean released;

   public IDirectInputEffect(long address, DIEffectInfo info) {
      this.address = address;
      this.info = info;
   }

   public final synchronized void rumble(float intensity) {
      try {
         this.checkReleased();
         if (intensity > 0.0F) {
            int int_gain = Math.round(intensity * 10000.0F);
            this.setGain(int_gain);
            this.start(1, 0);
         } else {
            this.stop();
         }
      } catch (IOException var3) {
         DirectInputEnvironmentPlugin.logln("Failed to set rumbler gain: " + var3.getMessage());
      }
   }

   public final Component.Identifier getAxisIdentifier() {
      return null;
   }

   public final String getAxisName() {
      return null;
   }

   public final synchronized void release() {
      if (!this.released) {
         this.released = true;
         nRelease(this.address);
      }
   }

   private static final native void nRelease(long var0);

   private final void checkReleased() throws IOException {
      if (this.released) {
         throw new IOException();
      }
   }

   private final void setGain(int gain) throws IOException {
      int res = nSetGain(this.address, gain);
      if (res != 3 && res != 4 && res != 0 && res != 8 && res != 12) {
         throw new IOException("Failed to set effect gain (0x" + Integer.toHexString(res) + ")");
      }
   }

   private static final native int nSetGain(long var0, int var2);

   private final void start(int iterations, int flags) throws IOException {
      int res = nStart(this.address, iterations, flags);
      if (res != 0) {
         throw new IOException("Failed to start effect (0x" + Integer.toHexString(res) + ")");
      }
   }

   private static final native int nStart(long var0, int var2, int var3);

   private final void stop() throws IOException {
      int res = nStop(this.address);
      if (res != 0) {
         throw new IOException("Failed to stop effect (0x" + Integer.toHexString(res) + ")");
      }
   }

   private static final native int nStop(long var0);

   protected void finalize() {
      this.release();
   }
}
