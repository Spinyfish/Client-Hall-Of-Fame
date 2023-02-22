package net.optifine.util;

import net.minecraft.src.Config;

public class CompoundKey {
    private final Object[] keys;
    private int hashcode;

    public CompoundKey(final Object[] keys) {
        this.hashcode = 0;
        this.keys = keys.clone();
    }

    public CompoundKey(final Object k1, final Object k2) {
        this(new Object[]{k1, k2});
    }

    public CompoundKey(final Object k1, final Object k2, final Object k3) {
        this(new Object[]{k1, k2, k3});
    }

    public int hashCode() {
        if (this.hashcode == 0) {
            this.hashcode = 7;

            for (int i = 0; i < this.keys.length; ++i) {
                final Object object = this.keys[i];

                if (object != null) {
                    this.hashcode = 31 * this.hashcode + object.hashCode();
                }
            }
        }

        return this.hashcode;
    }

    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (!(obj instanceof CompoundKey)) {
            return false;
        } else {
            final CompoundKey compoundkey = (CompoundKey) obj;
            final Object[] aobject = compoundkey.getKeys();

            if (aobject.length != this.keys.length) {
                return false;
            } else {
                for (int i = 0; i < this.keys.length; ++i) {
                    if (!compareKeys(this.keys[i], aobject[i])) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private static boolean compareKeys(final Object key1, final Object key2) {
        return key1 == key2 || (key1 != null && (key2 != null && key1.equals(key2)));
    }

    private Object[] getKeys() {
        return this.keys;
    }

    public Object[] getKeysCopy() {
        return this.keys.clone();
    }

    public String toString() {
        return "[" + Config.arrayToString(this.keys) + "]";
    }
}
