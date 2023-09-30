package dev.eternal.util;

import lombok.SneakyThrows;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import java.math.BigInteger;
import java.security.MessageDigest;

public class HWID {

  /**
   * @return A HWID made up of everything a computer may contain + "my name jef".
   */
  @SneakyThrows
  public static String get() {
    final HardwareAbstractionLayer hardware = new SystemInfo().getHardware();
    final CentralProcessor.ProcessorIdentifier identifier =
        hardware.getProcessor().getProcessorIdentifier();
    String gpu = "";
    if (!hardware.getGraphicsCards().isEmpty()) {
      final GraphicsCard graphicsCard = hardware.getGraphicsCards().get(0);
      gpu =
          graphicsCard.getName()
              + graphicsCard.getVendor()
              + graphicsCard.getDeviceId();
    }
    String combination =
        System.getProperty("user.name")
            + "my name jef"
            + System.getProperty("os.name")
            + System.getProperty("os.version")
            + System.getProperty("os.arch")
            + identifier.getIdentifier()
            + identifier.getFamily()
            + identifier.getName()
            + identifier.getModel()
            + identifier.getStepping()
            + identifier.getMicroarchitecture()
            + identifier.getVendor()
            + gpu
            + hardware.getComputerSystem().getHardwareUUID();

    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] messageDigest = md.digest(combination.getBytes());

    BigInteger no = new BigInteger(1, messageDigest);
    StringBuilder hashText = new StringBuilder(no.toString(16));

    while (hashText.length() < 32) {
      hashText.insert(0, "0");
    }

    return hashText.toString();
  }

  public void hwid() {
    System.out.println(get());
  }
}
