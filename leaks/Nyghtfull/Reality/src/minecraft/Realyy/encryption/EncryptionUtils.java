package Realyy.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class EncryptionUtils {

    private static final IvParameterSpec IV_PARAMETER_SPEC = new IvParameterSpec("0000000000000000".getBytes());


    /**
     * 加密�?�??六进制字符串
     *
     * <p>
     * 使用AES加密，并将Cipher加密�?�的byte数组转�?��?16进制字符串
     * </p>
     *
     * @author Cr
     * @date 2020-03-22
     */
    public static String encryptIntoHexString(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), IV_PARAMETER_SPEC);
            return bytesConvertHexString(cipher.doFinal(Arrays.copyOf(data.getBytes(StandardCharsets.UTF_8), 16 * ((data.getBytes(StandardCharsets.UTF_8).length / 16) + 1))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将加密�?�的�??六进制字符串进行解密
     *
     * @author Cr
     * @date 2020-03-22
     **/
    public static String decryptByHexString(String data, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), IV_PARAMETER_SPEC);
        return new String(cipher.doFinal(hexStringConvertBytes(data.toLowerCase())), StandardCharsets.UTF_8);
    }


    /**
     * byte数组转�?��?�??六进制字符串
     *
     * <p>
     * 先对�?个byte数值补�?�?�??进制,
     * 然�?�在将�??进制转�?��?对应的�??六进制.
     * 如果�?�次转�?�, �??六进制�?�有一�?时， 将在�?�?�追加0�?��?两�?.
     * </p>
     *
     * @author Cr
     * @date 2020-03-22
     */
    private static String bytesConvertHexString(byte[] data) {
        StringBuffer result = new StringBuffer();
        String hexString = "";
        for (byte b : data) {
            // 补�?�?正�??进制�?�转�?��?16进制
            hexString = Integer.toHexString(b & 255);
            result.append(hexString.length() == 1 ? "0" + hexString : hexString);
        }
        return result.toString().toUpperCase();
    }

    /**
     * �??六进制字符串转�?��?byte数组
     *
     * <p>
     * 在加密时, �??六进制数值和byte字节的对应关系 是:  2个�??六进制数值对应  1个byte字节  (2: 1)
     * 所以byte数组的长度应该是�??六进制字符串的一�?�, 并且在转�?�时
     * 应是两个�??六进制数值转�?��?一个byte字节  (2个2个�??六进制数值进行转�?�)
     * 这也是为什么�?�以*2的原因， 例如: 0, 2, 4, 6, 8, 10, 12 �?次�??历
     * </p>
     *
     * @author Cr
     * @date 2020-04-22
     */
    private static byte[] hexStringConvertBytes(String data) {
        int length = data.length() / 2;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            int first = Integer.parseInt(data.substring(i * 2, i * 2 + 1), 16);
            int second = Integer.parseInt(data.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (first * 16 + second);
        }
        return result;
    }

}
