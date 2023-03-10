/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.util.Map;

public final class EpollSocketChannelConfig
extends DefaultChannelConfig
implements SocketChannelConfig {
    private final EpollSocketChannel channel;
    private volatile boolean allowHalfClosure;

    EpollSocketChannelConfig(EpollSocketChannel channel) {
        super(channel);
        this.channel = channel;
        if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
            this.setTcpNoDelay(true);
        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE, EpollChannelOption.TCP_CORK, EpollChannelOption.TCP_KEEPCNT, EpollChannelOption.TCP_KEEPIDLE, EpollChannelOption.TCP_KEEPINTVL);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.SO_RCVBUF) {
            return (T)Integer.valueOf(this.getReceiveBufferSize());
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return (T)Integer.valueOf(this.getSendBufferSize());
        }
        if (option == ChannelOption.TCP_NODELAY) {
            return (T)Boolean.valueOf(this.isTcpNoDelay());
        }
        if (option == ChannelOption.SO_KEEPALIVE) {
            return (T)Boolean.valueOf(this.isKeepAlive());
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            return (T)Boolean.valueOf(this.isReuseAddress());
        }
        if (option == ChannelOption.SO_LINGER) {
            return (T)Integer.valueOf(this.getSoLinger());
        }
        if (option == ChannelOption.IP_TOS) {
            return (T)Integer.valueOf(this.getTrafficClass());
        }
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            return (T)Boolean.valueOf(this.isAllowHalfClosure());
        }
        if (option == EpollChannelOption.TCP_CORK) {
            return (T)Boolean.valueOf(this.isTcpCork());
        }
        if (option == EpollChannelOption.TCP_KEEPIDLE) {
            return (T)Integer.valueOf(this.getTcpKeepIdle());
        }
        if (option == EpollChannelOption.TCP_KEEPINTVL) {
            return (T)Integer.valueOf(this.getTcpKeepIntvl());
        }
        if (option == EpollChannelOption.TCP_KEEPCNT) {
            return (T)Integer.valueOf(this.getTcpKeepCnt());
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer)value);
        } else if (option == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((Integer)value);
        } else if (option == ChannelOption.TCP_NODELAY) {
            this.setTcpNoDelay((Boolean)value);
        } else if (option == ChannelOption.SO_KEEPALIVE) {
            this.setKeepAlive((Boolean)value);
        } else if (option == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((Boolean)value);
        } else if (option == ChannelOption.SO_LINGER) {
            this.setSoLinger((Integer)value);
        } else if (option == ChannelOption.IP_TOS) {
            this.setTrafficClass((Integer)value);
        } else if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            this.setAllowHalfClosure((Boolean)value);
        } else if (option == EpollChannelOption.TCP_CORK) {
            this.setTcpCork((Boolean)value);
        } else if (option == EpollChannelOption.TCP_KEEPIDLE) {
            this.setTcpKeepIdle((Integer)value);
        } else if (option == EpollChannelOption.TCP_KEEPCNT) {
            this.setTcpKeepCntl((Integer)value);
        } else if (option == EpollChannelOption.TCP_KEEPINTVL) {
            this.setTcpKeepIntvl((Integer)value);
        } else {
            return super.setOption(option, value);
        }
        return true;
    }

    @Override
    public int getReceiveBufferSize() {
        return Native.getReceiveBufferSize(this.channel.fd);
    }

    @Override
    public int getSendBufferSize() {
        return Native.getSendBufferSize(this.channel.fd);
    }

    @Override
    public int getSoLinger() {
        return Native.getSoLinger(this.channel.fd);
    }

    @Override
    public int getTrafficClass() {
        return Native.getTrafficClass(this.channel.fd);
    }

    @Override
    public boolean isKeepAlive() {
        return Native.isKeepAlive(this.channel.fd) == 1;
    }

    @Override
    public boolean isReuseAddress() {
        return Native.isReuseAddress(this.channel.fd) == 1;
    }

    @Override
    public boolean isTcpNoDelay() {
        return Native.isTcpNoDelay(this.channel.fd) == 1;
    }

    public boolean isTcpCork() {
        return Native.isTcpCork(this.channel.fd) == 1;
    }

    public int getTcpKeepIdle() {
        return Native.getTcpKeepIdle(this.channel.fd);
    }

    public int getTcpKeepIntvl() {
        return Native.getTcpKeepIntvl(this.channel.fd);
    }

    public int getTcpKeepCnt() {
        return Native.getTcpKeepCnt(this.channel.fd);
    }

    @Override
    public EpollSocketChannelConfig setKeepAlive(boolean keepAlive) {
        Native.setKeepAlive(this.channel.fd, keepAlive ? 1 : 0);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public EpollSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        Native.setReceiveBufferSize(this.channel.fd, receiveBufferSize);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setReuseAddress(boolean reuseAddress) {
        Native.setReuseAddress(this.channel.fd, reuseAddress ? 1 : 0);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setSendBufferSize(int sendBufferSize) {
        Native.setSendBufferSize(this.channel.fd, sendBufferSize);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setSoLinger(int soLinger) {
        Native.setSoLinger(this.channel.fd, soLinger);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
        Native.setTcpNoDelay(this.channel.fd, tcpNoDelay ? 1 : 0);
        return this;
    }

    public EpollSocketChannelConfig setTcpCork(boolean tcpCork) {
        Native.setTcpCork(this.channel.fd, tcpCork ? 1 : 0);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setTrafficClass(int trafficClass) {
        Native.setTrafficClass(this.channel.fd, trafficClass);
        return this;
    }

    public EpollSocketChannelConfig setTcpKeepIdle(int seconds) {
        Native.setTcpKeepIdle(this.channel.fd, seconds);
        return this;
    }

    public EpollSocketChannelConfig setTcpKeepIntvl(int seconds) {
        Native.setTcpKeepIntvl(this.channel.fd, seconds);
        return this;
    }

    public EpollSocketChannelConfig setTcpKeepCntl(int probes) {
        Native.setTcpKeepCnt(this.channel.fd, probes);
        return this;
    }

    @Override
    public boolean isAllowHalfClosure() {
        return this.allowHalfClosure;
    }

    @Override
    public EpollSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
        return this;
    }

    @Override
    public EpollSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    @Override
    protected void autoReadCleared() {
        this.channel.clearEpollIn();
    }
}

