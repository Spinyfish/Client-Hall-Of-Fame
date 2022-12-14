// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.SinceKotlin;
import kotlin.Metadata;
import kotlin.jvm.internal.markers.KMutableList;
import java.util.List;
import java.util.AbstractList;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\b'\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0007\b\u0004¢\u0006\u0002\u0010\u0004J\u001d\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00028\u0000H&¢\u0006\u0002\u0010\nJ\u0015\u0010\u000b\u001a\u00028\u00002\u0006\u0010\u0007\u001a\u00020\bH&¢\u0006\u0002\u0010\fJ\u001e\u0010\r\u001a\u00028\u00002\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00028\u0000H¦\u0002¢\u0006\u0002\u0010\u000e¨\u0006\u000f" }, d2 = { "Lkotlin/collections/AbstractMutableList;", "E", "", "Ljava/util/AbstractList;", "()V", "add", "", "index", "", "element", "(ILjava/lang/Object;)V", "removeAt", "(I)Ljava/lang/Object;", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", "kotlin-stdlib" })
@SinceKotlin(version = "1.1")
public abstract class AbstractMutableList<E> extends AbstractList<E> implements List<E>, KMutableList
{
    protected AbstractMutableList() {
    }
    
    @Override
    public abstract E set(final int p0, final E p1);
    
    public abstract E removeAt(final int p0);
    
    @Override
    public abstract void add(final int p0, final E p1);
    
    @Override
    public final /* bridge */ E remove(final int index) {
        return this.removeAt(index);
    }
    
    public abstract int getSize();
    
    @Override
    public final /* bridge */ int size() {
        return this.getSize();
    }
}
