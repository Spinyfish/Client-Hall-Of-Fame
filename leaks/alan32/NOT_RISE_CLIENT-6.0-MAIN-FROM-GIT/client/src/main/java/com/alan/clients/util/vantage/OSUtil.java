package com.alan.clients.util.vantage;

public class OSUtil {
    private static final String osName = System.getProperty("os.name").toLowerCase();

    public static OperatingSystem getOS() {
        if (osName.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (osName.contains("mac")) {
            return OperatingSystem.MACOSX;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OperatingSystem.LINUX;
        } else {
            System.exit(0);
            return null;
        }
    }
}