package org.crystalpvp.anticheat.api.util;


import org.crystalpvp.anticheat.CrystalAC;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {

	public static void run(Runnable runnable) {
		CrystalAC.getInstance().getServer().getScheduler().runTask(CrystalAC.getInstance(), runnable);
	}

	public static void runTimer(Runnable runnable, long delay, long timer) {
		CrystalAC.getInstance().getServer().getScheduler().runTaskTimer(CrystalAC.getInstance(), runnable, delay, timer);
	}

	public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
		runnable.runTaskTimer(CrystalAC.getInstance(), delay, timer);
	}

	public static void runLater(Runnable runnable, long delay) {
		CrystalAC.getInstance().getServer().getScheduler().runTaskLater(CrystalAC.getInstance(), runnable, delay);
	}

	public static void runAsync(Runnable runnable) {
		CrystalAC.getInstance().getServer().getScheduler().runTaskAsynchronously(CrystalAC.getInstance(), runnable);
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Throwable throwable) {

		}
	}
}
