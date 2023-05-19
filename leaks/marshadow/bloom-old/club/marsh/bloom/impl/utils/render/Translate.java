package club.marsh.bloom.impl.utils.render;

public class Translate {
	public double x;
	public double y;
	public Translate(double x, double y) {
		this.x = x; this.y = y;
	}
	public void interpolate(double targetX, double targetY, double smoothing) {
		 x = AnimationUtils.animate(targetX, x, smoothing);
	     y = AnimationUtils.animate(targetY, y, smoothing);
	}
}
