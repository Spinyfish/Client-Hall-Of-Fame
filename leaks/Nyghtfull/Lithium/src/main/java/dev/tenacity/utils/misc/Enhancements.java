package dev.tenacity.utils.misc;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Enhancements {

    /**
     * Patcher: Resource Exploit Fix <a href="https://github.com/Sk1erLLC/Patcher/blob/master/src/main/java/club/sk1er/patcher/hooks/NetHandlerPlayClientHook.java">Patcher NetHandlerPlayClientHook</a>
     */
    public static boolean validateResourcePackUrl(NetHandlerPlayClient client, S48PacketResourcePackSend packet) {
        try {
            String url = packet.getURL();
            final URI uri = new URI(url);
            final String scheme = uri.getScheme();
            final boolean isLevelProtocol = scheme.equals("level");

            if (!scheme.equals("http") && !scheme.equals("https") && !isLevelProtocol) {
                client.getNetworkManager().sendPacket(new C19PacketResourcePackStatus(packet.getHash(), C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                throw new URISyntaxException(url, "Wrong protocol");
            }

            url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());

            if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            }

            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

}
