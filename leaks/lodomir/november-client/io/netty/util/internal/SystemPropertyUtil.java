/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class SystemPropertyUtil {
    private static boolean initializedLogger = false;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SystemPropertyUtil.class);
    private static boolean loggedException;
    private static final Pattern INTEGER_PATTERN;

    public static boolean contains(String key) {
        return SystemPropertyUtil.get(key) != null;
    }

    public static String get(String key) {
        return SystemPropertyUtil.get(key, null);
    }

    public static String get(final String key, String def) {
        String value;
        block5: {
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (key.isEmpty()) {
                throw new IllegalArgumentException("key must not be empty.");
            }
            value = null;
            try {
                value = System.getSecurityManager() == null ? System.getProperty(key) : AccessController.doPrivileged(new PrivilegedAction<String>(){

                    @Override
                    public String run() {
                        return System.getProperty(key);
                    }
                });
            }
            catch (Exception e) {
                if (loggedException) break block5;
                SystemPropertyUtil.log("Unable to retrieve a system property '" + key + "'; default values will be used.", e);
                loggedException = true;
            }
        }
        if (value == null) {
            return def;
        }
        return value;
    }

    public static boolean getBoolean(String key, boolean def) {
        String value = SystemPropertyUtil.get(key);
        if (value == null) {
            return def;
        }
        if ((value = value.trim().toLowerCase()).isEmpty()) {
            return true;
        }
        if ("true".equals(value) || "yes".equals(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equals(value) || "no".equals(value) || "0".equals(value)) {
            return false;
        }
        SystemPropertyUtil.log("Unable to parse the boolean system property '" + key + "':" + value + " - " + "using the default value: " + def);
        return def;
    }

    public static int getInt(String key, int def) {
        String value = SystemPropertyUtil.get(key);
        if (value == null) {
            return def;
        }
        if (INTEGER_PATTERN.matcher(value = value.trim().toLowerCase()).matches()) {
            try {
                return Integer.parseInt(value);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        SystemPropertyUtil.log("Unable to parse the integer system property '" + key + "':" + value + " - " + "using the default value: " + def);
        return def;
    }

    public static long getLong(String key, long def) {
        String value = SystemPropertyUtil.get(key);
        if (value == null) {
            return def;
        }
        if (INTEGER_PATTERN.matcher(value = value.trim().toLowerCase()).matches()) {
            try {
                return Long.parseLong(value);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        SystemPropertyUtil.log("Unable to parse the long integer system property '" + key + "':" + value + " - " + "using the default value: " + def);
        return def;
    }

    private static void log(String msg) {
        if (initializedLogger) {
            logger.warn(msg);
        } else {
            Logger.getLogger(SystemPropertyUtil.class.getName()).log(Level.WARNING, msg);
        }
    }

    private static void log(String msg, Exception e) {
        if (initializedLogger) {
            logger.warn(msg, e);
        } else {
            Logger.getLogger(SystemPropertyUtil.class.getName()).log(Level.WARNING, msg, e);
        }
    }

    private SystemPropertyUtil() {
    }

    static {
        initializedLogger = true;
        INTEGER_PATTERN = Pattern.compile("-?[0-9]+");
    }
}

