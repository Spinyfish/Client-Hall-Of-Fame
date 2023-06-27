package shadersmod.common;

import org.apache.logging.log4j.*;

public abstract class SMCLog
{
    private static final Logger LOGGER;
    private static final String PREFIX = "[Shaders] ";
    
    public static void severe(final String message) {
        SMCLog.LOGGER.error("[Shaders] " + message);
    }
    
    public static void warning(final String message) {
        SMCLog.LOGGER.warn("[Shaders] " + message);
    }
    
    public static void info(final String message) {
        SMCLog.LOGGER.info("[Shaders] " + message);
    }
    
    public static void fine(final String message) {
        SMCLog.LOGGER.debug("[Shaders] " + message);
    }
    
    public static void severe(final String format, final Object... args) {
        final String s = String.format(format, args);
        SMCLog.LOGGER.error("[Shaders] " + s);
    }
    
    public static void warning(final String format, final Object... args) {
        final String s = String.format(format, args);
        SMCLog.LOGGER.warn("[Shaders] " + s);
    }
    
    public static void info(final String format, final Object... args) {
        final String s = String.format(format, args);
        SMCLog.LOGGER.info("[Shaders] " + s);
    }
    
    public static void fine(final String format, final Object... args) {
        final String s = String.format(format, args);
        SMCLog.LOGGER.debug("[Shaders] " + s);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
