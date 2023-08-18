package me.jinthium.straight.impl.utils.tuples.immutable;

import me.jinthium.straight.impl.utils.tuples.Unit;

import java.util.function.Consumer;
import java.util.function.Function;

public final class ImmutableUnit<A> extends Unit<A> {
    private final A a;

    ImmutableUnit(A a) {
        this.a = a;
    }

    public static <A> ImmutableUnit<A> of(A a) {
        return new ImmutableUnit<>(a);
    }

    @Override
    public A get() {
        return a;
    }

    @Override
    public <R> R apply(Function<? super A, ? extends R> func) {
        return func.apply(a);
    }

    @Override
    public void use(Consumer<? super A> func) {
        func.accept(a);
    }
}