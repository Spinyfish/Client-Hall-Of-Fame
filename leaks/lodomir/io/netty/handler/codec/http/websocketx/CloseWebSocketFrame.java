/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.EmptyArrays;

public class CloseWebSocketFrame
extends WebSocketFrame {
    public CloseWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public CloseWebSocketFrame(int statusCode, String reasonText) {
        this(true, 0, statusCode, reasonText);
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv) {
        this(finalFragment, rsv, Unpooled.buffer(0));
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv, int statusCode, String reasonText) {
        super(finalFragment, rsv, CloseWebSocketFrame.newBinaryData(statusCode, reasonText));
    }

    private static ByteBuf newBinaryData(int statusCode, String reasonText) {
        byte[] reasonBytes = EmptyArrays.EMPTY_BYTES;
        if (reasonText != null) {
            reasonBytes = reasonText.getBytes(CharsetUtil.UTF_8);
        }
        ByteBuf binaryData = Unpooled.buffer(2 + reasonBytes.length);
        binaryData.writeShort(statusCode);
        if (reasonBytes.length > 0) {
            binaryData.writeBytes(reasonBytes);
        }
        binaryData.readerIndex(0);
        return binaryData;
    }

    public CloseWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
    }

    public int statusCode() {
        ByteBuf binaryData = this.content();
        if (binaryData == null || binaryData.capacity() == 0) {
            return -1;
        }
        binaryData.readerIndex(0);
        short statusCode = binaryData.readShort();
        binaryData.readerIndex(0);
        return statusCode;
    }

    public String reasonText() {
        ByteBuf binaryData = this.content();
        if (binaryData == null || binaryData.capacity() <= 2) {
            return "";
        }
        binaryData.readerIndex(2);
        String reasonText = binaryData.toString(CharsetUtil.UTF_8);
        binaryData.readerIndex(0);
        return reasonText;
    }

    @Override
    public CloseWebSocketFrame copy() {
        return new CloseWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy());
    }

    @Override
    public CloseWebSocketFrame duplicate() {
        return new CloseWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate());
    }

    @Override
    public CloseWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public CloseWebSocketFrame retain(int increment) {
        super.retain(increment);
        return this;
    }
}

