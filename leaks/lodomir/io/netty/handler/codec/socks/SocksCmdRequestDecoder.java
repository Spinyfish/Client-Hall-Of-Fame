/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdType;
import io.netty.handler.codec.socks.SocksCommonUtils;
import io.netty.handler.codec.socks.SocksProtocolVersion;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.util.CharsetUtil;
import java.util.List;

public class SocksCmdRequestDecoder
extends ReplayingDecoder<State> {
    private static final String name = "SOCKS_CMD_REQUEST_DECODER";
    private SocksProtocolVersion version;
    private int fieldLength;
    private SocksCmdType cmdType;
    private SocksAddressType addressType;
    private byte reserved;
    private String host;
    private int port;
    private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;

    @Deprecated
    public static String getName() {
        return name;
    }

    public SocksCmdRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        block0 : switch ((State)((Object)this.state())) {
            case CHECK_PROTOCOL_VERSION: {
                this.version = SocksProtocolVersion.valueOf(byteBuf.readByte());
                if (this.version != SocksProtocolVersion.SOCKS5) break;
                this.checkpoint(State.READ_CMD_HEADER);
            }
            case READ_CMD_HEADER: {
                this.cmdType = SocksCmdType.valueOf(byteBuf.readByte());
                this.reserved = byteBuf.readByte();
                this.addressType = SocksAddressType.valueOf(byteBuf.readByte());
                this.checkpoint(State.READ_CMD_ADDRESS);
            }
            case READ_CMD_ADDRESS: {
                switch (this.addressType) {
                    case IPv4: {
                        this.host = SocksCommonUtils.intToIp(byteBuf.readInt());
                        this.port = byteBuf.readUnsignedShort();
                        this.msg = new SocksCmdRequest(this.cmdType, this.addressType, this.host, this.port);
                        break block0;
                    }
                    case DOMAIN: {
                        this.fieldLength = byteBuf.readByte();
                        this.host = byteBuf.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                        this.port = byteBuf.readUnsignedShort();
                        this.msg = new SocksCmdRequest(this.cmdType, this.addressType, this.host, this.port);
                        break block0;
                    }
                    case IPv6: {
                        this.host = SocksCommonUtils.ipv6toStr(byteBuf.readBytes(16).array());
                        this.port = byteBuf.readUnsignedShort();
                        this.msg = new SocksCmdRequest(this.cmdType, this.addressType, this.host, this.port);
                        break block0;
                    }
                }
            }
        }
        ctx.pipeline().remove(this);
        out.add(this.msg);
    }

    static enum State {
        CHECK_PROTOCOL_VERSION,
        READ_CMD_HEADER,
        READ_CMD_ADDRESS;

    }
}

