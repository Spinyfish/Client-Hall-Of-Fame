package dev.eternal.client.proxy;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.net.Proxy;
import java.net.Socket;

public record MinecraftProxyHandler(Proxy proxy) implements ChannelFactory<OioSocketChannel> {

  @Override
  public OioSocketChannel newChannel() {
    Socket socket = new Socket(proxy());
    return new OioSocketChannel(socket);
  }
}
