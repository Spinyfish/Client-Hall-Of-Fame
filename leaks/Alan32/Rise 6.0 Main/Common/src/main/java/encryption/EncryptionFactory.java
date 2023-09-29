package encryption;

import encryption.impl.AESEncryption;
import util.interfaces.Factory;

public final class EncryptionFactory implements Factory<Encryption> {

    private String key;

    public EncryptionFactory setKey(final String key) {
        this.key = key;
        return this;
    }

    @Override
    public Encryption build() {
        return new AESEncryption(this.key);
    }
}
