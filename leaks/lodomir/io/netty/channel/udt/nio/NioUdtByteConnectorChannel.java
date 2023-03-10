/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.barchart.udt.TypeUDT
 *  com.barchart.udt.nio.ChannelUDT
 *  com.barchart.udt.nio.SocketChannelUDT
 */
package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.ChannelUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.udt.DefaultUdtChannelConfig;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtChannelConfig;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SelectableChannel;

public class NioUdtByteConnectorChannel
extends AbstractNioByteChannel
implements UdtChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioUdtByteConnectorChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final UdtChannelConfig config;

    public NioUdtByteConnectorChannel() {
        this(TypeUDT.STREAM);
    }

    public NioUdtByteConnectorChannel(Channel parent, SocketChannelUDT channelUDT) {
        super(parent, (SelectableChannel)channelUDT);
        try {
            channelUDT.configureBlocking(false);
            switch (channelUDT.socketUDT().status()) {
                case INIT: 
                case OPENED: {
                    this.config = new DefaultUdtChannelConfig(this, (ChannelUDT)channelUDT, true);
                    break;
                }
                default: {
                    this.config = new DefaultUdtChannelConfig(this, (ChannelUDT)channelUDT, false);
                    break;
                }
            }
        }
        catch (Exception e) {
            block7: {
                try {
                    channelUDT.close();
                }
                catch (Exception e2) {
                    if (!logger.isWarnEnabled()) break block7;
                    logger.warn("Failed to close channel.", e2);
                }
            }
            throw new ChannelException("Failed to configure channel.", e);
        }
    }

    public NioUdtByteConnectorChannel(SocketChannelUDT channelUDT) {
        this(null, channelUDT);
    }

    public NioUdtByteConnectorChannel(TypeUDT type) {
        this(NioUdtProvider.newConnectorChannelUDT(type));
    }

    @Override
    public UdtChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.javaChannel().bind(localAddress);
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        this.doBind(localAddress != null ? localAddress : new InetSocketAddress(0));
        boolean success = false;
        try {
            boolean connected = this.javaChannel().connect(remoteAddress);
            if (!connected) {
                this.selectionKey().interestOps(this.selectionKey().interestOps() | 8);
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
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new Error("Provider error: failed to finish connect. Provider library should be upgraded.");
        }
        this.selectionKey().interestOps(this.selectionKey().interestOps() & 0xFFFFFFF7);
    }

    @Override
    protected int doReadBytes(ByteBuf byteBuf) throws Exception {
        return byteBuf.writeBytes((ScatteringByteChannel)this.javaChannel(), byteBuf.writableBytes());
    }

    @Override
    protected int doWriteBytes(ByteBuf byteBuf) throws Exception {
        int expectedWrittenBytes = byteBuf.readableBytes();
        return byteBuf.readBytes((GatheringByteChannel)this.javaChannel(), expectedWrittenBytes);
    }

    @Override
    protected long doWriteFileRegion(FileRegion region) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isActive() {
        SocketChannelUDT channelUDT = this.javaChannel();
        return channelUDT.isOpen() && channelUDT.isConnectFinished();
    }

    protected SocketChannelUDT javaChannel() {
        return (SocketChannelUDT)super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }
}

