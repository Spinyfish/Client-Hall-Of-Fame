/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.sctp.nio;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.sctp.DefaultSctpChannelConfig;
import io.netty.channel.sctp.SctpChannel;
import io.netty.channel.sctp.SctpChannelConfig;
import io.netty.channel.sctp.SctpMessage;
import io.netty.channel.sctp.SctpNotificationHandler;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NioSctpChannel
extends AbstractNioMessageChannel
implements SctpChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSctpChannel.class);
    private final SctpChannelConfig config;
    private final NotificationHandler<?> notificationHandler;
    private RecvByteBufAllocator.Handle allocHandle;

    private static com.sun.nio.sctp.SctpChannel newSctpChannel() {
        try {
            return com.sun.nio.sctp.SctpChannel.open();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a sctp channel.", e);
        }
    }

    public NioSctpChannel() {
        this(NioSctpChannel.newSctpChannel());
    }

    public NioSctpChannel(com.sun.nio.sctp.SctpChannel sctpChannel) {
        this(null, sctpChannel);
    }

    public NioSctpChannel(Channel parent, com.sun.nio.sctp.SctpChannel sctpChannel) {
        super(parent, sctpChannel, 1);
        try {
            sctpChannel.configureBlocking(false);
            this.config = new NioSctpChannelConfig(this, sctpChannel);
            this.notificationHandler = new SctpNotificationHandler(this);
        }
        catch (IOException e) {
            block4: {
                try {
                    sctpChannel.close();
                }
                catch (IOException e2) {
                    if (!logger.isWarnEnabled()) break block4;
                    logger.warn("Failed to close a partially initialized sctp channel.", e2);
                }
            }
            throw new ChannelException("Failed to enter non-blocking mode.", e);
        }
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public SctpServerChannel parent() {
        return (SctpServerChannel)super.parent();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public Association association() {
        try {
            return this.javaChannel().association();
        }
        catch (IOException ignored) {
            return null;
        }
    }

    @Override
    public Set<InetSocketAddress> allLocalAddresses() {
        try {
            Set<SocketAddress> allLocalAddresses = this.javaChannel().getAllLocalAddresses();
            LinkedHashSet<InetSocketAddress> addresses = new LinkedHashSet<InetSocketAddress>(allLocalAddresses.size());
            for (SocketAddress socketAddress : allLocalAddresses) {
                addresses.add((InetSocketAddress)socketAddress);
            }
            return addresses;
        }
        catch (Throwable ignored) {
            return Collections.emptySet();
        }
    }

    @Override
    public SctpChannelConfig config() {
        return this.config;
    }

    @Override
    public Set<InetSocketAddress> allRemoteAddresses() {
        try {
            Set<SocketAddress> allLocalAddresses = this.javaChannel().getRemoteAddresses();
            HashSet<InetSocketAddress> addresses = new HashSet<InetSocketAddress>(allLocalAddresses.size());
            for (SocketAddress socketAddress : allLocalAddresses) {
                addresses.add((InetSocketAddress)socketAddress);
            }
            return addresses;
        }
        catch (Throwable ignored) {
            return Collections.emptySet();
        }
    }

    @Override
    protected com.sun.nio.sctp.SctpChannel javaChannel() {
        return (com.sun.nio.sctp.SctpChannel)super.javaChannel();
    }

    @Override
    public boolean isActive() {
        com.sun.nio.sctp.SctpChannel ch = this.javaChannel();
        return ch.isOpen() && this.association() != null;
    }

    @Override
    protected SocketAddress localAddress0() {
        try {
            Iterator<SocketAddress> i = this.javaChannel().getAllLocalAddresses().iterator();
            if (i.hasNext()) {
                return i.next();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        try {
            Iterator<SocketAddress> i = this.javaChannel().getRemoteAddresses().iterator();
            if (i.hasNext()) {
                return i.next();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.javaChannel().bind(localAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.javaChannel().bind(localAddress);
        }
        boolean success = false;
        try {
            boolean connected = this.javaChannel().connect(remoteAddress);
            if (!connected) {
                this.selectionKey().interestOps(8);
            }
            success = true;
            boolean bl = connected;
            return bl;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new Error();
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        com.sun.nio.sctp.SctpChannel ch = this.javaChannel();
        RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
        if (allocHandle == null) {
            this.allocHandle = allocHandle = this.config().getRecvByteBufAllocator().newHandle();
        }
        ByteBuf buffer = allocHandle.allocate(this.config().getAllocator());
        boolean free = true;
        try {
            ByteBuffer data = buffer.internalNioBuffer(buffer.writerIndex(), buffer.writableBytes());
            int pos = data.position();
            MessageInfo messageInfo = ch.receive(data, null, this.notificationHandler);
            if (messageInfo == null) {
                int n = 0;
                return n;
            }
            buf.add(new SctpMessage(messageInfo, buffer.writerIndex(buffer.writerIndex() + data.position() - pos)));
            free = false;
            int n = 1;
            return n;
        }
        catch (Throwable cause) {
            PlatformDependent.throwException(cause);
            int n = -1;
            return n;
        }
        finally {
            int bytesRead = buffer.readableBytes();
            allocHandle.record(bytesRead);
            if (free) {
                buffer.release();
            }
        }
    }

    @Override
    protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
        ByteBuffer nioData;
        boolean needsCopy;
        SctpMessage packet = (SctpMessage)msg;
        ByteBuf data = packet.content();
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        ByteBufAllocator alloc = this.alloc();
        boolean bl = needsCopy = data.nioBufferCount() != 1;
        if (!needsCopy && !data.isDirect() && alloc.isDirectBufferPooled()) {
            needsCopy = true;
        }
        if (!needsCopy) {
            nioData = data.nioBuffer();
        } else {
            data = alloc.directBuffer(dataLen).writeBytes(data);
            nioData = data.nioBuffer();
        }
        MessageInfo mi = MessageInfo.createOutgoing(this.association(), null, packet.streamIdentifier());
        mi.payloadProtocolID(packet.protocolIdentifier());
        mi.streamNumber(packet.streamIdentifier());
        int writtenBytes = this.javaChannel().send(nioData, mi);
        return writtenBytes > 0;
    }

    @Override
    protected final Object filterOutboundMessage(Object msg) throws Exception {
        if (msg instanceof SctpMessage) {
            SctpMessage m = (SctpMessage)msg;
            ByteBuf buf = m.content();
            if (buf.isDirect() && buf.nioBufferCount() == 1) {
                return m;
            }
            return new SctpMessage(m.protocolIdentifier(), m.streamIdentifier(), this.newDirectBuffer(m, buf));
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + " (expected: " + StringUtil.simpleClassName(SctpMessage.class));
    }

    @Override
    public ChannelFuture bindAddress(InetAddress localAddress) {
        return this.bindAddress(localAddress, this.newPromise());
    }

    @Override
    public ChannelFuture bindAddress(final InetAddress localAddress, final ChannelPromise promise) {
        if (this.eventLoop().inEventLoop()) {
            try {
                this.javaChannel().bindAddress(localAddress);
                promise.setSuccess();
            }
            catch (Throwable t) {
                promise.setFailure(t);
            }
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    NioSctpChannel.this.bindAddress(localAddress, promise);
                }
            });
        }
        return promise;
    }

    @Override
    public ChannelFuture unbindAddress(InetAddress localAddress) {
        return this.unbindAddress(localAddress, this.newPromise());
    }

    @Override
    public ChannelFuture unbindAddress(final InetAddress localAddress, final ChannelPromise promise) {
        if (this.eventLoop().inEventLoop()) {
            try {
                this.javaChannel().unbindAddress(localAddress);
                promise.setSuccess();
            }
            catch (Throwable t) {
                promise.setFailure(t);
            }
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    NioSctpChannel.this.unbindAddress(localAddress, promise);
                }
            });
        }
        return promise;
    }

    private final class NioSctpChannelConfig
    extends DefaultSctpChannelConfig {
        private NioSctpChannelConfig(NioSctpChannel channel, com.sun.nio.sctp.SctpChannel javaChannel) {
            super(channel, javaChannel);
        }

        @Override
        protected void autoReadCleared() {
            NioSctpChannel.this.setReadPending(false);
        }
    }
}

