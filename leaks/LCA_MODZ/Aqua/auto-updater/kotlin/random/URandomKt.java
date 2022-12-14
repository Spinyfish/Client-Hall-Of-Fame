// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.random;

import kotlin.UByteArray;
import kotlin.ranges.ULongRange;
import kotlin.ULong;
import kotlin.UnsignedKt;
import kotlin.ranges.UIntRange;
import kotlin.ExperimentalUnsignedTypes;
import kotlin.WasExperimental;
import kotlin.SinceKotlin;
import kotlin.UInt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 2, xi = 48, d1 = { "\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\"\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u0000\u00f8\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006\u001a\"\u0010\u0007\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\bH\u0000\u00f8\u0001\u0000¢\u0006\u0004\b\t\u0010\n\u001a\u001c\u0010\u000b\u001a\u00020\f*\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007\u00f8\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u001e\u0010\u000b\u001a\u00020\f*\u00020\r2\u0006\u0010\u0011\u001a\u00020\fH\u0007\u00f8\u0001\u0000¢\u0006\u0004\b\u0012\u0010\u0013\u001a2\u0010\u000b\u001a\u00020\f*\u00020\r2\u0006\u0010\u0011\u001a\u00020\f2\b\b\u0002\u0010\u0014\u001a\u00020\u000f2\b\b\u0002\u0010\u0015\u001a\u00020\u000fH\u0007\u00f8\u0001\u0000¢\u0006\u0004\b\u0016\u0010\u0017\u001a\u0014\u0010\u0018\u001a\u00020\u0003*\u00020\rH\u0007\u00f8\u0001\u0000¢\u0006\u0002\u0010\u0019\u001a\u001e\u0010\u0018\u001a\u00020\u0003*\u00020\r2\u0006\u0010\u0004\u001a\u00020\u0003H\u0007\u00f8\u0001\u0000¢\u0006\u0004\b\u001a\u0010\u001b\u001a&\u0010\u0018\u001a\u00020\u0003*\u00020\r2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u0007\u00f8\u0001\u0000¢\u0006\u0004\b\u001c\u0010\u001d\u001a\u001c\u0010\u0018\u001a\u00020\u0003*\u00020\r2\u0006\u0010\u001e\u001a\u00020\u001fH\u0007\u00f8\u0001\u0000¢\u0006\u0002\u0010 \u001a\u0014\u0010!\u001a\u00020\b*\u00020\rH\u0007\u00f8\u0001\u0000¢\u0006\u0002\u0010\"\u001a\u001e\u0010!\u001a\u00020\b*\u00020\r2\u0006\u0010\u0004\u001a\u00020\bH\u0007\u00f8\u0001\u0000¢\u0006\u0004\b#\u0010$\u001a&\u0010!\u001a\u00020\b*\u00020\r2\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\bH\u0007\u00f8\u0001\u0000¢\u0006\u0004\b%\u0010&\u001a\u001c\u0010!\u001a\u00020\b*\u00020\r2\u0006\u0010\u001e\u001a\u00020'H\u0007\u00f8\u0001\u0000¢\u0006\u0002\u0010(\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006)" }, d2 = { "checkUIntRangeBounds", "", "from", "Lkotlin/UInt;", "until", "checkUIntRangeBounds-J1ME1BU", "(II)V", "checkULongRangeBounds", "Lkotlin/ULong;", "checkULongRangeBounds-eb3DHEI", "(JJ)V", "nextUBytes", "Lkotlin/UByteArray;", "Lkotlin/random/Random;", "size", "", "(Lkotlin/random/Random;I)[B", "array", "nextUBytes-EVgfTAA", "(Lkotlin/random/Random;[B)[B", "fromIndex", "toIndex", "nextUBytes-Wvrt4B4", "(Lkotlin/random/Random;[BII)[B", "nextUInt", "(Lkotlin/random/Random;)I", "nextUInt-qCasIEU", "(Lkotlin/random/Random;I)I", "nextUInt-a8DCA5k", "(Lkotlin/random/Random;II)I", "range", "Lkotlin/ranges/UIntRange;", "(Lkotlin/random/Random;Lkotlin/ranges/UIntRange;)I", "nextULong", "(Lkotlin/random/Random;)J", "nextULong-V1Xi4fY", "(Lkotlin/random/Random;J)J", "nextULong-jmpaW-c", "(Lkotlin/random/Random;JJ)J", "Lkotlin/ranges/ULongRange;", "(Lkotlin/random/Random;Lkotlin/ranges/ULongRange;)J", "kotlin-stdlib" })
public final class URandomKt
{
    @SinceKotlin(version = "1.5")
    @WasExperimental(markerClass = { ExperimentalUnsignedTypes.class })
    public static final int nextUInt(@NotNull final Random $this$nextUInt) {
        Intrinsics.checkNotNullParameter($this$nextUInt, "<this>");
        return UInt.constructor-impl($this$nextUInt.nextInt());
    }
    
    @SinceKotlin(version = "1.5")
    @WasExperimental(markerClass = { ExperimentalUnsignedTypes.class })
    public static final int nextUInt-qCasIEU(@NotNull final Random $this$nextUInt_u2dqCasIEU, final int until) {
        Intrinsics.checkNotNullParameter($this$nextUInt_u2dqCasIEU, "$this$nextUInt");
        return nextUInt-a8DCA5k($this$nextUInt_u2dqCasIEU, 0, until);
    }
    
    @SinceKotlin(version = "1.5")
    @WasExperimental(markerClass = { ExperimentalUnsignedTypes.class })
    public static final int nextUInt-a8DCA5k(@NotNull final Random $this$nextUInt_u2da8DCA5k, final int from, final int until) {
        Intrinsics.checkNotNullParameter($this$nextUInt_u2da8DCA5k, "$this$nextUInt");
        checkUIntRangeBounds-J1ME1BU(from, until);
        final int signedFrom = from ^ Integer.MIN_VALUE;
        final int signedUntil = until ^ Integer.MIN_VALUE;
        final int signedResult = $this$nextUInt_u2da8DCA5k.nextInt(signedFrom, signedUntil) ^ Integer.MIN_VALUE;
        return UInt.constructor-impl(signedResult);
    }
    
    @SinceKotlin(version = "1.5")
    @WasExperimental(markerClass = { ExperimentalUnsignedTypes.class })
    public static final int nextUInt(@NotNull final Random $this$nextUInt, @NotNull final UIntRange range) {
        Intrinsics.checkNotNullParameter($this$nextUInt, "<this>");
        Intrinsics.checkNotNullParameter(range, "range");
        if (range.isEmpty()) {
            throw new IllegalArgumentException("Cannot get random in empty range: " + range);
        }
        return (UnsignedKt.uintCompare(range.getLast-pVg5ArA(), -1) < 0) ? nextUInt-a8DCA5k($this$nextUInt, range.getFirst-pVg5ArA(), UInt.constructor-impl(range.getLast-pVg5ArA() + 1)) : ((UnsignedKt.uintCompare(range.getFirst-pVg5ArA(), 0) > 0) ? UInt.constructor-impl(nextUInt-a8DCA5k($this$nextUInt, UInt.constructor-impl(range.getFirst-pVg5ArA() - 1), range.getLast-pVg5ArA()) + 1) : nextUInt($this$nextUInt));
    }
    
    @SinceKotlin(version = "1.5")
    @WasExperimental(markerClass = { ExperimentalUnsignedTypes.class })
    public static final long nextULong(@NotNull final Random $this$nextULong) {
        Intrinsics.checkNotNullParameter($this$nextULong, "<this>");
        return ULong.constructor-impl($this$nextULong.nextLong());
    }
    
    @SinceKotlin(version = "1.5")
    @WasExperimental(markerClass = { ExperimentalUnsignedTypes.class })
    public static final long nextULong-V1Xi4fY(@NotNull final Random $this$nextULong_u2dV1Xi4fY, final long until) {
        Intrinsics.checkNotNullParameter($this$nextULong_u2dV1Xi4fY, "$this$nextULong");
        return nextULong-jmpaW-c($this$nextULong_u2dV1Xi4fY, 0L, until);
    }
    
    @SinceKotlin(version = "1.5")
    @WasExperimental(markerClass = { ExperimentalUnsignedTypes.class })
    public static final long nextULong-jmpaW-c(@NotNull final Random $this$nextULong_u2djmpaW_u2dc, final long from, final long until) {
        Intrinsics.checkNotNullParameter($this$nextULong_u2djmpaW_u2dc, "$this$nextULong");
        checkULongRangeBounds-eb3DHEI(from, until);
        final long signedFrom = from ^ Long.MIN_VALUE;
        final long signedUntil = until ^ Long.MIN_VALUE;
        final long signedResult = $this$nextULong_u2djmpaW_u2dc.nextLong(signedFrom, signedUntil) ^ Long.MIN_VALUE;
        return ULong.constructor-impl(signedResult);
    }
    
    @SinceKotlin(version = "1.5")
    @WasExperimental(markerClass = { ExperimentalUnsignedTypes.class })
    public static final long nextULong(@NotNull final Random $this$nextULong, @NotNull final ULongRange range) {
        Intrinsics.checkNotNullParameter($this$nextULong, "<this>");
        Intrinsics.checkNotNullParameter(range, "range");
        if (range.isEmpty()) {
            throw new IllegalArgumentException("Cannot get random in empty range: " + range);
        }
        return (UnsignedKt.ulongCompare(range.getLast-s-VKNKU(), -1L) < 0) ? nextULong-jmpaW-c($this$nextULong, range.getFirst-s-VKNKU(), ULong.constructor-impl(range.getLast-s-VKNKU() + ULong.constructor-impl((long)1 & 0xFFFFFFFFL))) : ((UnsignedKt.ulongCompare(range.getFirst-s-VKNKU(), 0L) > 0) ? ULong.constructor-impl(nextULong-jmpaW-c($this$nextULong, ULong.constructor-impl(range.getFirst-s-VKNKU() - ULong.constructor-impl((long)1 & 0xFFFFFFFFL)), range.getLast-s-VKNKU()) + ULong.constructor-impl((long)1 & 0xFFFFFFFFL)) : nextULong($this$nextULong));
    }
    
    @SinceKotlin(version = "1.3")
    @ExperimentalUnsignedTypes
    @NotNull
    public static final byte[] nextUBytes-EVgfTAA(@NotNull final Random $this$nextUBytes_u2dEVgfTAA, @NotNull final byte[] array) {
        Intrinsics.checkNotNullParameter($this$nextUBytes_u2dEVgfTAA, "$this$nextUBytes");
        Intrinsics.checkNotNullParameter(array, "array");
        $this$nextUBytes_u2dEVgfTAA.nextBytes(array);
        return array;
    }
    
    @SinceKotlin(version = "1.3")
    @ExperimentalUnsignedTypes
    @NotNull
    public static final byte[] nextUBytes(@NotNull final Random $this$nextUBytes, final int size) {
        Intrinsics.checkNotNullParameter($this$nextUBytes, "<this>");
        return UByteArray.constructor-impl($this$nextUBytes.nextBytes(size));
    }
    
    @SinceKotlin(version = "1.3")
    @ExperimentalUnsignedTypes
    @NotNull
    public static final byte[] nextUBytes-Wvrt4B4(@NotNull final Random $this$nextUBytes_u2dWvrt4B4, @NotNull final byte[] array, final int fromIndex, final int toIndex) {
        Intrinsics.checkNotNullParameter($this$nextUBytes_u2dWvrt4B4, "$this$nextUBytes");
        Intrinsics.checkNotNullParameter(array, "array");
        $this$nextUBytes_u2dWvrt4B4.nextBytes(array, fromIndex, toIndex);
        return array;
    }
    
    public static final void checkUIntRangeBounds-J1ME1BU(final int from, final int until) {
        if (UnsignedKt.uintCompare(until, from) <= 0) {
            final int n = 0;
            throw new IllegalArgumentException(RandomKt.boundsErrorMessage(UInt.box-impl(from), UInt.box-impl(until)).toString());
        }
    }
    
    public static final void checkULongRangeBounds-eb3DHEI(final long from, final long until) {
        if (UnsignedKt.ulongCompare(until, from) <= 0) {
            final int n = 0;
            throw new IllegalArgumentException(RandomKt.boundsErrorMessage(ULong.box-impl(from), ULong.box-impl(until)).toString());
        }
    }
}
