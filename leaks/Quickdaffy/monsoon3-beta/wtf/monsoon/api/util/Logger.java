/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util;

import wtf.monsoon.Wrapper;

public class Logger {
    public void info(String message) {
        System.out.println(Color.WHITE.get() + "<" + Color.BRIGHT_BLUE.get() + "monsoon" + Color.WHITE.get() + "> " + Color.RESET.get() + message);
    }

    public void error(String message) {
        System.out.println(Color.WHITE.get() + "<" + Color.BRIGHT_RED.get() + "error" + Color.WHITE.get() + "> " + Color.RESET.get() + message);
    }

    public void warn(String message) {
        System.out.println(Color.WHITE.get() + "<" + Color.BRIGHT_YELLOW.get() + "warning" + Color.WHITE.get() + "> " + Color.RESET.get() + message);
    }

    public void debug(String message) {
        if (Wrapper.isDebugModeEnabled()) {
            System.out.println(Color.WHITE.get() + "<" + Color.BRIGHT_YELLOW.get() + "debug" + Color.WHITE.get() + "> " + Color.RESET.get() + message);
        }
    }

    public static enum Color {
        RESET("\u001b[0m"),
        BRIGHT_BLACK("\u001b[30;1m"),
        BRIGHT_RED("\u001b[31;1m"),
        BRIGHT_YELLOW("\u001b[33;1m"),
        BRIGHT_BLUE("\u001b[34;1m"),
        BRIGHT_MAGENTA("\u001b[35;1m"),
        BRIGHT_CYAN("\u001b[36;1m"),
        BRIGHT_WHITE("\u001b[37;1m"),
        BLACK("\u001b[30m"),
        RED("\u001b[31m"),
        GREEN("\u001b[32m"),
        YELLOW("\u001b[33m"),
        BLUE("\u001b[34m"),
        MAGENTA("\u001b[35m"),
        GREEN_BG("\u001b[42m"),
        RED_BG("\u001b[41;1m"),
        WHITE_BG("\u001b[47;1m"),
        MAGENTA_BG("\u001b[45;1m"),
        CYAN("\u001b[36m"),
        WHITE("\u001b[37m"),
        CLEAR("\u001b[H\u001b[2J"),
        NEW_LINE("\r\n");

        private final String code;

        private Color(String code) {
            this.code = code;
        }

        public String get() {
            return this.code;
        }
    }
}

