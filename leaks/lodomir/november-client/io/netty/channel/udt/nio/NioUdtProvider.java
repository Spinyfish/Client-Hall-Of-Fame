/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.barchart.udt.SocketUDT
 *  com.barchart.udt.TypeUDT
 *  com.barchart.udt.nio.ChannelUDT
 *  com.barchart.udt.nio.KindUDT
 *  com.barchart.udt.nio.RendezvousChannelUDT
 *  com.barchart.udt.nio.SelectorProviderUDT
 *  com.barchart.udt.nio.ServerSocketChannelUDT
 *  com.barchart.udt.nio.SocketChannelUDT
 */
package io.netty.channel.udt.nio;

import com.barchart.udt.SocketUDT;
import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.ChannelUDT;
import com.barchart.udt.nio.KindUDT;
import com.barchart.udt.nio.RendezvousChannelUDT;
import com.barchart.udt.nio.SelectorProviderUDT;
import com.barchart.udt.nio.ServerSocketChannelUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtServerChannel;
import io.netty.channel.udt.nio.NioUdtByteAcceptorChannel;
import io.netty.channel.udt.nio.NioUdtByteConnectorChannel;
import io.netty.channel.udt.nio.NioUdtByteRendezvousChannel;
import io.netty.channel.udt.nio.NioUdtMessageAcceptorChannel;
import io.netty.channel.udt.nio.NioUdtMessageConnectorChannel;
import io.netty.channel.udt.nio.NioUdtMessageRendezvousChannel;
import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;

public final class NioUdtProvider<T extends UdtChannel>
implements ChannelFactory<T> {
    public static final ChannelFactory<UdtServerChannel> BYTE_ACCEPTOR = new NioUdtProvider<UdtServerChannel>(TypeUDT.STREAM, KindUDT.ACCEPTOR);
    public static final ChannelFactory<UdtChannel> BYTE_CONNECTOR = new NioUdtProvider<UdtChannel>(TypeUDT.STREAM, KindUDT.CONNECTOR);
    public static final SelectorProvider BYTE_PROVIDER = SelectorProviderUDT.STREAM;
    public static final ChannelFactory<UdtChannel> BYTE_RENDEZVOUS = new NioUdtProvider<UdtChannel>(TypeUDT.STREAM, KindUDT.RENDEZVOUS);
    public static final ChannelFactory<UdtServerChannel> MESSAGE_ACCEPTOR = new NioUdtProvider<UdtServerChannel>(TypeUDT.DATAGRAM, KindUDT.ACCEPTOR);
    public static final ChannelFactory<UdtChannel> MESSAGE_CONNECTOR = new NioUdtProvider<UdtChannel>(TypeUDT.DATAGRAM, KindUDT.CONNECTOR);
    public static final SelectorProvider MESSAGE_PROVIDER = SelectorProviderUDT.DATAGRAM;
    public static final ChannelFactory<UdtChannel> MESSAGE_RENDEZVOUS = new NioUdtProvider<UdtChannel>(TypeUDT.DATAGRAM, KindUDT.RENDEZVOUS);
    private final KindUDT kind;
    private final TypeUDT type;

    public static ChannelUDT channelUDT(Channel channel) {
        if (channel instanceof NioUdtByteAcceptorChannel) {
            return ((NioUdtByteAcceptorChannel)channel).javaChannel();
        }
        if (channel instanceof NioUdtByteConnectorChannel) {
            return ((NioUdtByteConnectorChannel)channel).javaChannel();
        }
        if (channel instanceof NioUdtByteRendezvousChannel) {
            return ((NioUdtByteRendezvousChannel)channel).javaChannel();
        }
        if (channel instanceof NioUdtMessageAcceptorChannel) {
            return ((NioUdtMessageAcceptorChannel)channel).javaChannel();
        }
        if (channel instanceof NioUdtMessageConnectorChannel) {
            return ((NioUdtMessageConnectorChannel)channel).javaChannel();
        }
        if (channel instanceof NioUdtMessageRendezvousChannel) {
            return ((NioUdtMessageRendezvousChannel)channel).javaChannel();
        }
        return null;
    }

    static ServerSocketChannelUDT newAcceptorChannelUDT(TypeUDT type) {
        try {
            return SelectorProviderUDT.from((TypeUDT)type).openServerSocketChannel();
        }
        catch (IOException e) {
            throw new ChannelException("failed to open a server socket channel", e);
        }
    }

    static SocketChannelUDT newConnectorChannelUDT(TypeUDT type) {
        try {
            return SelectorProviderUDT.from((TypeUDT)type).openSocketChannel();
        }
        catch (IOException e) {
            throw new ChannelException("failed to open a socket channel", e);
        }
    }

    static RendezvousChannelUDT newRendezvousChannelUDT(TypeUDT type) {
        try {
            return SelectorProviderUDT.from((TypeUDT)type).openRendezvousChannel();
        }
        catch (IOException e) {
            throw new ChannelException("failed to open a rendezvous channel", e);
        }
    }

    public static SocketUDT socketUDT(Channel channel) {
        ChannelUDT channelUDT = NioUdtProvider.channelUDT(channel);
        if (channelUDT == null) {
            return null;
        }
        return channelUDT.socketUDT();
    }

    private NioUdtProvider(TypeUDT type, KindUDT kind) {
        this.type = type;
        this.kind = kind;
    }

    public KindUDT kind() {
        return this.kind;
    }

    @Override
    public T newChannel() {
        switch (this.kind) {
            case ACCEPTOR: {
                switch (this.type) {
                    case DATAGRAM: {
                        return (T)new NioUdtMessageAcceptorChannel();
                    }
                    case STREAM: {
                        return (T)new NioUdtByteAcceptorChannel();
                    }
                }
                throw new IllegalStateException("wrong type=" + this.type);
            }
            case CONNECTOR: {
                switch (this.type) {
                    case DATAGRAM: {
                        return (T)new NioUdtMessageConnectorChannel();
                    }
                    case STREAM: {
                        return (T)new NioUdtByteConnectorChannel();
                    }
                }
                throw new IllegalStateException("wrong type=" + this.type);
            }
            case RENDEZVOUS: {
                switch (this.type) {
                    case DATAGRAM: {
                        return (T)new NioUdtMessageRendezvousChannel();
                    }
                    case STREAM: {
                        return (T)new NioUdtByteRendezvousChannel();
                    }
                }
                throw new IllegalStateException("wrong type=" + this.type);
            }
        }
        throw new IllegalStateException("wrong kind=" + this.kind);
    }

    public TypeUDT type() {
        return this.type;
    }
}

