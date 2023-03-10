package de.datasecs.hydra.client;

import de.datasecs.hydra.shared.handler.listener.HydraSessionListener;
import de.datasecs.hydra.shared.initializer.HydraChannelInitializer;
import de.datasecs.hydra.shared.protocol.HydraProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with love by DataSecs on 30.11.17.
 */
public class Client {

    public static class Builder {

        private String host;

        private int port;

        private boolean connectAfterSetup = true;

        private int workerThreads = 2;

        private Map<ChannelOption, Object> options = new HashMap<>();

        private Map<AttributeKey, Object> attributeKeys = new HashMap<>();

        private boolean useEpoll;

        private HydraProtocol protocol;

        public Builder(String host, int port, HydraProtocol protocol) {
            this.host = host;
            this.port = port;
            this.protocol = protocol;
        }

        /**
         * This attribute determines whether the client connects instantly or just when the 'connect()' method
         * from {@link HydraClient} is called. The standard value is true, so the standard setting makes the client
         * connect instantly after setup.
         *
         * @param connectAfterSetup determines whether the client connects instantly or just when 'connect()' method is called.
         */
        public Builder connectAfterSetup(boolean connectAfterSetup) {
            this.connectAfterSetup = connectAfterSetup;
            return this;
        }

        /**
         * Sets the number of worker threads for the client.
         * A worker thread performs non-blocking operations for one or more channels.
         * The standard amount is set to 2.
         *
         * @param workerThreads the amount of worker threads for the client
         */
        public Builder workerThreads(int workerThreads) {
            this.workerThreads = workerThreads;
            return this;
        }

        /**
         * Adds a specific option to the client. These options include a lot of possibilities.
         *
         * @param channelOption the desired channel option
         * @param value the value that is supposed to be set for the desired channel option
         *
         * @see <a href="https://netty.io/4.1/api/io/netty/channel/ChannelOption.html">Channel options</a>
         */
        public <T> Builder option(ChannelOption<T> channelOption, T value) {
            options.put(channelOption, value);
            return this;
        }

        /**
         * Adds a specific attribute to the client. The attributes are saved in an attribute map by Netty.
         *
         * @param attributeKey the attribute key that is supposed to be stored in the map.
         * @param value the value that is supposed to be mapped to the given attribute key.
         */
        public <T> Builder attribute(AttributeKey<T> attributeKey, T value) {
            attributeKeys.put(attributeKey, value);
            return this;
        }

        /**
         * Basically epoll decides whether it's a unix based system netty is operating on, or not.
         * This method gives the possibility to allow the usage of epoll, if it's available.
         *
         * @param useEpoll sets whether epoll should be used or not
         */
        public Builder useEpoll(boolean useEpoll) {
            this.useEpoll = useEpoll;
            return this;
        }

        /**
         * This method adds a session listener to the client. The session listener has 2 methods.
         * The first one is 'onConnected', which gets triggered when the client is successfully connected to the aimed server.
         * The second on is 'onDisconnected', which gets fired when the client is disconnected from the server it was
         * connected to.
         *
         * @param sessionListener this method takes an instance of the session listener interface HydraSessionListener.
         */
        public Builder addSessionListener(HydraSessionListener sessionListener) {
            protocol.addSessionListener(sessionListener);
            return this;
        }

        /**
         * Builds the final client. Returns an instance of type HydraClient, not of type Client, as HydraClient includes
         * all the useful methods for users.
         *
         * @return Returns an instance of HydraClient that can e.g. be used to get the session created for the client and server.
         */
        public HydraClient build() {
            return setUpClient();
        }

        private HydraClient setUpClient() {
            boolean epoll = useEpoll && Epoll.isAvailable();
            EventLoopGroup workerGroup = epoll ? new EpollEventLoopGroup(workerThreads) : new NioEventLoopGroup(workerThreads);

            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(epoll ? EpollSocketChannel.class : NioSocketChannel.class)
                    .remoteAddress(host, port);

            options.forEach(bootstrap::option);
            attributeKeys.forEach(bootstrap::attr);

            Channel channel = null;
            try {
                if (connectAfterSetup) {
                    bootstrap.handler(new HydraChannelInitializer(protocol, false));
                    channel = bootstrap.connect().sync().channel();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return connectAfterSetup ? new HydraClient(channel, protocol, workerGroup) : new HydraClient(protocol, workerGroup, bootstrap);
        }
    }
}