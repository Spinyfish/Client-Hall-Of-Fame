// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.internal.CollectionToArray;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import java.util.Iterator;
import kotlin.SinceKotlin;
import kotlin.Metadata;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.Collection;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u00006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010(\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\b'\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0007\b\u0004¢\u0006\u0002\u0010\u0003J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00028\u00000\u0002H\u0016J\b\u0010\u000e\u001a\u00020\tH\u0016J\u000f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00028\u00000\u0010H¦\u0002J\u0015\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00130\u0012H\u0015¢\u0006\u0002\u0010\u0014J'\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00150\u0012\"\u0004\b\u0001\u0010\u00152\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u0002H\u00150\u0012H\u0014¢\u0006\u0002\u0010\u0017J\b\u0010\u0018\u001a\u00020\u0019H\u0016R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u001a" }, d2 = { "Lkotlin/collections/AbstractCollection;", "E", "", "()V", "size", "", "getSize", "()I", "contains", "", "element", "(Ljava/lang/Object;)Z", "containsAll", "elements", "isEmpty", "iterator", "", "toArray", "", "", "()[Ljava/lang/Object;", "T", "array", "([Ljava/lang/Object;)[Ljava/lang/Object;", "toString", "", "kotlin-stdlib" })
@SinceKotlin(version = "1.1")
public abstract class AbstractCollection<E> implements Collection<E>, KMappedMarker
{
    protected AbstractCollection() {
    }
    
    public abstract int getSize();
    
    @NotNull
    @Override
    public abstract Iterator<E> iterator();
    
    @Override
    public boolean contains(final E element) {
        final Iterable $this$any$iv = this;
        final int $i$f$any = 0;
        boolean b;
        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
            b = false;
        }
        else {
            for (final Object it : $this$any$iv) {
                final Object element$iv = it;
                final int n = 0;
                if (Intrinsics.areEqual(it, element)) {
                    b = true;
                    return b;
                }
            }
            b = false;
        }
        return b;
    }
    
    @Override
    public boolean containsAll(@NotNull final Collection<?> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        final Iterable $this$all$iv = elements;
        final int $i$f$all = 0;
        boolean b;
        if (((Collection)$this$all$iv).isEmpty()) {
            b = true;
        }
        else {
            for (final Object it : $this$all$iv) {
                final Object element$iv = it;
                final int n = 0;
                if (!this.contains(it)) {
                    b = false;
                    return b;
                }
            }
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @NotNull
    @Override
    public String toString() {
        return CollectionsKt___CollectionsKt.joinToString$default(this, ", ", "[", "]", 0, null, (Function1)new AbstractCollection$toString.AbstractCollection$toString$1(this), 24, null);
    }
    
    @NotNull
    @Override
    public Object[] toArray() {
        return CollectionToArray.toArray(this);
    }
    
    @NotNull
    @Override
    public <T> T[] toArray(@NotNull final T[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        final Object[] array2 = CollectionToArray.toArray(this, array);
        Intrinsics.checkNotNull(array2, "null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.CollectionsKt__CollectionsJVMKt.copyToArrayImpl>");
        return (T[])array2;
    }
    
    @Override
    public boolean add(final E element) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> elements) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean remove(final Object element) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean removeAll(final Collection<?> elements) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean retainAll(final Collection<?> elements) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public final /* bridge */ int size() {
        return this.getSize();
    }
}
