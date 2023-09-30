/*
 * Decompiled with CFR 0.152.
 */
package encryption;

import encryption.Encryption;
import encryption.impl.AESEncryption;
import util.interfaces.Factory;

public final class EncryptionFactory
implements Factory<Encryption> {
    private String key;

    public EncryptionFactory setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public Encryption build() {
        return new AESEncryption(this.key);
    }
}

