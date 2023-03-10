/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socks.SocksAuthRequest;
import io.netty.handler.codec.socks.SocksCommonUtils;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.handler.codec.socks.SocksSubnegotiationVersion;
import io.netty.util.CharsetUtil;
import java.util.List;

public class SocksAuthRequestDecoder
extends ReplayingDecoder<State> {
    private static final String name = "SOCKS_AUTH_REQUEST_DECODER";
    private SocksSubnegotiationVersion version;
    private int fieldLength;
    private String username;
    private String password;
    private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;

    @Deprecated
    public static String getName() {
        return name;
    }

    public SocksAuthRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        switch ((State)((Object)this.state())) {
            case CHECK_PROTOCOL_VERSION: {
                this.version = SocksSubnegotiationVersion.valueOf(byteBuf.readByte());
                if (this.version != SocksSubnegotiationVersion.AUTH_PASSWORD) break;
                this.checkpoint(State.READ_USERNAME);
            }
            case READ_USERNAME: {
                this.fieldLength = byteBuf.readByte();
                this.username = byteBuf.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                this.checkpoint(State.READ_PASSWORD);
            }
            case READ_PASSWORD: {
                this.fieldLength = byteBuf.readByte();
                this.password = byteBuf.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                this.msg = new SocksAuthRequest(this.username, this.password);
            }
        }
        ctx.pipeline().remove(this);
        out.add(this.msg);
    }

    static enum State {
        CHECK_PROTOCOL_VERSION,
        READ_USERNAME,
        READ_PASSWORD;

    }
}

