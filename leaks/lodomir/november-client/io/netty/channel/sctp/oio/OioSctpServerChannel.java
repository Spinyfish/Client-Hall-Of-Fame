/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.sctp.oio;

import com.sun.nio.sctp.SctpChannel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.sctp.DefaultSctpServerChannelConfig;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.channel.sctp.SctpServerChannelConfig;
import io.netty.channel.sctp.oio.OioSctpChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OioSctpServerChannel
extends AbstractOioMessageChannel
implements SctpServerChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSctpServerChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final com.sun.nio.sctp.SctpServerChannel sch;
    private final SctpServerChannelConfig config;
    private final Selector selector;

    private static com.sun.nio.sctp.SctpServerChannel newServerSocket() {
        try {
            return com.sun.nio.sctp.SctpServerChannel.open();
        }
        catch (IOException e) {
            throw new ChannelException("failed to create a sctp server channel", e);
        }
    }

    public OioSctpServerChannel() {
        this(OioSctpServerChannel.newServerSocket());
    }

    public OioSctpServerChannel(com.sun.nio.sctp.SctpServerChannel sch) {
        super(null);
        if (sch == null) {
            throw new NullPointerException("sctp server channel");
        }
        this.sch = sch;
        boolean success = false;
        try {
            sch.configureBlocking(false);
            this.selector = Selector.open();
            sch.register(this.selector, 16);
            this.config = new OioSctpServerChannelConfig(this, sch);
            success = true;
        }
        catch (Exception e) {
            throw new ChannelException("failed to initialize a sctp server channel", e);
        }
        finally {
            if (!success) {
                try {
                    sch.close();
                }
                catch (IOException e) {
                    logger.warn("Failed to close a sctp server channel.", e);
                }
            }
        }
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public SctpServerChannelConfig config() {
        return this.config;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public boolean isOpen() {
        return this.sch.isOpen();
    }

    @Override
    protected SocketAddress localAddress0() {
        try {
            Iterator<SocketAddress> i = this.sch.getAllLocalAddresses().iterator();
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
            Set<SocketAddress> allLocalAddresses = this.sch.getAllLocalAddresses();
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
    public boolean isActive() {
        return this.isOpen() && this.localAddress0() != null;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.sch.bind(localAddress, this.config.getBacklog());
    }

    @Override
    protected void doClose() throws Exception {
        try {
            this.selector.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close a selector.", e);
        }
        this.sch.close();
    }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        int acceptedChannels;
        block7: {
            if (!this.isActive()) {
                return -1;
            }
            AbstractInterruptibleChannel s = null;
            acceptedChannels = 0;
            try {
                int selectedKeys = this.selector.select(1000L);
                if (selectedKeys > 0) {
                    Iterator<SelectionKey> selectionKeys = this.selector.selectedKeys().iterator();
                    do {
                        SelectionKey key = selectionKeys.next();
                        selectionKeys.remove();
                        if (!key.isAcceptable() || (s = this.sch.accept()) == null) continue;
                        buf.add(new OioSctpChannel(this, (SctpChannel)s));
                        ++acceptedChannels;
                    } while (selectionKeys.hasNext());
                    return acceptedChannels;
                }
            }
            catch (Throwable t) {
                logger.warn("Failed to create a new channel from an accepted sctp channel.", t);
                if (s == null) break block7;
                try {
                    s.close();
                }
                catch (Throwable t2) {
                    logger.warn("Failed to close a sctp channel.", t2);
                }
            }
        }
        return acceptedChannels;
    }

    @Override
    public ChannelFuture bindAddress(InetAddress localAddress) {
        return this.bindAddress(localAddress, this.newPromise());
    }

    @Override
    public ChannelFuture bindAddress(final InetAddress localAddress, final ChannelPromise promise) {
        if (this.eventLoop().inEventLoop()) {
            try {
                this.sch.bindAddress(localAddress);
                promise.setSuccess();
            }
            catch (Throwable t) {
                promise.setFailure(t);
            }
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    OioSctpServerChannel.this.bindAddress(localAddress, promise);
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
                this.sch.unbindAddress(localAddress);
                promise.setSuccess();
            }
            catch (Throwable t) {
                promise.setFailure(t);
            }
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    OioSctpServerChannel.this.unbindAddress(localAddress, promise);
                }
            });
        }
        return promise;
    }

    @Override
    protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object filterOutboundMessage(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    private final class OioSctpServerChannelConfig
    extends DefaultSctpServerChannelConfig {
        private OioSctpServerChannelConfig(OioSctpServerChannel channel, com.sun.nio.sctp.SctpServerChannel javaChannel) {
            super(channel, javaChannel);
        }

        @Override
        protected void autoReadCleared() {
            OioSctpServerChannel.this.setReadPending(false);
        }
    }
}

