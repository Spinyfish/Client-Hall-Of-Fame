/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.sctp.oio;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioMessageChannel;
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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OioSctpChannel
extends AbstractOioMessageChannel
implements SctpChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSctpChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final String EXPECTED_TYPE = " (expected: " + StringUtil.simpleClassName(SctpMessage.class) + ')';
    private final com.sun.nio.sctp.SctpChannel ch;
    private final SctpChannelConfig config;
    private final Selector readSelector;
    private final Selector writeSelector;
    private final Selector connectSelector;
    private final NotificationHandler<?> notificationHandler;
    private RecvByteBufAllocator.Handle allocHandle;

    private static com.sun.nio.sctp.SctpChannel openChannel() {
        try {
            return com.sun.nio.sctp.SctpChannel.open();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a sctp channel.", e);
        }
    }

    public OioSctpChannel() {
        this(OioSctpChannel.openChannel());
    }

    public OioSctpChannel(com.sun.nio.sctp.SctpChannel ch) {
        this(null, ch);
    }

    public OioSctpChannel(Channel parent, com.sun.nio.sctp.SctpChannel ch) {
        super(parent);
        this.ch = ch;
        boolean success = false;
        try {
            ch.configureBlocking(false);
            this.readSelector = Selector.open();
            this.writeSelector = Selector.open();
            this.connectSelector = Selector.open();
            ch.register(this.readSelector, 1);
            ch.register(this.writeSelector, 4);
            ch.register(this.connectSelector, 8);
            this.config = new OioSctpChannelConfig(this, ch);
            this.notificationHandler = new SctpNotificationHandler(this);
            success = true;
        }
        catch (Exception e) {
            throw new ChannelException("failed to initialize a sctp channel", e);
        }
        finally {
            if (!success) {
                try {
                    ch.close();
                }
                catch (IOException e) {
                    logger.warn("Failed to close a sctp channel.", e);
                }
            }
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
    public SctpChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return this.ch.isOpen();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int doReadMessages(List<Object> msgs) throws Exception {
        boolean keysSelected;
        if (!this.readSelector.isOpen()) {
            return 0;
        }
        int readMessages = 0;
        int selectedKeys = this.readSelector.select(1000L);
        boolean bl = keysSelected = selectedKeys > 0;
        if (!keysSelected) {
            return readMessages;
        }
        Set<SelectionKey> reableKeys = this.readSelector.selectedKeys();
        try {
            for (SelectionKey ignored : reableKeys) {
                RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
                if (allocHandle == null) {
                    this.allocHandle = allocHandle = this.config().getRecvByteBufAllocator().newHandle();
                }
                ByteBuf buffer = allocHandle.allocate(this.config().getAllocator());
                boolean free = true;
                try {
                    ByteBuffer data = buffer.nioBuffer(buffer.writerIndex(), buffer.writableBytes());
                    MessageInfo messageInfo = this.ch.receive(data, null, this.notificationHandler);
                    if (messageInfo == null) {
                        int n = readMessages;
                        return n;
                    }
                    data.flip();
                    msgs.add(new SctpMessage(messageInfo, buffer.writerIndex(buffer.writerIndex() + data.remaining())));
                    free = false;
                    ++readMessages;
                }
                catch (Throwable cause) {
                    PlatformDependent.throwException(cause);
                }
                finally {
                    int bytesRead = buffer.readableBytes();
                    allocHandle.record(bytesRead);
                    if (!free) continue;
                    buffer.release();
                }
            }
        }
        finally {
            reableKeys.clear();
        }
        return readMessages;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        if (!this.writeSelector.isOpen()) {
            return;
        }
        int size = in.size();
        int selectedKeys = this.writeSelector.select(1000L);
        if (selectedKeys > 0) {
            Set<SelectionKey> writableKeys = this.writeSelector.selectedKeys();
            if (writableKeys.isEmpty()) {
                return;
            }
            Iterator<SelectionKey> writableKeysIt = writableKeys.iterator();
            int written = 0;
            do {
                ByteBuffer nioData;
                if (written == size) {
                    return;
                }
                writableKeysIt.next();
                writableKeysIt.remove();
                SctpMessage packet = (SctpMessage)in.current();
                if (packet == null) {
                    return;
                }
                ByteBuf data = packet.content();
                int dataLen = data.readableBytes();
                if (data.nioBufferCount() != -1) {
                    nioData = data.nioBuffer();
                } else {
                    nioData = ByteBuffer.allocate(dataLen);
                    data.getBytes(data.readerIndex(), nioData);
                    nioData.flip();
                }
                MessageInfo mi = MessageInfo.createOutgoing(this.association(), null, packet.streamIdentifier());
                mi.payloadProtocolID(packet.protocolIdentifier());
                mi.streamNumber(packet.streamIdentifier());
                this.ch.send(nioData, mi);
                ++written;
                in.remove();
            } while (writableKeysIt.hasNext());
            return;
        }
    }

    @Override
    protected Object filterOutboundMessage(Object msg) throws Exception {
        if (msg instanceof SctpMessage) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPE);
    }

    @Override
    public Association association() {
        try {
            return this.ch.association();
        }
        catch (IOException ignored) {
            return null;
        }
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && this.association() != null;
    }

    @Override
    protected SocketAddress localAddress0() {
        try {
            Iterator<SocketAddress> i = this.ch.getAllLocalAddresses().iterator();
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
    public Set<InetSocketAddress> allLocalAddresses() {
        try {
            Set<SocketAddress> allLocalAddresses = this.ch.getAllLocalAddresses();
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
    protected SocketAddress remoteAddress0() {
        try {
            Iterator<SocketAddress> i = this.ch.getRemoteAddresses().iterator();
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
    public Set<InetSocketAddress> allRemoteAddresses() {
        try {
            Set<SocketAddress> allLocalAddresses = this.ch.getRemoteAddresses();
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
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.ch.bind(localAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.ch.bind(localAddress);
        }
        boolean success = false;
        try {
            this.ch.connect(remoteAddress);
            boolean finishConnect = false;
            while (!finishConnect) {
                if (this.connectSelector.select(1000L) < 0) continue;
                Set<SelectionKey> selectionKeys = this.connectSelector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    if (!key.isConnectable()) continue;
                    selectionKeys.clear();
                    finishConnect = true;
                    break;
                }
                selectionKeys.clear();
            }
            success = this.ch.finishConnect();
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        OioSctpChannel.closeSelector("read", this.readSelector);
        OioSctpChannel.closeSelector("write", this.writeSelector);
        OioSctpChannel.closeSelector("connect", this.connectSelector);
        this.ch.close();
    }

    private static void closeSelector(String selectorName, Selector selector) {
        try {
            selector.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close a " + selectorName + " selector.", e);
        }
    }

    @Override
    public ChannelFuture bindAddress(InetAddress localAddress) {
        return this.bindAddress(localAddress, this.newPromise());
    }

    @Override
    public ChannelFuture bindAddress(final InetAddress localAddress, final ChannelPromise promise) {
        if (this.eventLoop().inEventLoop()) {
            try {
                this.ch.bindAddress(localAddress);
                promise.setSuccess();
            }
            catch (Throwable t) {
                promise.setFailure(t);
            }
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    OioSctpChannel.this.bindAddress(localAddress, promise);
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
                this.ch.unbindAddress(localAddress);
                promise.setSuccess();
            }
            catch (Throwable t) {
                promise.setFailure(t);
            }
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    OioSctpChannel.this.unbindAddress(localAddress, promise);
                }
            });
        }
        return promise;
    }

    private final class OioSctpChannelConfig
    extends DefaultSctpChannelConfig {
        private OioSctpChannelConfig(OioSctpChannel channel, com.sun.nio.sctp.SctpChannel javaChannel) {
            super(channel, javaChannel);
        }

        @Override
        protected void autoReadCleared() {
            OioSctpChannel.this.setReadPending(false);
        }
    }
}

