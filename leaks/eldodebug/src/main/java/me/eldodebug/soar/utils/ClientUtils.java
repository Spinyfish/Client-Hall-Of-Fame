package me.eldodebug.soar.utils;

import java.io.File;
import java.lang.reflect.Field;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.FullbrightMod;
import me.eldodebug.soar.ui.notification.Notification;
import me.eldodebug.soar.ui.notification.NotificationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.server.MinecraftServer;

public class ClientUtils {
	
    public static Field gameSettings_ofFastRender;
	
	private static Notification notification = new Notification();
	
	public static boolean loadedMinimap = false;
	
	public static void showNotification(String title, String message) {
		notification.setNotification(title, message);
		NotificationManager.show(notification);
	}
	
    public static File getModsDir() {
    	return new File(Minecraft.getMinecraft().mcDataDir, "mods");
    }
    
    public static boolean isFullbright() {
        MinecraftServer server = MinecraftServer.getServer();

        if (server != null && server.isCallingFromMinecraftThread()) {
            return false;
        }

        return Soar.instance.modManager.getModByClass(FullbrightMod.class).isToggled();
    }

    static {
        try {
            Class.forName("Config");

            gameSettings_ofFastRender = GameSettings.class.getDeclaredField("ofFastRender");
            gameSettings_ofFastRender.setAccessible(true);
        } catch (ClassNotFoundException ignore) {
        } catch (NoSuchFieldException e) {}
    }
    
	public static boolean isDevelopment() {
		for(StackTraceElement element : Thread.currentThread().getStackTrace()) {
			if(element.getClassName().equals("GradleStart")) {
				return true;
			}
		}

		return false;
	}
	
	public static void shutdown() {
		Minecraft.getMinecraft().shutdown();
	}
}