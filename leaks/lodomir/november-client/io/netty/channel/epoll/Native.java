/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.ChannelException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Locale;

final class Native {
    private static final byte[] IPV4_MAPPED_IPV6_PREFIX = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1};
    public static final int EPOLLIN = 1;
    public static final int EPOLLOUT = 2;
    public static final int EPOLLACCEPT = 4;
    public static final int EPOLLRDHUP = 8;
    public static final int IOV_MAX;

    public static native int eventFd();

    public static native void eventFdWrite(int var0, long var1);

    public static native void eventFdRead(int var0);

    public static native int epollCreate();

    public static native int epollWait(int var0, long[] var1, int var2);

    public static native void epollCtlAdd(int var0, int var1, int var2, int var3);

    public static native void epollCtlMod(int var0, int var1, int var2, int var3);

    public static native void epollCtlDel(int var0, int var1);

    public static native void close(int var0) throws IOException;

    public static native int write(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

    public static native int writeAddress(int var0, long var1, int var3, int var4) throws IOException;

    public static native long writev(int var0, ByteBuffer[] var1, int var2, int var3) throws IOException;

    public static native long writevAddresses(int var0, long var1, int var3) throws IOException;

    public static native int read(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

    public static native int readAddress(int var0, long var1, int var3, int var4) throws IOException;

    public static native long sendfile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

    public static int sendTo(int fd, ByteBuffer buf, int pos, int limit, InetAddress addr, int port) throws IOException {
        int scopeId;
        byte[] address;
        if (addr instanceof Inet6Address) {
            address = addr.getAddress();
            scopeId = ((Inet6Address)addr).getScopeId();
        } else {
            scopeId = 0;
            address = Native.ipv4MappedIpv6Address(addr.getAddress());
        }
        return Native.sendTo(fd, buf, pos, limit, address, scopeId, port);
    }

    private static native int sendTo(int var0, ByteBuffer var1, int var2, int var3, byte[] var4, int var5, int var6) throws IOException;

    public static int sendToAddress(int fd, long memoryAddress, int pos, int limit, InetAddress addr, int port) throws IOException {
        int scopeId;
        byte[] address;
        if (addr instanceof Inet6Address) {
            address = addr.getAddress();
            scopeId = ((Inet6Address)addr).getScopeId();
        } else {
            scopeId = 0;
            address = Native.ipv4MappedIpv6Address(addr.getAddress());
        }
        return Native.sendToAddress(fd, memoryAddress, pos, limit, address, scopeId, port);
    }

    private static native int sendToAddress(int var0, long var1, int var3, int var4, byte[] var5, int var6, int var7) throws IOException;

    public static native EpollDatagramChannel.DatagramSocketAddress recvFrom(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

    public static native EpollDatagramChannel.DatagramSocketAddress recvFromAddress(int var0, long var1, int var3, int var4) throws IOException;

    public static int socketStreamFd() {
        try {
            return Native.socketStream();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public static int socketDgramFd() {
        try {
            return Native.socketDgram();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    private static native int socketStream() throws IOException;

    private static native int socketDgram() throws IOException;

    public static void bind(int fd, InetAddress addr, int port) throws IOException {
        NativeInetAddress address = Native.toNativeInetAddress(addr);
        Native.bind(fd, address.address, address.scopeId, port);
    }

    private static byte[] ipv4MappedIpv6Address(byte[] ipv4) {
        byte[] address = new byte[16];
        System.arraycopy(IPV4_MAPPED_IPV6_PREFIX, 0, address, 0, IPV4_MAPPED_IPV6_PREFIX.length);
        System.arraycopy(ipv4, 0, address, 12, ipv4.length);
        return address;
    }

    public static native void bind(int var0, byte[] var1, int var2, int var3) throws IOException;

    public static native void listen(int var0, int var1) throws IOException;

    public static boolean connect(int fd, InetAddress addr, int port) throws IOException {
        NativeInetAddress address = Native.toNativeInetAddress(addr);
        return Native.connect(fd, address.address, address.scopeId, port);
    }

    public static native boolean connect(int var0, byte[] var1, int var2, int var3) throws IOException;

    public static native boolean finishConnect(int var0) throws IOException;

    public static native InetSocketAddress remoteAddress(int var0);

    public static native InetSocketAddress localAddress(int var0);

    public static native int accept(int var0) throws IOException;

    public static native void shutdown(int var0, boolean var1, boolean var2) throws IOException;

    public static native int getReceiveBufferSize(int var0);

    public static native int getSendBufferSize(int var0);

    public static native int isKeepAlive(int var0);

    public static native int isReuseAddress(int var0);

    public static native int isReusePort(int var0);

    public static native int isTcpNoDelay(int var0);

    public static native int isTcpCork(int var0);

    public static native int getSoLinger(int var0);

    public static native int getTrafficClass(int var0);

    public static native int isBroadcast(int var0);

    public static native int getTcpKeepIdle(int var0);

    public static native int getTcpKeepIntvl(int var0);

    public static native int getTcpKeepCnt(int var0);

    public static native void setKeepAlive(int var0, int var1);

    public static native void setReceiveBufferSize(int var0, int var1);

    public static native void setReuseAddress(int var0, int var1);

    public static native void setReusePort(int var0, int var1);

    public static native void setSendBufferSize(int var0, int var1);

    public static native void setTcpNoDelay(int var0, int var1);

    public static native void setTcpCork(int var0, int var1);

    public static native void setSoLinger(int var0, int var1);

    public static native void setTrafficClass(int var0, int var1);

    public static native void setBroadcast(int var0, int var1);

    public static native void setTcpKeepIdle(int var0, int var1);

    public static native void setTcpKeepIntvl(int var0, int var1);

    public static native void setTcpKeepCnt(int var0, int var1);

    private static NativeInetAddress toNativeInetAddress(InetAddress addr) {
        byte[] bytes = addr.getAddress();
        if (addr instanceof Inet6Address) {
            return new NativeInetAddress(bytes, ((Inet6Address)addr).getScopeId());
        }
        return new NativeInetAddress(Native.ipv4MappedIpv6Address(bytes));
    }

    public static native String kernelVersion();

    private static native int iovMax();

    private Native() {
    }

    static {
        String name = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
        if (!name.startsWith("linux")) {
            throw new IllegalStateException("Only supported on Linux");
        }
        NativeLibraryLoader.load("netty-transport-native-epoll", PlatformDependent.getClassLoader(Native.class));
        IOV_MAX = Native.iovMax();
    }

    private static class NativeInetAddress {
        final byte[] address;
        final int scopeId;

        NativeInetAddress(byte[] address, int scopeId) {
            this.address = address;
            this.scopeId = scopeId;
        }

        NativeInetAddress(byte[] address) {
            this(address, 0);
        }
    }
}

