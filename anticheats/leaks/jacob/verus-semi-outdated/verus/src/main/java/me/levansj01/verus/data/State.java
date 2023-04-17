package me.levansj01.verus.data.state;

import java.util.function.Supplier;
import me.levansj01.verus.data.bytes.ByteData;
import me.levansj01.verus.data.state.compress.CachedCompress;
import me.levansj01.verus.data.state.lazy.AtomicLazy;
import me.levansj01.verus.data.state.lazy.Lazy;
import me.levansj01.verus.data.state.lazy.ReleaseLazy;
import me.levansj01.verus.data.state.lazy.ResetLazy;
import me.levansj01.verus.data.state.statics.ReleaseStatic;
import me.levansj01.verus.data.state.statics.Static;

public interface State<T> extends Supplier<T>, Releasable {

    default boolean nonNull() {
        boolean var10000;
        if (this.get() != null) {
            var10000 = true;
        } else {
            var10000 = false;
        }

        return var10000;
    }

    static <T extends Releasable> State<T> release(T var0) {
        return new ReleaseStatic(var0);
    }

    static <T> State<T> fast(Supplier<T> var0) {
        return new Lazy(var0);
    }

    static <T> ResetState<T> reset(Supplier<T> var0) {
        return new ResetLazy(var0);
    }

    static <T> State<T> safe(Supplier<T> var0) {
        return new AtomicLazy(var0);
    }

    static State<ByteData> compress(ByteData var0) {
        return release((State)(new CachedCompress(var0)));
    }

    static <T> State<T> of(T var0) {
        return new Static(var0);
    }

    static <T extends Releasable> State<T> release(State<T> var0) {
        return new ReleaseLazy(var0);
    }
}
