package best.azura.irc.utils;

import best.azura.irc.core.channels.ChannelManager;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyPair;
import java.util.Base64;

/**
 * Class to save IRC-SQL Data for usage.
 */
public class IRCData {

    // Instance of the Channel-Manager.
    public ChannelManager channelManager = new ChannelManager();

    public KeyPair keyPair;

    public Key AESKey;

    public IRCData() {
        try {
            if (keyPair == null) keyPair = RSAUtil.generateKeyPair();
        } catch (Exception ignored) {}
    }


    public void setAESKey(String key) {
        try {
            AESKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        } catch (Exception ignored) {}
    }

    public Key getAESKey() {
        return AESKey;
    }
}
