package net.minecraft.util;

public class Tuple<A, B> {
    private final A a;
    private final B b;

    public Tuple(final A aIn, final B bIn) {
        this.a = aIn;
        this.b = bIn;
    }

    /**
     * Get the first Object in the Tuple
     */
    public A getFirst() {
        return this.a;
    }

    /**
     * Get the second Object in the Tuple
     */
    public B getSecond() {
        return this.b;
    }
}
