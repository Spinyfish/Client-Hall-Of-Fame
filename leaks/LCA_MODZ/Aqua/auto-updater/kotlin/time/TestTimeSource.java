// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.time;

import kotlin.SinceKotlin;
import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 1, xi = 48, d1 = { "\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002\u00f8\u0001\u0000¢\u0006\u0004\b\t\u0010\nJ\u001b\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086\u0002\u00f8\u0001\u0000¢\u0006\u0004\b\f\u0010\nJ\b\u0010\r\u001a\u00020\u0004H\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u000e" }, d2 = { "Lkotlin/time/TestTimeSource;", "Lkotlin/time/AbstractLongTimeSource;", "()V", "reading", "", "overflow", "", "duration", "Lkotlin/time/Duration;", "overflow-LRDsOJo", "(J)V", "plusAssign", "plusAssign-LRDsOJo", "read", "kotlin-stdlib" })
@SinceKotlin(version = "1.3")
@ExperimentalTime
public final class TestTimeSource extends AbstractLongTimeSource
{
    private long reading;
    
    public TestTimeSource() {
        super(DurationUnit.NANOSECONDS);
    }
    
    @Override
    protected long read() {
        return this.reading;
    }
    
    public final void plusAssign-LRDsOJo(final long duration) {
        final long longDelta = Duration.toLong-impl(duration, this.getUnit());
        long reading;
        if (longDelta != Long.MIN_VALUE && longDelta != Long.MAX_VALUE) {
            final long newReading = this.reading + longDelta;
            if ((this.reading ^ longDelta) >= 0L && (this.reading ^ newReading) < 0L) {
                this.overflow-LRDsOJo(duration);
            }
            reading = newReading;
        }
        else {
            final double delta = Duration.toDouble-impl(duration, this.getUnit());
            final double newReading2 = this.reading + delta;
            if (newReading2 > 9.223372036854776E18 || newReading2 < -9.223372036854776E18) {
                this.overflow-LRDsOJo(duration);
            }
            reading = (long)newReading2;
        }
        this.reading = reading;
    }
    
    private final void overflow-LRDsOJo(final long duration) {
        throw new IllegalStateException("TestTimeSource will overflow if its reading " + this.reading + "ns is advanced by " + (Object)Duration.toString-impl(duration) + '.');
    }
}
