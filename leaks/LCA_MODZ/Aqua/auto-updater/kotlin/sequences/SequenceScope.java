// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import kotlin.coroutines.intrinsics.IntrinsicsKt;
import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.SinceKotlin;
import kotlin.coroutines.RestrictsSuspension;
import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000.\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u001c\n\u0002\b\u0002\n\u0002\u0010(\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b'\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00002\u00020\u0002B\u0007\b\u0000¢\u0006\u0002\u0010\u0003J\u0019\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00028\u0000H¦@\u00f8\u0001\u0000¢\u0006\u0002\u0010\u0007J\u001f\u0010\b\u001a\u00020\u00052\f\u0010\t\u001a\b\u0012\u0004\u0012\u00028\u00000\nH\u0086@\u00f8\u0001\u0000¢\u0006\u0002\u0010\u000bJ\u001f\u0010\b\u001a\u00020\u00052\f\u0010\f\u001a\b\u0012\u0004\u0012\u00028\u00000\rH¦@\u00f8\u0001\u0000¢\u0006\u0002\u0010\u000eJ\u001f\u0010\b\u001a\u00020\u00052\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00028\u00000\u0010H\u0086@\u00f8\u0001\u0000¢\u0006\u0002\u0010\u0011\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0012" }, d2 = { "Lkotlin/sequences/SequenceScope;", "T", "", "()V", "yield", "", "value", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "yieldAll", "elements", "", "(Ljava/lang/Iterable;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "iterator", "", "(Ljava/util/Iterator;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sequence", "Lkotlin/sequences/Sequence;", "(Lkotlin/sequences/Sequence;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlin-stdlib" })
@RestrictsSuspension
@SinceKotlin(version = "1.3")
public abstract class SequenceScope<T>
{
    @Nullable
    public abstract Object yield(final T p0, @NotNull final Continuation<? super Unit> p1);
    
    @Nullable
    public abstract Object yieldAll(@NotNull final Iterator<? extends T> p0, @NotNull final Continuation<? super Unit> p1);
    
    @Nullable
    public final Object yieldAll(@NotNull final Iterable<? extends T> elements, @NotNull final Continuation<? super Unit> $completion) {
        if (elements instanceof Collection && ((Collection)elements).isEmpty()) {
            return Unit.INSTANCE;
        }
        final Object yieldAll = this.yieldAll(elements.iterator(), $completion);
        if (yieldAll == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            return yieldAll;
        }
        return Unit.INSTANCE;
    }
    
    @Nullable
    public final Object yieldAll(@NotNull final Sequence<? extends T> sequence, @NotNull final Continuation<? super Unit> $completion) {
        final Object yieldAll = this.yieldAll(sequence.iterator(), $completion);
        if (yieldAll == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            return yieldAll;
        }
        return Unit.INSTANCE;
    }
}
