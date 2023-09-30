package net.optifine.shaders;

import org.tinylog.Logger;

public abstract class SMCLog {
    private static final String PREFIX = "[Shaders] ";

    public static void severe(String message) {
        Logger.error("[Shaders] " + message);
    }

    public static void warning(String message) {
        Logger.warn("[Shaders] " + message);
    }

    public static void info(String message) {
        Logger.info("[Shaders] " + message);
    }

    public static void fine(String message) {
        Logger.debug("[Shaders] " + message);
    }

    public static void severe(String format, Object... args) {
        String s = String.format(format, args);
        Logger.error("[Shaders] " + s);
    }

    public static void warning(String format, Object... args) {
        String s = String.format(format, args);
        Logger.warn("[Shaders] " + s);
    }

    public static void info(String format, Object... args) {
        String s = String.format(format, args);
        Logger.info("[Shaders] " + s);
    }

    public static void fine(String format, Object... args) {
        String s = String.format(format, args);
        Logger.debug("[Shaders] " + s);
    }
}
