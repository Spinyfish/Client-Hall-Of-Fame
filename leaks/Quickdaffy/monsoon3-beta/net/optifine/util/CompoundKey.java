/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import net.minecraft.src.Config;

public class CompoundKey {
    private Object[] keys;
    private int hashcode = 0;

    public CompoundKey(Object[] keys) {
        this.keys = (Object[])keys.clone();
    }

    public CompoundKey(Object k1, Object k2) {
        this(new Object[]{k1, k2});
    }

    public CompoundKey(Object k1, Object k2, Object k3) {
        this(new Object[]{k1, k2, k3});
    }

    public int hashCode() {
        if (this.hashcode == 0) {
            this.hashcode = 7;
            for (int i = 0; i < this.keys.length; ++i) {
                Object object = this.keys[i];
                if (object == null) continue;
                this.hashcode = 31 * this.hashcode + object.hashCode();
            }
        }
        return this.hashcode;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CompoundKey)) {
            return false;
        }
        CompoundKey compoundkey = (CompoundKey)obj;
        Object[] aobject = compoundkey.getKeys();
        if (aobject.length != this.keys.length) {
            return false;
        }
        for (int i = 0; i < this.keys.length; ++i) {
            if (CompoundKey.compareKeys(this.keys[i], aobject[i])) continue;
            return false;
        }
        return true;
    }

    private static boolean compareKeys(Object key1, Object key2) {
        return key1 == key2 ? true : (key1 == null ? false : (key2 == null ? false : key1.equals(key2)));
    }

    private Object[] getKeys() {
        return this.keys;
    }

    public Object[] getKeysCopy() {
        return (Object[])this.keys.clone();
    }

    public String toString() {
        return "[" + Config.arrayToString(this.keys) + "]";
    }
}

