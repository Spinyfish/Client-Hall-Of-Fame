package club.pulsive.impl.util.client;

import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class LicenceUtil {
    public static String getLicence() {
        final SystemInfo systemInfo = new SystemInfo();
        final OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        final HardwareAbstractionLayer hardware = systemInfo.getHardware();
        final ComputerSystem motherboard = hardware.getComputerSystem();
        final Firmware firmware = motherboard.getFirmware();
        final Baseboard baseboard = motherboard.getBaseboard();
        final String string = operatingSystem.getFamily() + operatingSystem.getManufacturer()
                + hardware.getProcessor().getProcessorIdentifier().getProcessorID()
                + hardware.getProcessor().getProcessorIdentifier().getIdentifier() + hardware.getProcessor().getProcessorIdentifier().getModel()
                + hardware.getProcessor().getProcessorIdentifier().getMicroarchitecture()
                + hardware.getProcessor().getProcessorIdentifier().getVendor()
                + hardware.getProcessor().getProcessorIdentifier().getName()
                + hardware.getProcessor().getProcessorIdentifier().getFamily()
                + SystemInfo.getCurrentPlatform().getName()
                + operatingSystem.getVersionInfo().getCodeName()
                + motherboard.getHardwareUUID()
                + motherboard.getSerialNumber()
                + motherboard.getModel()
                + motherboard.getManufacturer()
                + firmware.getName()
                + firmware.getManufacturer()
                + baseboard.getManufacturer()
                + baseboard.getSerialNumber()
                + baseboard.getModel();
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(string.getBytes(StandardCharsets.UTF_8));
            byte[] digest = messageDigest.digest();
            return "pulsive-" + Base64.getEncoder().encodeToString(digest);
        } catch (Exception ignore) {}

        return null;
    }
}
