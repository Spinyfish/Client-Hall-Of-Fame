/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.AbstractNioByteChannel;
import java.util.IdentityHashMap;
import java.util.Map;

public class DefaultChannelConfig
implements ChannelConfig {
    private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR = AdaptiveRecvByteBufAllocator.DEFAULT;
    private static final MessageSizeEstimator DEFAULT_MSG_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    protected final Channel channel;
    private volatile ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
    private volatile RecvByteBufAllocator rcvBufAllocator = DEFAULT_RCVBUF_ALLOCATOR;
    private volatile MessageSizeEstimator msgSizeEstimator = DEFAULT_MSG_SIZE_ESTIMATOR;
    private volatile int connectTimeoutMillis = 30000;
    private volatile int maxMessagesPerRead;
    private volatile int writeSpinCount = 16;
    private volatile boolean autoRead = true;
    private volatile boolean autoClose = true;
    private volatile int writeBufferHighWaterMark = 65536;
    private volatile int writeBufferLowWaterMark = 32768;

    public DefaultChannelConfig(Channel channel) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        this.channel = channel;
        this.maxMessagesPerRead = channel instanceof ServerChannel || channel instanceof AbstractNioByteChannel ? 16 : 1;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(null, ChannelOption.CONNECT_TIMEOUT_MILLIS, ChannelOption.MAX_MESSAGES_PER_READ, ChannelOption.WRITE_SPIN_COUNT, ChannelOption.ALLOCATOR, ChannelOption.AUTO_READ, ChannelOption.AUTO_CLOSE, ChannelOption.RCVBUF_ALLOCATOR, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, ChannelOption.MESSAGE_SIZE_ESTIMATOR);
    }

    protected Map<ChannelOption<?>, Object> getOptions(Map<ChannelOption<?>, Object> result, ChannelOption<?> ... options) {
        if (result == null) {
            result = new IdentityHashMap();
        }
        for (ChannelOption<?> o : options) {
            result.put(o, this.getOption(o));
        }
        return result;
    }

    @Override
    public boolean setOptions(Map<ChannelOption<?>, ?> options) {
        if (options == null) {
            throw new NullPointerException("options");
        }
        boolean setAllOptions = true;
        for (Map.Entry<ChannelOption<?>, ?> e : options.entrySet()) {
            if (this.setOption(e.getKey(), e.getValue())) continue;
            setAllOptions = false;
        }
        return setAllOptions;
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == null) {
            throw new NullPointerException("option");
        }
        if (option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
            return (T)Integer.valueOf(this.getConnectTimeoutMillis());
        }
        if (option == ChannelOption.MAX_MESSAGES_PER_READ) {
            return (T)Integer.valueOf(this.getMaxMessagesPerRead());
        }
        if (option == ChannelOption.WRITE_SPIN_COUNT) {
            return (T)Integer.valueOf(this.getWriteSpinCount());
        }
        if (option == ChannelOption.ALLOCATOR) {
            return (T)this.getAllocator();
        }
        if (option == ChannelOption.RCVBUF_ALLOCATOR) {
            return (T)this.getRecvByteBufAllocator();
        }
        if (option == ChannelOption.AUTO_READ) {
            return (T)Boolean.valueOf(this.isAutoRead());
        }
        if (option == ChannelOption.AUTO_CLOSE) {
            return (T)Boolean.valueOf(this.isAutoClose());
        }
        if (option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
            return (T)Integer.valueOf(this.getWriteBufferHighWaterMark());
        }
        if (option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
            return (T)Integer.valueOf(this.getWriteBufferLowWaterMark());
        }
        if (option == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
            return (T)this.getMessageSizeEstimator();
        }
        return null;
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
            this.setConnectTimeoutMillis((Integer)value);
        } else if (option == ChannelOption.MAX_MESSAGES_PER_READ) {
            this.setMaxMessagesPerRead((Integer)value);
        } else if (option == ChannelOption.WRITE_SPIN_COUNT) {
            this.setWriteSpinCount((Integer)value);
        } else if (option == ChannelOption.ALLOCATOR) {
            this.setAllocator((ByteBufAllocator)value);
        } else if (option == ChannelOption.RCVBUF_ALLOCATOR) {
            this.setRecvByteBufAllocator((RecvByteBufAllocator)value);
        } else if (option == ChannelOption.AUTO_READ) {
            this.setAutoRead((Boolean)value);
        } else if (option == ChannelOption.AUTO_CLOSE) {
            this.setAutoClose((Boolean)value);
        } else if (option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
            this.setWriteBufferHighWaterMark((Integer)value);
        } else if (option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
            this.setWriteBufferLowWaterMark((Integer)value);
        } else if (option == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
            this.setMessageSizeEstimator((MessageSizeEstimator)value);
        } else {
            return false;
        }
        return true;
    }

    protected <T> void validate(ChannelOption<T> option, T value) {
        if (option == null) {
            throw new NullPointerException("option");
        }
        option.validate(value);
    }

    @Override
    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    @Override
    public ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        if (connectTimeoutMillis < 0) {
            throw new IllegalArgumentException(String.format("connectTimeoutMillis: %d (expected: >= 0)", connectTimeoutMillis));
        }
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    @Override
    public int getMaxMessagesPerRead() {
        return this.maxMessagesPerRead;
    }

    @Override
    public ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        if (maxMessagesPerRead <= 0) {
            throw new IllegalArgumentException("maxMessagesPerRead: " + maxMessagesPerRead + " (expected: > 0)");
        }
        this.maxMessagesPerRead = maxMessagesPerRead;
        return this;
    }

    @Override
    public int getWriteSpinCount() {
        return this.writeSpinCount;
    }

    @Override
    public ChannelConfig setWriteSpinCount(int writeSpinCount) {
        if (writeSpinCount <= 0) {
            throw new IllegalArgumentException("writeSpinCount must be a positive integer.");
        }
        this.writeSpinCount = writeSpinCount;
        return this;
    }

    @Override
    public ByteBufAllocator getAllocator() {
        return this.allocator;
    }

    @Override
    public ChannelConfig setAllocator(ByteBufAllocator allocator) {
        if (allocator == null) {
            throw new NullPointerException("allocator");
        }
        this.allocator = allocator;
        return this;
    }

    @Override
    public RecvByteBufAllocator getRecvByteBufAllocator() {
        return this.rcvBufAllocator;
    }

    @Override
    public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        if (allocator == null) {
            throw new NullPointerException("allocator");
        }
        this.rcvBufAllocator = allocator;
        return this;
    }

    @Override
    public boolean isAutoRead() {
        return this.autoRead;
    }

    @Override
    public ChannelConfig setAutoRead(boolean autoRead) {
        boolean oldAutoRead = this.autoRead;
        this.autoRead = autoRead;
        if (autoRead && !oldAutoRead) {
            this.channel.read();
        } else if (!autoRead && oldAutoRead) {
            this.autoReadCleared();
        }
        return this;
    }

    protected void autoReadCleared() {
    }

    @Override
    public boolean isAutoClose() {
        return this.autoClose;
    }

    @Override
    public ChannelConfig setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    @Override
    public int getWriteBufferHighWaterMark() {
        return this.writeBufferHighWaterMark;
    }

    @Override
    public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        if (writeBufferHighWaterMark < this.getWriteBufferLowWaterMark()) {
            throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + this.getWriteBufferLowWaterMark() + "): " + writeBufferHighWaterMark);
        }
        if (writeBufferHighWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferHighWaterMark must be >= 0");
        }
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
        return this;
    }

    @Override
    public int getWriteBufferLowWaterMark() {
        return this.writeBufferLowWaterMark;
    }

    @Override
    public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        if (writeBufferLowWaterMark > this.getWriteBufferHighWaterMark()) {
            throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + this.getWriteBufferHighWaterMark() + "): " + writeBufferLowWaterMark);
        }
        if (writeBufferLowWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferLowWaterMark must be >= 0");
        }
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
        return this;
    }

    @Override
    public MessageSizeEstimator getMessageSizeEstimator() {
        return this.msgSizeEstimator;
    }

    @Override
    public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        if (estimator == null) {
            throw new NullPointerException("estimator");
        }
        this.msgSizeEstimator = estimator;
        return this;
    }
}

