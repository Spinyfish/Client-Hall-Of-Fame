// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import org.jetbrains.annotations.NotNull;
import kotlin.Metadata;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.Iterator;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010(\n\u0002\u0010\n\n\u0002\b\u0005\b&\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u0002H\u0086\u0002¢\u0006\u0002\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0002H&¨\u0006\u0007" }, d2 = { "Lkotlin/collections/ShortIterator;", "", "", "()V", "next", "()Ljava/lang/Short;", "nextShort", "kotlin-stdlib" })
public abstract class ShortIterator implements Iterator<Short>, KMappedMarker
{
    @NotNull
    @Override
    public final Short next() {
        return this.nextShort();
    }
    
    public abstract short nextShort();
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
