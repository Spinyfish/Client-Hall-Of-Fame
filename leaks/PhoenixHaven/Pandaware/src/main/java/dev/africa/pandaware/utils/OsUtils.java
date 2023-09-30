package dev.africa.pandaware.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OsUtils {
    public OS getOsType() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return OS.WINDOWS;
        } else if (os.contains("mac")) {
            return OS.MAC;
        } else if (os.contains("solaris") || os.contains("sunos")) {
            return OS.SOLARIS;
        } else if (os.contains("linux") || os.contains("unix")) {
            return OS.LINUX;
        } else {
            return OS.OTHER;
        }
    }

    public enum OS {
        LINUX, MAC, SOLARIS, OTHER, WINDOWS
    }
}