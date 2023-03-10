/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Memory;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;

public class WeakMemoryHolder {
    ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
    IdentityHashMap<Reference<Object>, Memory> backingMap = new IdentityHashMap();

    public synchronized void put(Object o, Memory m) {
        this.clean();
        WeakReference<Object> reference = new WeakReference<Object>(o, this.referenceQueue);
        this.backingMap.put(reference, m);
    }

    public synchronized void clean() {
        Reference<Object> ref = this.referenceQueue.poll();
        while (ref != null) {
            this.backingMap.remove(ref);
            ref = this.referenceQueue.poll();
        }
    }
}

