/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.IntIterable;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterator;

public final class IntIterables {
    private IntIterables() {
    }

    public static long size(IntIterable iterable) {
        long c = 0L;
        IntIterator intIterator = iterable.iterator();
        while (intIterator.hasNext()) {
            int dummy = (Integer)intIterator.next();
            ++c;
        }
        return c;
    }
}

