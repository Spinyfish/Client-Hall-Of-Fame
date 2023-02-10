/**
 * @project Myth
 * @author Skush/Duzey
 * @at 12.08.2022
 */
package dev.myth.api.utils.encryption;

import club.antiskid.annotations.Obfuscate;
import dev.myth.main.ClientMain;

@Obfuscate
public class TempKeyUtil {
    private static String tempKey;

    public static String getDecryptedTempKey() {
        if (tempKey == null)
            tempKey = AES256.decryptStr(ClientMain.INSTANCE.getTempKey(), EncryptionUtil.getEncryptionKey(), EncryptionUtil.getSalt());
        return tempKey;
    }
}
