package encryption;

public interface Encryption {
    byte[] encrypt(final byte[] bytes);

    byte[] decrypt(final byte[] bytes);
}
