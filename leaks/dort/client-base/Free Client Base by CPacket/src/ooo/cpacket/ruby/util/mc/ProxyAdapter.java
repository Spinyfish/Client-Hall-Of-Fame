package ooo.cpacket.ruby.util.mc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;

import io.netty.channel.socket.oio.OioSocketChannel;

public class ProxyAdapter implements io.netty.bootstrap.ChannelFactory<io.netty.channel.socket.oio.OioSocketChannel> {

	private Proxy proxy;

	public ProxyAdapter(Proxy proxy) {
		this.proxy = proxy;
		XD2 = true;
	}

	boolean XD2;

	@Override
	public OioSocketChannel newChannel() {
		if (this.proxy.address() != null && ((InetSocketAddress)this.proxy.address()).getPort() == 8080) {
			Socket rolf = new Socket(this.proxy);
			return new OioSocketChannel(rolf);
		}
		Socket socks = new Socket(this.proxy);
		Class clazzSocks = socks.getClass();
		Method setSockVersion = null;
		Field sockImplField = null;
		SocketImpl socksimpl = null;
		try {
			sockImplField = clazzSocks.getDeclaredField("impl");
			sockImplField.setAccessible(true);
			socksimpl = (SocketImpl) sockImplField.get(socks);
			if (socksimpl.getClass().getSimpleName().equalsIgnoreCase("PlainSocketImpl")) {
				return new OioSocketChannel(socks);
			}
			Class clazzSocksImpl = socksimpl.getClass();
			setSockVersion = clazzSocksImpl.getDeclaredMethod("setV4");
			setSockVersion.setAccessible(true);
			if (null != setSockVersion) {
				setSockVersion.invoke(socksimpl);
			}
			sockImplField.set(socks, socksimpl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new OioSocketChannel(socks);
	}
}
