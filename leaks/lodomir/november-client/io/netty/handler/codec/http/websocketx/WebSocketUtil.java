/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class WebSocketUtil {
    static byte[] md5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(data);
        }
        catch (NoSuchAlgorithmException e) {
            throw new InternalError("MD5 not supported on this platform - Outdated?");
        }
    }

    static byte[] sha1(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            return md.digest(data);
        }
        catch (NoSuchAlgorithmException e) {
            throw new InternalError("SHA-1 is not supported on this platform - Outdated?");
        }
    }

    static String base64(byte[] data) {
        ByteBuf encodedData = Unpooled.wrappedBuffer(data);
        ByteBuf encoded = Base64.encode(encodedData);
        String encodedString = encoded.toString(CharsetUtil.UTF_8);
        encoded.release();
        return encodedString;
    }

    static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        for (int index = 0; index < size; ++index) {
            bytes[index] = (byte)WebSocketUtil.randomNumber(0, 255);
        }
        return bytes;
    }

    static int randomNumber(int minimum, int maximum) {
        return (int)(Math.random() * (double)maximum + (double)minimum);
    }

    private WebSocketUtil() {
    }
}

