package haptic.util.misc;

public class TimerUtil {
	
	private long lastTime = System.currentTimeMillis();
	
	public boolean hasReached(long time) {
		if(System.currentTimeMillis() - lastTime >= time) {
			return true;
		}
		return false;
	}
	
	public long getTimeElapsed() {
		return System.currentTimeMillis() - lastTime;
	}
	
	public void reset() {
		lastTime = System.currentTimeMillis();
	}
	
}
