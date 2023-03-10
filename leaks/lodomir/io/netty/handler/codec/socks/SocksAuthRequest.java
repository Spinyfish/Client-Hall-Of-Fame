/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.handler.codec.socks.SocksRequestType;
import io.netty.handler.codec.socks.SocksSubnegotiationVersion;
import io.netty.util.CharsetUtil;
import java.nio.charset.CharsetEncoder;

public final class SocksAuthRequest
extends SocksRequest {
    private static final CharsetEncoder asciiEncoder = CharsetUtil.getEncoder(CharsetUtil.US_ASCII);
    private static final SocksSubnegotiationVersion SUBNEGOTIATION_VERSION = SocksSubnegotiationVersion.AUTH_PASSWORD;
    private final String username;
    private final String password;

    public SocksAuthRequest(String username, String password) {
        super(SocksRequestType.AUTH);
        if (username == null) {
            throw new NullPointerException("username");
        }
        if (password == null) {
            throw new NullPointerException("username");
        }
        if (!asciiEncoder.canEncode(username) || !asciiEncoder.canEncode(password)) {
            throw new IllegalArgumentException(" username: " + username + " or password: " + password + " values should be in pure ascii");
        }
        if (username.length() > 255) {
            throw new IllegalArgumentException(username + " exceeds 255 char limit");
        }
        if (password.length() > 255) {
            throw new IllegalArgumentException(password + " exceeds 255 char limit");
        }
        this.username = username;
        this.password = password;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    @Override
    public void encodeAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeByte(SUBNEGOTIATION_VERSION.byteValue());
        byteBuf.writeByte(this.username.length());
        byteBuf.writeBytes(this.username.getBytes(CharsetUtil.US_ASCII));
        byteBuf.writeByte(this.password.length());
        byteBuf.writeBytes(this.password.getBytes(CharsetUtil.US_ASCII));
    }
}

