/* November.lol © 2023 */
package lol.november.utility.math;

/**
 * @author TerrificTable
 * @since 1.0.0
 */
public class EncryptionUtils {

  /**
   * four step xor (split xor key in 4 parts and encrypt everything four times)
   * example: xor4("abc".toCharArray(), 0xabcdef)
   *
   * (oh wow, this looks like shit with the new formatting)
   *
   * @param inp input string (char[])
   * @param key xor key
   * @return xor encrypted input (char[])
   */
  public static char[] xor4(char[] inp, int key) {
    int k1 = key & 0xff, k2 = (key << 8) & 0xff, k3 = (key << 16) & 0xff, k4 =
      (key << 24) & 0xff;

    char[] out = new char[inp.length];
    for (int i = 0; i < inp.length; i++) {
      int tmp = inp[i] ^ k1;
      tmp = (tmp ^ k2) ^ k3;

      out[i] = (char) (tmp ^ k4);
    }

    return out;
  }

  /**
   * one step xor
   * example: xor("abc".toCharArray(), 0xab)
   *
   * @param inp input string (char[])
   * @param key encryption key
   * @return xor encrypted input (char[])
   */
  public static char[] xor(char[] inp, int key) {
    char[] out = new char[inp.length];
    for (int i = 0; i < inp.length; i++) out[i] = (char) (inp[i] ^ key);
    return out;
  }
}
