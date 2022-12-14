// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import kotlin.collections.IntIterator;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;
import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\t\b&\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0002B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0013\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00028\u0000¢\u0006\u0002\u0010\u0012J\b\u0010\u0003\u001a\u00020\u0004H\u0004J\u001d\u0010\u0013\u001a\u00028\u00002\u0006\u0010\u0014\u001a\u00028\u00002\u0006\u0010\u0015\u001a\u00028\u0000H\u0004¢\u0006\u0002\u0010\u0016J\u0011\u0010\u0017\u001a\u00020\u0004*\u00028\u0000H$¢\u0006\u0002\u0010\u0018R\u001a\u0010\u0006\u001a\u00020\u0004X\u0084\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\u0005R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00018\u00000\u000bX\u0082\u0004¢\u0006\n\n\u0002\u0010\u000e\u0012\u0004\b\f\u0010\r¨\u0006\u0019" }, d2 = { "Lkotlin/jvm/internal/PrimitiveSpreadBuilder;", "T", "", "size", "", "(I)V", "position", "getPosition", "()I", "setPosition", "spreads", "", "getSpreads$annotations", "()V", "[Ljava/lang/Object;", "addSpread", "", "spreadArgument", "(Ljava/lang/Object;)V", "toArray", "values", "result", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "getSize", "(Ljava/lang/Object;)I", "kotlin-stdlib" })
public abstract class PrimitiveSpreadBuilder<T>
{
    private final int size;
    private int position;
    @NotNull
    private final T[] spreads;
    
    public PrimitiveSpreadBuilder(final int size) {
        this.size = size;
        this.spreads = (T[])new Object[this.size];
    }
    
    protected abstract int getSize(@NotNull final T p0);
    
    protected final int getPosition() {
        return this.position;
    }
    
    protected final void setPosition(final int <set-?>) {
        this.position = <set-?>;
    }
    
    public final void addSpread(@NotNull final T spreadArgument) {
        Intrinsics.checkNotNullParameter(spreadArgument, "spreadArgument");
        this.spreads[this.position++] = spreadArgument;
    }
    
    protected final int size() {
        int totalLength = 0;
        final IntIterator iterator = new IntRange(0, this.size - 1).iterator();
        while (iterator.hasNext()) {
            final int i = iterator.nextInt();
            final int n = totalLength;
            final T t = this.spreads[i];
            totalLength = n + ((t != null) ? this.getSize(t) : 1);
        }
        return totalLength;
    }
    
    @NotNull
    protected final T toArray(@NotNull final T values, @NotNull final T result) {
        Intrinsics.checkNotNullParameter(values, "values");
        Intrinsics.checkNotNullParameter(result, "result");
        int dstIndex = 0;
        int copyValuesFrom = 0;
        final IntIterator iterator = new IntRange(0, this.size - 1).iterator();
        while (iterator.hasNext()) {
            final int i = iterator.nextInt();
            final Object spreadArgument = this.spreads[i];
            if (spreadArgument != null) {
                if (copyValuesFrom < i) {
                    System.arraycopy(values, copyValuesFrom, result, dstIndex, i - copyValuesFrom);
                    dstIndex += i - copyValuesFrom;
                }
                final int spreadSize = this.getSize(spreadArgument);
                System.arraycopy(spreadArgument, 0, result, dstIndex, spreadSize);
                dstIndex += spreadSize;
                copyValuesFrom = i + 1;
            }
        }
        if (copyValuesFrom < this.size) {
            System.arraycopy(values, copyValuesFrom, result, dstIndex, this.size - copyValuesFrom);
        }
        return result;
    }
}
