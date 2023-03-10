/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.ComposedLastHttpContent;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class HttpContentEncoder
extends MessageToMessageCodec<HttpRequest, HttpObject> {
    private final Queue<String> acceptEncodingQueue = new ArrayDeque<String>();
    private String acceptEncoding;
    private EmbeddedChannel encoder;
    private State state = State.AWAIT_HEADERS;

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg instanceof HttpContent || msg instanceof HttpResponse;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpRequest msg, List<Object> out) throws Exception {
        String acceptedEncoding = msg.headers().get("Accept-Encoding");
        if (acceptedEncoding == null) {
            acceptedEncoding = "identity";
        }
        this.acceptEncodingQueue.add(acceptedEncoding);
        out.add(ReferenceCountUtil.retain(msg));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        boolean isFull = msg instanceof HttpResponse && msg instanceof LastHttpContent;
        switch (this.state) {
            case AWAIT_HEADERS: {
                HttpContentEncoder.ensureHeaders(msg);
                assert (this.encoder == null);
                HttpResponse res = (HttpResponse)msg;
                if (res.getStatus().code() == 100) {
                    if (isFull) {
                        out.add(ReferenceCountUtil.retain(res));
                        break;
                    }
                    out.add(res);
                    this.state = State.PASS_THROUGH;
                    break;
                }
                this.acceptEncoding = this.acceptEncodingQueue.poll();
                if (this.acceptEncoding == null) {
                    throw new IllegalStateException("cannot send more responses than requests");
                }
                if (isFull && !((ByteBufHolder)((Object)res)).content().isReadable()) {
                    out.add(ReferenceCountUtil.retain(res));
                    break;
                }
                Result result = this.beginEncode(res, this.acceptEncoding);
                if (result == null) {
                    if (isFull) {
                        out.add(ReferenceCountUtil.retain(res));
                        break;
                    }
                    out.add(res);
                    this.state = State.PASS_THROUGH;
                    break;
                }
                this.encoder = result.contentEncoder();
                res.headers().set("Content-Encoding", (Object)result.targetContentEncoding());
                res.headers().remove("Content-Length");
                res.headers().set("Transfer-Encoding", (Object)"chunked");
                if (isFull) {
                    DefaultHttpResponse newRes = new DefaultHttpResponse(res.getProtocolVersion(), res.getStatus());
                    newRes.headers().set(res.headers());
                    out.add(newRes);
                } else {
                    out.add(res);
                    this.state = State.AWAIT_CONTENT;
                    if (!(msg instanceof HttpContent)) break;
                }
            }
            case AWAIT_CONTENT: {
                HttpContentEncoder.ensureContent(msg);
                if (!this.encodeContent((HttpContent)msg, out)) break;
                this.state = State.AWAIT_HEADERS;
                break;
            }
            case PASS_THROUGH: {
                HttpContentEncoder.ensureContent(msg);
                out.add(ReferenceCountUtil.retain(msg));
                if (!(msg instanceof LastHttpContent)) break;
                this.state = State.AWAIT_HEADERS;
            }
        }
    }

    private static void ensureHeaders(HttpObject msg) {
        if (!(msg instanceof HttpResponse)) {
            throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
        }
    }

    private static void ensureContent(HttpObject msg) {
        if (!(msg instanceof HttpContent)) {
            throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
        }
    }

    private boolean encodeContent(HttpContent c, List<Object> out) {
        ByteBuf content = c.content();
        this.encode(content, out);
        if (c instanceof LastHttpContent) {
            this.finishEncode(out);
            LastHttpContent last = (LastHttpContent)c;
            HttpHeaders headers = last.trailingHeaders();
            if (headers.isEmpty()) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                out.add(new ComposedLastHttpContent(headers));
            }
            return true;
        }
        return false;
    }

    protected abstract Result beginEncode(HttpResponse var1, String var2) throws Exception;

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.channelInactive(ctx);
    }

    private void cleanup() {
        if (this.encoder != null) {
            if (this.encoder.finish()) {
                ByteBuf buf;
                while ((buf = (ByteBuf)this.encoder.readOutbound()) != null) {
                    buf.release();
                }
            }
            this.encoder = null;
        }
    }

    private void encode(ByteBuf in, List<Object> out) {
        this.encoder.writeOutbound(in.retain());
        this.fetchEncoderOutput(out);
    }

    private void finishEncode(List<Object> out) {
        if (this.encoder.finish()) {
            this.fetchEncoderOutput(out);
        }
        this.encoder = null;
    }

    private void fetchEncoderOutput(List<Object> out) {
        ByteBuf buf;
        while ((buf = (ByteBuf)this.encoder.readOutbound()) != null) {
            if (!buf.isReadable()) {
                buf.release();
                continue;
            }
            out.add(new DefaultHttpContent(buf));
        }
    }

    public static final class Result {
        private final String targetContentEncoding;
        private final EmbeddedChannel contentEncoder;

        public Result(String targetContentEncoding, EmbeddedChannel contentEncoder) {
            if (targetContentEncoding == null) {
                throw new NullPointerException("targetContentEncoding");
            }
            if (contentEncoder == null) {
                throw new NullPointerException("contentEncoder");
            }
            this.targetContentEncoding = targetContentEncoding;
            this.contentEncoder = contentEncoder;
        }

        public String targetContentEncoding() {
            return this.targetContentEncoding;
        }

        public EmbeddedChannel contentEncoder() {
            return this.contentEncoder;
        }
    }

    private static enum State {
        PASS_THROUGH,
        AWAIT_HEADERS,
        AWAIT_CONTENT;

    }
}

