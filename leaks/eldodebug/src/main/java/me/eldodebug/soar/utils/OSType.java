package me.eldodebug.soar.utils;

public enum OSType {
	
    WINDOWS, 
    LINUX, 
    MAC, 
    UNKNOWN;
    
    public static String getArch() {
    	
        String osArch = System.getProperty("os.arch");
        
        if (osArch.contains("x64")) {
            return "x64";
        }
        if (osArch.contains("x86")) {
            return "x86";
        }
        if (osArch.contains("arm")) {
            return "arm";
        }
        return "unknown";
    }
    
    public static OSType getType() {
    	
        String os = System.getProperty("os.name");
        
        if (os.toLowerCase().contains("win")) {
            return OSType.WINDOWS;
        }
        if (os.toLowerCase().contains("ux")) {
            return OSType.LINUX;
        }
        if (os.toLowerCase().contains("mac")) {
            return OSType.MAC;
        }
        return OSType.UNKNOWN;
    }
}