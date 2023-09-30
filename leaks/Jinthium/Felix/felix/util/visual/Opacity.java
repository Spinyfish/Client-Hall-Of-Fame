package felix.util.visual;

public class Opacity {
   private float opacity;
   private long lastMS;

   public Opacity(int opacity) {
      this.opacity = (float)opacity;
      this.lastMS = System.currentTimeMillis();
   }

   public void interpolate(float targetOpacity) {
      long currentMS = System.currentTimeMillis();
      long delta = currentMS - this.lastMS;
      this.lastMS = currentMS;
      this.opacity = calculateCompensation(targetOpacity, this.opacity, delta, 20);
   }

   public void interp(float targetOpacity, int speed) {
      long currentMS = System.currentTimeMillis();
      long delta = currentMS - this.lastMS;
      this.lastMS = currentMS;
      this.opacity = calculateCompensation(targetOpacity, this.opacity, delta, speed);
   }

   public static float calculateCompensation(float target, float current, long delta, int speed) {
	      float diff = current - target;
	      if (delta < 1L) {
	         delta = 1L;
	      }

	      double xD;
	      if (diff > (float)speed) {
	         xD = (double)((long)speed * delta / 16L) < 0.25D ? 0.5D : (double)((long)speed * delta / 16L);
	         current = (float)((double)current - xD);
	         if (current < target) {
	            current = target;
	         }
	      } else if (diff < (float)(-speed)) {
	         xD = (double)((long)speed * delta / 16L) < 0.25D ? 0.5D : (double)((long)speed * delta / 16L);
	         current = (float)((double)current + xD);
	         if (current > target) {
	            current = target;
	         }
	      } else {
	         current = target;
	      }

	      return current;
	   }

public float getOpacity() {
      return (float)((int)this.opacity);
   }

   public void setOpacity(float opacity) {
      this.opacity = opacity;
   }
}
