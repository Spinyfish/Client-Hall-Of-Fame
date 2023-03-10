/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.rxtx;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface RxtxChannelConfig
extends ChannelConfig {
    public RxtxChannelConfig setBaudrate(int var1);

    public RxtxChannelConfig setStopbits(Stopbits var1);

    public RxtxChannelConfig setDatabits(Databits var1);

    public RxtxChannelConfig setParitybit(Paritybit var1);

    public int getBaudrate();

    public Stopbits getStopbits();

    public Databits getDatabits();

    public Paritybit getParitybit();

    public boolean isDtr();

    public RxtxChannelConfig setDtr(boolean var1);

    public boolean isRts();

    public RxtxChannelConfig setRts(boolean var1);

    public int getWaitTimeMillis();

    public RxtxChannelConfig setWaitTimeMillis(int var1);

    public RxtxChannelConfig setReadTimeout(int var1);

    public int getReadTimeout();

    @Override
    public RxtxChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public RxtxChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public RxtxChannelConfig setWriteSpinCount(int var1);

    @Override
    public RxtxChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public RxtxChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public RxtxChannelConfig setAutoRead(boolean var1);

    @Override
    public RxtxChannelConfig setAutoClose(boolean var1);

    @Override
    public RxtxChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public RxtxChannelConfig setWriteBufferLowWaterMark(int var1);

    @Override
    public RxtxChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

    public static enum Paritybit {
        NONE(0),
        ODD(1),
        EVEN(2),
        MARK(3),
        SPACE(4);

        private final int value;

        private Paritybit(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static Paritybit valueOf(int value) {
            for (Paritybit paritybit : Paritybit.values()) {
                if (paritybit.value != value) continue;
                return paritybit;
            }
            throw new IllegalArgumentException("unknown " + Paritybit.class.getSimpleName() + " value: " + value);
        }
    }

    public static enum Databits {
        DATABITS_5(5),
        DATABITS_6(6),
        DATABITS_7(7),
        DATABITS_8(8);

        private final int value;

        private Databits(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static Databits valueOf(int value) {
            for (Databits databit : Databits.values()) {
                if (databit.value != value) continue;
                return databit;
            }
            throw new IllegalArgumentException("unknown " + Databits.class.getSimpleName() + " value: " + value);
        }
    }

    public static enum Stopbits {
        STOPBITS_1(1),
        STOPBITS_2(2),
        STOPBITS_1_5(3);

        private final int value;

        private Stopbits(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static Stopbits valueOf(int value) {
            for (Stopbits stopbit : Stopbits.values()) {
                if (stopbit.value != value) continue;
                return stopbit;
            }
            throw new IllegalArgumentException("unknown " + Stopbits.class.getSimpleName() + " value: " + value);
        }
    }
}

