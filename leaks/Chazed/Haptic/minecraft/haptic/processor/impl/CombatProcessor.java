package haptic.processor.impl;

public class CombatProcessor {
	
	private static boolean isBlocking;

	public static boolean isBlocking() {
		return isBlocking;
	}

	public static void setBlocking(boolean isBlocking) {
		CombatProcessor.isBlocking = isBlocking;
	}
	
}