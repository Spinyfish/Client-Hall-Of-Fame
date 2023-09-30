package club.pulsive.impl.networking.packet;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PacketEncoder {

    public static String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String data) {
        return new String(Base64.getDecoder().decode(data));
    }

}
