package intent.AquaDev.aqua.utils;

public final class AnimationUtil {
   public static float calculateCompensation(float target, float current, long delta, double speed) {
      float diff = current - target;
      if (delta < 1L) {
         delta = 1L;
      }

      if (delta > 1000L) {
         delta = 16L;
      }

      double d0 = speed * (double)delta / 16.0 < 0.5 ? 0.5 : speed * (double)delta / 16.0;
      if ((double)diff > speed) {
         current -= (float)d0;
         if (current < target) {
            current = target;
         }
      } else if ((double)diff < -speed) {
         current += (float)d0;
         if (current > target) {
            current = target;
         }
      } else {
         current = target;
      }

      return current;
   }

   public static double animate(double current, double target, double speed) {
      double animated = current;
      if (current < target) {
         if (current + speed > target) {
            animated = target;
         } else {
            animated = current + speed;
         }
      }

      if (current > target) {
         if (current - speed < target) {
            animated = target;
         } else {
            animated -= speed;
         }
      }

      return animated;
   }
}
