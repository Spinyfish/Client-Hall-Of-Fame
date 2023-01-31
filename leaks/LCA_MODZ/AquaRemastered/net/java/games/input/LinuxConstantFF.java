package net.java.games.input;

import java.io.IOException;

final class LinuxConstantFF extends LinuxForceFeedbackEffect {
   public LinuxConstantFF(LinuxEventDevice device) throws IOException {
      super(device);
   }

   protected final int upload(int id, float intensity) throws IOException {
      int scaled_intensity = Math.round(intensity * 32767.0F);
      return this.getDevice().uploadConstantEffect(id, 0, 0, 0, 0, 0, scaled_intensity, 0, 0, 0, 0);
   }
}
