package de.datasecs.hydra.client;

import de.datasecs.hydra.shared.handler.HydraSession;
import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.initializer.HydraChannelInitializer;
import de.datasecs.hydra.shared.protocol.HydraProtocol;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import java.io.Serializable;
import java.net.SocketAddress;

/**
 * Created with love by DataSecs on 29.09.2017.
 * <br>
 * For an instruction about setup visit in the Hydra wiki the article
 * <a href="https://github.com/DataSecs/Hydra/wiki/Client-setup">client setup</a>.
 * <br>
 * For an example of how to work with the client, visit
 * <a href="https://github.com/DataSecs/Hydra/tree/master/client/src/test/java/client">client example</a>.
 */
public class HydraClient {

    private Channel channel;

    private HydraProtocol protocol;

    private EventLoopGroup workerGroup;

    private Session clientSession;

    private Bootstrap bootstrap;

    public HydraClient(Channel channel, HydraProtocol protocol, EventLoopGroup workerGroup) {
        this.channel = channel;
        this.protocol = protocol;
        this.workerGroup = workerGroup;
        clientSession = protocol.getClientSession();
    }

    public HydraClient(HydraProtocol protocol, EventLoopGroup workerGroup, Bootstrap bootstrap) {
        this.protocol = protocol;
        this.workerGroup = workerGroup;
        this.bootstrap = bootstrap;
    }

    /**
     * In case that the attribute 'connectAfterSetup' from {@link Client} is set to false via the corresponding method,
     * the 'connect()' method can be invoked in order to connect the client to the server not instantly after setup,
     * but at the desired moment.
     */
    public void connect() {
        if (channel != null) {
            throw new IllegalStateException("Client is already connected!");
        }

        bootstrap.handler(new HydraChannelInitializer(protocol, false));
        try {
            channel = bootstrap.connect().sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientSession = protocol.getClientSession();
    }

    /**
     * Closes the channel of the client, so that the session is closed and can't be reused.
     * This method also shuts down the workerGroup, as it's not needed anymore, when the session is closed.
     */
    public void close() {
        checkChannel();
        channel.close();
        workerGroup.shutdownGracefully();
    }

    /**
     * Returns whether the channel is connected and writable.
     *
     * @return whether channel is connected.
     */
    public boolean isConnected() {
        checkChannel();
        return channel.isWritable();
    }

    /**
     * Sends a packet to the opponent that is connected with this session.
     *
     * @param packet the packet that is supposed to be send to the opponent of the session.
     */
    public void send(Packet packet) {
        checkChannel();
        clientSession.send(packet);
    }

    /**
     * Sends a packet to the opponent that is connected with this session. With the difference that the param not is a
     * packet. The packet is created internally and then send to the opponent, so the user doesn't have to bother with
     * the packet creation. Therefore the object that is passed to the method has to be serializable.
     * See {@link de.datasecs.hydra.shared.protocol.packets.StandardPacket} for the structure of the standard packet.
     *
     * @param object the object that is supposed to be send to the opponent of the session.
     */
    public <T extends Serializable> void send(T object) {
        checkChannel();
        clientSession.send(object);
    }

    /**
     * Returns the channel (a connection/pipeline) that was created for the server. The channel allows a lot of functionality.
     * The channel provides information about the channel configuration, the channel state, the channel pipeline and much
     * more. The user is not required to use the channel for "casual" use. This method is supposed to allow in-depth work.
     *
     * @return the channel that is created for the server.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Returns the worker group that handles I/O operations and allows to register channels. The worker group handles
     * traffic of created connections.
     *
     * @return the worker group that handles the I/O operations (traffic).
     */
    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    /**
     * Returns the remote address the client is connected to. The client is not bound to a local address.
     * It just has a remote address that it's connected to.
     *
     * @return the remote address the client is connected to.
     */
    public SocketAddress getRemoteAddress() {
        checkChannel();
        return channel.remoteAddress();
    }

    /**
     * Returns the local address of the client.
     * The local address is the address the socket is bound to. In this case the client is bound to a local address.
     *
     * @return the local address of the client.
     */
    public SocketAddress getLocalAddress() {
        checkChannel();
        return channel.localAddress();
    }

    /**
     * Returns the session that is created, when the client connects with the server.
     * See {@link HydraSession} for more information about what a session is.
     *
     * @return the session created for client and server.
     */
    public Session getSession() {
        return clientSession;
    }

    /**
     * Simple check whether the channel is null. If the channel is null, an exception is thrown.
     */
    private void checkChannel() {
        if (channel == null) {
            throw new IllegalStateException("Client is not connected!");
        }
    }
}