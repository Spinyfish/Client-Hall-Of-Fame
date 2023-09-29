package com.alan.clients.util.vantage;

import com.profesorfalken.wmi4java.WMI4Java;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class HWIDUtil {
    static String hwid;
    static {
        hwid = bytesToHex(generateHWID());
    }
    public static String getHWID() {
        return hwid;
    }

    public static byte[] generateHWID() {
        try {
            OperatingSystem os = OSUtil.getOS();
            MessageDigest hash = MessageDigest.getInstance("SHA-256");
            String o = "VANTAGE_" + os.getNiceName() + System.getProperty("os.arch") + System.getProperty("os.version")
                    + Runtime.getRuntime().availableProcessors() + System.getenv("PROCESSOR_IDENTIFIER")
                    + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432")
                    + System.getenv("NUMBER_OF_PROCESSORS") + System.getenv("COMPUTERNAME") + System.getenv("os")
                    + System.getProperty("user.language") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE")
                    + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("HOME")
                    + System.getenv("HOSTNAME") + System.getenv("SHELL") + System.getenv("LOGNAME") + System.getenv("USERNAME");
            if(os.equals(OperatingSystem.WINDOWS)) {
                Map<String, String> videoData = WMI4Java.get().getWMIObject("Win32_VideoController");
                o += videoData.get("Caption") + videoData.get("DeviceID") + videoData.get("AdapterDACType") + videoData.get("MinRefreshRate")
                        + videoData.get("Description") + videoData.get("CreationClassName") + videoData.get("MaxRefreshRate")
                        + videoData.get("AdapterCompatability") + videoData.get("VideoProcessor") + videoData.get("PNPDeviceID")
                        + WMI4Java.get().getWMIObject("Win32_BIOS").get("SerialNumber");
            }
            return hash.digest(o.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new Error("HWIDException: ", e);
        }

    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}