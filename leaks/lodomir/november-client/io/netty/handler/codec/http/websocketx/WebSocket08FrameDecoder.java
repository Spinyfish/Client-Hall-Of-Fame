/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.Utf8Validator;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.List;

public class WebSocket08FrameDecoder
extends ReplayingDecoder<State>
implements WebSocketFrameDecoder {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private int fragmentedFramesCount;
    private final long maxFramePayloadLength;
    private boolean frameFinalFlag;
    private int frameRsv;
    private int frameOpcode;
    private long framePayloadLength;
    private ByteBuf framePayload;
    private int framePayloadBytesRead;
    private byte[] maskingKey;
    private ByteBuf payloadBuffer;
    private final boolean allowExtensions;
    private final boolean maskedPayload;
    private boolean receivedClosingHandshake;
    private Utf8Validator utf8Validator;

    public WebSocket08FrameDecoder(boolean maskedPayload, boolean allowExtensions, int maxFramePayloadLength) {
        super(State.FRAME_START);
        this.maskedPayload = maskedPayload;
        this.allowExtensions = allowExtensions;
        this.maxFramePayloadLength = maxFramePayloadLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.receivedClosingHandshake) {
            in.skipBytes(this.actualReadableBytes());
            return;
        }
        try {
            switch ((State)((Object)this.state())) {
                case FRAME_START: {
                    this.framePayloadBytesRead = 0;
                    this.framePayloadLength = -1L;
                    this.framePayload = null;
                    this.payloadBuffer = null;
                    byte b = in.readByte();
                    this.frameFinalFlag = (b & 0x80) != 0;
                    this.frameRsv = (b & 0x70) >> 4;
                    this.frameOpcode = b & 0xF;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Decoding WebSocket Frame opCode={}", (Object)this.frameOpcode);
                    }
                    boolean frameMasked = ((b = in.readByte()) & 0x80) != 0;
                    int framePayloadLen1 = b & 0x7F;
                    if (this.frameRsv != 0 && !this.allowExtensions) {
                        this.protocolViolation(ctx, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
                        return;
                    }
                    if (this.maskedPayload && !frameMasked) {
                        this.protocolViolation(ctx, "unmasked client to server frame");
                        return;
                    }
                    if (this.frameOpcode > 7) {
                        if (!this.frameFinalFlag) {
                            this.protocolViolation(ctx, "fragmented control frame");
                            return;
                        }
                        if (framePayloadLen1 > 125) {
                            this.protocolViolation(ctx, "control frame with payload length > 125 octets");
                            return;
                        }
                        if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                            this.protocolViolation(ctx, "control frame using reserved opcode " + this.frameOpcode);
                            return;
                        }
                        if (this.frameOpcode == 8 && framePayloadLen1 == 1) {
                            this.protocolViolation(ctx, "received close control frame with payload len 1");
                            return;
                        }
                    } else {
                        if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                            this.protocolViolation(ctx, "data frame using reserved opcode " + this.frameOpcode);
                            return;
                        }
                        if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                            this.protocolViolation(ctx, "received continuation data frame outside fragmented message");
                            return;
                        }
                        if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0 && this.frameOpcode != 9) {
                            this.protocolViolation(ctx, "received non-continuation data frame while inside fragmented message");
                            return;
                        }
                    }
                    if (framePayloadLen1 == 126) {
                        this.framePayloadLength = in.readUnsignedShort();
                        if (this.framePayloadLength < 126L) {
                            this.protocolViolation(ctx, "invalid data frame length (not using minimal length encoding)");
                            return;
                        }
                    } else if (framePayloadLen1 == 127) {
                        this.framePayloadLength = in.readLong();
                        if (this.framePayloadLength < 65536L) {
                            this.protocolViolation(ctx, "invalid data frame length (not using minimal length encoding)");
                            return;
                        }
                    } else {
                        this.framePayloadLength = framePayloadLen1;
                    }
                    if (this.framePayloadLength > this.maxFramePayloadLength) {
                        this.protocolViolation(ctx, "Max frame length of " + this.maxFramePayloadLength + " has been exceeded.");
                        return;
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Decoding WebSocket Frame length={}", (Object)this.framePayloadLength);
                    }
                    this.checkpoint(State.MASKING_KEY);
                }
                case MASKING_KEY: {
                    if (this.maskedPayload) {
                        if (this.maskingKey == null) {
                            this.maskingKey = new byte[4];
                        }
                        in.readBytes(this.maskingKey);
                    }
                    this.checkpoint(State.PAYLOAD);
                }
                case PAYLOAD: {
                    int rbytes = this.actualReadableBytes();
                    long willHaveReadByteCount = this.framePayloadBytesRead + rbytes;
                    if (willHaveReadByteCount == this.framePayloadLength) {
                        this.payloadBuffer = ctx.alloc().buffer(rbytes);
                        this.payloadBuffer.writeBytes(in, rbytes);
                    } else {
                        if (willHaveReadByteCount < this.framePayloadLength) {
                            if (this.framePayload == null) {
                                this.framePayload = ctx.alloc().buffer(WebSocket08FrameDecoder.toFrameLength(this.framePayloadLength));
                            }
                            this.framePayload.writeBytes(in, rbytes);
                            this.framePayloadBytesRead += rbytes;
                            return;
                        }
                        if (willHaveReadByteCount > this.framePayloadLength) {
                            if (this.framePayload == null) {
                                this.framePayload = ctx.alloc().buffer(WebSocket08FrameDecoder.toFrameLength(this.framePayloadLength));
                            }
                            this.framePayload.writeBytes(in, WebSocket08FrameDecoder.toFrameLength(this.framePayloadLength - (long)this.framePayloadBytesRead));
                        }
                    }
                    this.checkpoint(State.FRAME_START);
                    if (this.framePayload == null) {
                        this.framePayload = this.payloadBuffer;
                        this.payloadBuffer = null;
                    } else if (this.payloadBuffer != null) {
                        this.framePayload.writeBytes(this.payloadBuffer);
                        this.payloadBuffer.release();
                        this.payloadBuffer = null;
                    }
                    if (this.maskedPayload) {
                        this.unmask(this.framePayload);
                    }
                    if (this.frameOpcode == 9) {
                        out.add(new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                        this.framePayload = null;
                        return;
                    }
                    if (this.frameOpcode == 10) {
                        out.add(new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                        this.framePayload = null;
                        return;
                    }
                    if (this.frameOpcode == 8) {
                        this.checkCloseFrameBody(ctx, this.framePayload);
                        this.receivedClosingHandshake = true;
                        out.add(new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                        this.framePayload = null;
                        return;
                    }
                    if (this.frameFinalFlag) {
                        if (this.frameOpcode != 9) {
                            this.fragmentedFramesCount = 0;
                            if (this.frameOpcode == 1 || this.utf8Validator != null && this.utf8Validator.isChecking()) {
                                this.checkUTF8String(ctx, this.framePayload);
                                this.utf8Validator.finish();
                            }
                        }
                    } else {
                        if (this.fragmentedFramesCount == 0) {
                            if (this.frameOpcode == 1) {
                                this.checkUTF8String(ctx, this.framePayload);
                            }
                        } else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
                            this.checkUTF8String(ctx, this.framePayload);
                        }
                        ++this.fragmentedFramesCount;
                    }
                    if (this.frameOpcode == 1) {
                        out.add(new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                        this.framePayload = null;
                        return;
                    }
                    if (this.frameOpcode == 2) {
                        out.add(new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                        this.framePayload = null;
                        return;
                    }
                    if (this.frameOpcode == 0) {
                        out.add(new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                        this.framePayload = null;
                        return;
                    }
                    throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
                }
                case CORRUPT: {
                    in.readByte();
                    return;
                }
            }
            throw new Error("Shouldn't reach here.");
        }
        catch (Exception e) {
            if (this.payloadBuffer != null) {
                if (this.payloadBuffer.refCnt() > 0) {
                    this.payloadBuffer.release();
                }
                this.payloadBuffer = null;
            }
            if (this.framePayload != null) {
                if (this.framePayload.refCnt() > 0) {
                    this.framePayload.release();
                }
                this.framePayload = null;
            }
            throw e;
        }
    }

    private void unmask(ByteBuf frame) {
        for (int i = frame.readerIndex(); i < frame.writerIndex(); ++i) {
            frame.setByte(i, frame.getByte(i) ^ this.maskingKey[i % 4]);
        }
    }

    private void protocolViolation(ChannelHandlerContext ctx, String reason) {
        this.protocolViolation(ctx, new CorruptedFrameException(reason));
    }

    private void protocolViolation(ChannelHandlerContext ctx, CorruptedFrameException ex) {
        this.checkpoint(State.CORRUPT);
        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
        throw ex;
    }

    private static int toFrameLength(long l) {
        if (l > Integer.MAX_VALUE) {
            throw new TooLongFrameException("Length:" + l);
        }
        return (int)l;
    }

    private void checkUTF8String(ChannelHandlerContext ctx, ByteBuf buffer) {
        try {
            if (this.utf8Validator == null) {
                this.utf8Validator = new Utf8Validator();
            }
            this.utf8Validator.check(buffer);
        }
        catch (CorruptedFrameException ex) {
            this.protocolViolation(ctx, ex);
        }
    }

    protected void checkCloseFrameBody(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (buffer == null || !buffer.isReadable()) {
            return;
        }
        if (buffer.readableBytes() == 1) {
            this.protocolViolation(ctx, "Invalid close frame body");
        }
        int idx = buffer.readerIndex();
        buffer.readerIndex(0);
        short statusCode = buffer.readShort();
        if (statusCode >= 0 && statusCode <= 999 || statusCode >= 1004 && statusCode <= 1006 || statusCode >= 1012 && statusCode <= 2999) {
            this.protocolViolation(ctx, "Invalid close frame getStatus code: " + statusCode);
        }
        if (buffer.isReadable()) {
            try {
                new Utf8Validator().check(buffer);
            }
            catch (CorruptedFrameException ex) {
                this.protocolViolation(ctx, ex);
            }
        }
        buffer.readerIndex(idx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (this.framePayload != null) {
            this.framePayload.release();
        }
        if (this.payloadBuffer != null) {
            this.payloadBuffer.release();
        }
    }

    static enum State {
        FRAME_START,
        MASKING_KEY,
        PAYLOAD,
        CORRUPT;

    }
}

