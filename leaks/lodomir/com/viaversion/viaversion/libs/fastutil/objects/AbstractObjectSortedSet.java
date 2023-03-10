/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSet;

public abstract class AbstractObjectSortedSet<K>
extends AbstractObjectSet<K>
implements ObjectSortedSet<K> {
    protected AbstractObjectSortedSet() {
    }

    @Override
    public abstract ObjectBidirectionalIterator<K> iterator();
}

