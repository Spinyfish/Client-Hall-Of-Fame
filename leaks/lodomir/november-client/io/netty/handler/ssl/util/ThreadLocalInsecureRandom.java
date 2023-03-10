/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl.util;

import io.netty.util.internal.ThreadLocalRandom;
import java.security.SecureRandom;
import java.util.Random;

final class ThreadLocalInsecureRandom
extends SecureRandom {
    private static final long serialVersionUID = -8209473337192526191L;
    private static final SecureRandom INSTANCE = new ThreadLocalInsecureRandom();

    static SecureRandom current() {
        return INSTANCE;
    }

    private ThreadLocalInsecureRandom() {
    }

    @Override
    public String getAlgorithm() {
        return "insecure";
    }

    @Override
    public void setSeed(byte[] seed) {
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void nextBytes(byte[] bytes) {
        ThreadLocalInsecureRandom.random().nextBytes(bytes);
    }

    @Override
    public byte[] generateSeed(int numBytes) {
        byte[] seed = new byte[numBytes];
        ThreadLocalInsecureRandom.random().nextBytes(seed);
        return seed;
    }

    @Override
    public int nextInt() {
        return ThreadLocalInsecureRandom.random().nextInt();
    }

    @Override
    public int nextInt(int n) {
        return ThreadLocalInsecureRandom.random().nextInt(n);
    }

    @Override
    public boolean nextBoolean() {
        return ThreadLocalInsecureRandom.random().nextBoolean();
    }

    @Override
    public long nextLong() {
        return ThreadLocalInsecureRandom.random().nextLong();
    }

    @Override
    public float nextFloat() {
        return ThreadLocalInsecureRandom.random().nextFloat();
    }

    @Override
    public double nextDouble() {
        return ThreadLocalInsecureRandom.random().nextDouble();
    }

    @Override
    public double nextGaussian() {
        return ThreadLocalInsecureRandom.random().nextGaussian();
    }

    private static Random random() {
        return ThreadLocalRandom.current();
    }
}

