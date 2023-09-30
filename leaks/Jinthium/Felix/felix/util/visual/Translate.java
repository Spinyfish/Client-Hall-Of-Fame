package felix.util.visual;

public final class Translate {
   private double x;
   private double y;

   public Translate(float x, float y) {
      this.x = (double)x;
      this.y = (double)y;
   }

   public final void interpolate(double targetX, double targetY, double smoothing) {
      this.x = animate(targetX, this.x, smoothing);
      this.y = animate(targetY, this.y, smoothing);
   }

   public double getX() {
      return this.x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getY() {
      return this.y;
   }

   public void setY(double y) {
      this.y = y;
   }
   
   public static double animate(double target, double current, double speed) {
	   boolean larger = target > current;
	   if (speed < 0.0D) {
		   speed = 0.0D;
	      } else if (speed > 1.0D) {
	         speed = 1.0D;
	      }

	      double dif = Math.max(target, current) - Math.min(target, current);
	      double factor = dif * speed;
	      if (factor < 0.1D) {
	         factor = 0.1D;
	      }

	      if (larger) {
	         current += factor;
	      } else {
	         current -= factor;
	      }

	      return current;
   }
}
