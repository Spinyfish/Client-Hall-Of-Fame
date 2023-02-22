package net.optifine.shaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SMCLog {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PREFIX = "[Shaders] ";

    public static void severe(final String message) {
        LOGGER.error("[Shaders] " + message);
    }

    public static void warning(final String message) {
        LOGGER.warn("[Shaders] " + message);
    }

    public static void info(final String message) {
        LOGGER.info("[Shaders] " + message);
    }

    public static void fine(final String message) {
        LOGGER.debug("[Shaders] " + message);
    }

    public static void severe(final String format, final Object... args) {
        final String s = String.format(format, args);
        LOGGER.error("[Shaders] " + s);
    }

    public static void warning(final String format, final Object... args) {
        final String s = String.format(format, args);
        LOGGER.warn("[Shaders] " + s);
    }

    public static void info(final String format, final Object... args) {
        final String s = String.format(format, args);
        LOGGER.info("[Shaders] " + s);
    }

    public static void fine(final String format, final Object... args) {
        final String s = String.format(format, args);
        LOGGER.debug("[Shaders] " + s);
    }
}
