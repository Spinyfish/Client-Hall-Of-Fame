/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.AppendableCharSequence;
import java.util.List;

public abstract class HttpObjectDecoder
extends ReplayingDecoder<State> {
    private final int maxInitialLineLength;
    private final int maxHeaderSize;
    private final int maxChunkSize;
    private final boolean chunkedSupported;
    protected final boolean validateHeaders;
    private final AppendableCharSequence seq = new AppendableCharSequence(128);
    private final HeaderParser headerParser = new HeaderParser(this.seq);
    private final LineParser lineParser = new LineParser(this.seq);
    private HttpMessage message;
    private long chunkSize;
    private int headerSize;
    private long contentLength = Long.MIN_VALUE;

    protected HttpObjectDecoder() {
        this(4096, 8192, 8192, true);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, true);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
        super(State.SKIP_CONTROL_CHARS);
        if (maxInitialLineLength <= 0) {
            throw new IllegalArgumentException("maxInitialLineLength must be a positive integer: " + maxInitialLineLength);
        }
        if (maxHeaderSize <= 0) {
            throw new IllegalArgumentException("maxHeaderSize must be a positive integer: " + maxHeaderSize);
        }
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + maxChunkSize);
        }
        this.maxInitialLineLength = maxInitialLineLength;
        this.maxHeaderSize = maxHeaderSize;
        this.maxChunkSize = maxChunkSize;
        this.chunkedSupported = chunkedSupported;
        this.validateHeaders = validateHeaders;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        switch ((State)((Object)this.state())) {
            case SKIP_CONTROL_CHARS: {
                try {
                    HttpObjectDecoder.skipControlCharacters(buffer);
                    this.checkpoint(State.READ_INITIAL);
                }
                finally {
                    this.checkpoint();
                }
            }
            case READ_INITIAL: {
                try {
                    String[] initialLine = HttpObjectDecoder.splitInitialLine(this.lineParser.parse(buffer));
                    if (initialLine.length < 3) {
                        this.checkpoint(State.SKIP_CONTROL_CHARS);
                        return;
                    }
                    this.message = this.createMessage(initialLine);
                    this.checkpoint(State.READ_HEADER);
                }
                catch (Exception e) {
                    out.add(this.invalidMessage(e));
                    return;
                }
            }
            case READ_HEADER: {
                try {
                    State nextState = this.readHeaders(buffer);
                    this.checkpoint(nextState);
                    if (nextState == State.READ_CHUNK_SIZE) {
                        if (!this.chunkedSupported) {
                            throw new IllegalArgumentException("Chunked messages not supported");
                        }
                        out.add(this.message);
                        return;
                    }
                    if (nextState == State.SKIP_CONTROL_CHARS) {
                        out.add(this.message);
                        out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                        this.reset();
                        return;
                    }
                    long contentLength = this.contentLength();
                    if (contentLength == 0L || contentLength == -1L && this.isDecodingRequest()) {
                        out.add(this.message);
                        out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                        this.reset();
                        return;
                    }
                    assert (nextState == State.READ_FIXED_LENGTH_CONTENT || nextState == State.READ_VARIABLE_LENGTH_CONTENT);
                    out.add(this.message);
                    if (nextState == State.READ_FIXED_LENGTH_CONTENT) {
                        this.chunkSize = contentLength;
                    }
                    return;
                }
                catch (Exception e) {
                    out.add(this.invalidMessage(e));
                    return;
                }
            }
            case READ_VARIABLE_LENGTH_CONTENT: {
                int toRead = Math.min(this.actualReadableBytes(), this.maxChunkSize);
                if (toRead > 0) {
                    ByteBuf content = ByteBufUtil.readBytes(ctx.alloc(), buffer, toRead);
                    if (buffer.isReadable()) {
                        out.add(new DefaultHttpContent(content));
                    } else {
                        out.add(new DefaultLastHttpContent(content, this.validateHeaders));
                        this.reset();
                    }
                } else if (!buffer.isReadable()) {
                    out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                    this.reset();
                }
                return;
            }
            case READ_FIXED_LENGTH_CONTENT: {
                int readLimit = this.actualReadableBytes();
                if (readLimit == 0) {
                    return;
                }
                int toRead = Math.min(readLimit, this.maxChunkSize);
                if ((long)toRead > this.chunkSize) {
                    toRead = (int)this.chunkSize;
                }
                ByteBuf content = ByteBufUtil.readBytes(ctx.alloc(), buffer, toRead);
                this.chunkSize -= (long)toRead;
                if (this.chunkSize == 0L) {
                    out.add(new DefaultLastHttpContent(content, this.validateHeaders));
                    this.reset();
                } else {
                    out.add(new DefaultHttpContent(content));
                }
                return;
            }
            case READ_CHUNK_SIZE: {
                try {
                    AppendableCharSequence line = this.lineParser.parse(buffer);
                    int chunkSize = HttpObjectDecoder.getChunkSize(line.toString());
                    this.chunkSize = chunkSize;
                    if (chunkSize == 0) {
                        this.checkpoint(State.READ_CHUNK_FOOTER);
                        return;
                    }
                    this.checkpoint(State.READ_CHUNKED_CONTENT);
                }
                catch (Exception e) {
                    out.add(this.invalidChunk(e));
                    return;
                }
            }
            case READ_CHUNKED_CONTENT: {
                assert (this.chunkSize <= Integer.MAX_VALUE);
                int toRead = Math.min((int)this.chunkSize, this.maxChunkSize);
                DefaultHttpContent chunk = new DefaultHttpContent(ByteBufUtil.readBytes(ctx.alloc(), buffer, toRead));
                this.chunkSize -= (long)toRead;
                out.add(chunk);
                if (this.chunkSize == 0L) {
                    this.checkpoint(State.READ_CHUNK_DELIMITER);
                } else {
                    return;
                }
            }
            case READ_CHUNK_DELIMITER: {
                while (true) {
                    byte next;
                    if ((next = buffer.readByte()) == 13) {
                        if (buffer.readByte() != 10) continue;
                        this.checkpoint(State.READ_CHUNK_SIZE);
                        return;
                    }
                    if (next == 10) {
                        this.checkpoint(State.READ_CHUNK_SIZE);
                        return;
                    }
                    this.checkpoint();
                }
            }
            case READ_CHUNK_FOOTER: {
                try {
                    LastHttpContent trailer = this.readTrailingHeaders(buffer);
                    out.add(trailer);
                    this.reset();
                    return;
                }
                catch (Exception e) {
                    out.add(this.invalidChunk(e));
                    return;
                }
            }
            case BAD_MESSAGE: {
                buffer.skipBytes(this.actualReadableBytes());
                break;
            }
            case UPGRADED: {
                int readableBytes = this.actualReadableBytes();
                if (readableBytes <= 0) break;
                out.add(buffer.readBytes(this.actualReadableBytes()));
                break;
            }
        }
    }

    @Override
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.decode(ctx, in, out);
        if (this.message != null) {
            boolean prematureClosure = this.isDecodingRequest() ? true : this.contentLength() > 0L;
            this.reset();
            if (!prematureClosure) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            }
        }
    }

    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse res = (HttpResponse)msg;
            int code = res.getStatus().code();
            if (code >= 100 && code < 200) {
                return code != 101 || res.headers().contains("Sec-WebSocket-Accept");
            }
            switch (code) {
                case 204: 
                case 205: 
                case 304: {
                    return true;
                }
            }
        }
        return false;
    }

    private void reset() {
        HttpResponse res;
        HttpMessage message = this.message;
        this.message = null;
        this.contentLength = Long.MIN_VALUE;
        if (!this.isDecodingRequest() && (res = (HttpResponse)message) != null && res.getStatus().code() == 101) {
            this.checkpoint(State.UPGRADED);
            return;
        }
        this.checkpoint(State.SKIP_CONTROL_CHARS);
    }

    private HttpMessage invalidMessage(Exception cause) {
        this.checkpoint(State.BAD_MESSAGE);
        if (this.message != null) {
            this.message.setDecoderResult(DecoderResult.failure(cause));
        } else {
            this.message = this.createInvalidMessage();
            this.message.setDecoderResult(DecoderResult.failure(cause));
        }
        HttpMessage ret = this.message;
        this.message = null;
        return ret;
    }

    private HttpContent invalidChunk(Exception cause) {
        this.checkpoint(State.BAD_MESSAGE);
        DefaultLastHttpContent chunk = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
        chunk.setDecoderResult(DecoderResult.failure(cause));
        this.message = null;
        return chunk;
    }

    private static void skipControlCharacters(ByteBuf buffer) {
        char c;
        while (Character.isISOControl(c = (char)buffer.readUnsignedByte()) || Character.isWhitespace(c)) {
        }
        buffer.readerIndex(buffer.readerIndex() - 1);
    }

    private State readHeaders(ByteBuf buffer) {
        State nextState;
        this.headerSize = 0;
        HttpMessage message = this.message;
        HttpHeaders headers = message.headers();
        AppendableCharSequence line = this.headerParser.parse(buffer);
        String name = null;
        String value = null;
        if (line.length() > 0) {
            headers.clear();
            do {
                char firstChar = line.charAt(0);
                if (name != null && (firstChar == ' ' || firstChar == '\t')) {
                    value = value + ' ' + line.toString().trim();
                    continue;
                }
                if (name != null) {
                    headers.add(name, value);
                }
                String[] header = HttpObjectDecoder.splitHeader(line);
                name = header[0];
                value = header[1];
            } while ((line = this.headerParser.parse(buffer)).length() > 0);
            if (name != null) {
                headers.add(name, (Object)value);
            }
        }
        if (this.isContentAlwaysEmpty(message)) {
            HttpHeaders.removeTransferEncodingChunked(message);
            nextState = State.SKIP_CONTROL_CHARS;
        } else {
            nextState = HttpHeaders.isTransferEncodingChunked(message) ? State.READ_CHUNK_SIZE : (this.contentLength() >= 0L ? State.READ_FIXED_LENGTH_CONTENT : State.READ_VARIABLE_LENGTH_CONTENT);
        }
        return nextState;
    }

    private long contentLength() {
        if (this.contentLength == Long.MIN_VALUE) {
            this.contentLength = HttpHeaders.getContentLength(this.message, -1L);
        }
        return this.contentLength;
    }

    private LastHttpContent readTrailingHeaders(ByteBuf buffer) {
        this.headerSize = 0;
        AppendableCharSequence line = this.headerParser.parse(buffer);
        String lastHeader = null;
        if (line.length() > 0) {
            DefaultLastHttpContent trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
            do {
                char firstChar = line.charAt(0);
                if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
                    List<String> current = trailer.trailingHeaders().getAll(lastHeader);
                    if (current.isEmpty()) continue;
                    int lastPos = current.size() - 1;
                    String newString = current.get(lastPos) + line.toString().trim();
                    current.set(lastPos, newString);
                    continue;
                }
                String[] header = HttpObjectDecoder.splitHeader(line);
                String name = header[0];
                if (!(HttpHeaders.equalsIgnoreCase(name, "Content-Length") || HttpHeaders.equalsIgnoreCase(name, "Transfer-Encoding") || HttpHeaders.equalsIgnoreCase(name, "Trailer"))) {
                    trailer.trailingHeaders().add(name, (Object)header[1]);
                }
                lastHeader = name;
            } while ((line = this.headerParser.parse(buffer)).length() > 0);
            return trailer;
        }
        return LastHttpContent.EMPTY_LAST_CONTENT;
    }

    protected abstract boolean isDecodingRequest();

    protected abstract HttpMessage createMessage(String[] var1) throws Exception;

    protected abstract HttpMessage createInvalidMessage();

    private static int getChunkSize(String hex) {
        hex = hex.trim();
        for (int i = 0; i < hex.length(); ++i) {
            char c = hex.charAt(i);
            if (c != ';' && !Character.isWhitespace(c) && !Character.isISOControl(c)) continue;
            hex = hex.substring(0, i);
            break;
        }
        return Integer.parseInt(hex, 16);
    }

    private static String[] splitInitialLine(AppendableCharSequence sb) {
        int aStart = HttpObjectDecoder.findNonWhitespace(sb, 0);
        int aEnd = HttpObjectDecoder.findWhitespace(sb, aStart);
        int bStart = HttpObjectDecoder.findNonWhitespace(sb, aEnd);
        int bEnd = HttpObjectDecoder.findWhitespace(sb, bStart);
        int cStart = HttpObjectDecoder.findNonWhitespace(sb, bEnd);
        int cEnd = HttpObjectDecoder.findEndOfString(sb);
        return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), cStart < cEnd ? sb.substring(cStart, cEnd) : ""};
    }

    private static String[] splitHeader(AppendableCharSequence sb) {
        int valueStart;
        int colonEnd;
        int nameStart;
        char ch;
        int nameEnd;
        int length = sb.length();
        for (nameEnd = nameStart = HttpObjectDecoder.findNonWhitespace(sb, 0); nameEnd < length && (ch = sb.charAt(nameEnd)) != ':' && !Character.isWhitespace(ch); ++nameEnd) {
        }
        for (colonEnd = nameEnd; colonEnd < length; ++colonEnd) {
            if (sb.charAt(colonEnd) != ':') continue;
            ++colonEnd;
            break;
        }
        if ((valueStart = HttpObjectDecoder.findNonWhitespace(sb, colonEnd)) == length) {
            return new String[]{sb.substring(nameStart, nameEnd), ""};
        }
        int valueEnd = HttpObjectDecoder.findEndOfString(sb);
        return new String[]{sb.substring(nameStart, nameEnd), sb.substring(valueStart, valueEnd)};
    }

    private static int findNonWhitespace(CharSequence sb, int offset) {
        int result;
        for (result = offset; result < sb.length() && Character.isWhitespace(sb.charAt(result)); ++result) {
        }
        return result;
    }

    private static int findWhitespace(CharSequence sb, int offset) {
        int result;
        for (result = offset; result < sb.length() && !Character.isWhitespace(sb.charAt(result)); ++result) {
        }
        return result;
    }

    private static int findEndOfString(CharSequence sb) {
        int result;
        for (result = sb.length(); result > 0 && Character.isWhitespace(sb.charAt(result - 1)); --result) {
        }
        return result;
    }

    private final class LineParser
    implements ByteBufProcessor {
        private final AppendableCharSequence seq;
        private int size;

        LineParser(AppendableCharSequence seq) {
            this.seq = seq;
        }

        public AppendableCharSequence parse(ByteBuf buffer) {
            this.seq.reset();
            this.size = 0;
            int i = buffer.forEachByte(this);
            buffer.readerIndex(i + 1);
            return this.seq;
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char)value;
            if (nextByte == '\r') {
                return true;
            }
            if (nextByte == '\n') {
                return false;
            }
            if (this.size >= HttpObjectDecoder.this.maxInitialLineLength) {
                throw new TooLongFrameException("An HTTP line is larger than " + HttpObjectDecoder.this.maxInitialLineLength + " bytes.");
            }
            ++this.size;
            this.seq.append(nextByte);
            return true;
        }
    }

    private final class HeaderParser
    implements ByteBufProcessor {
        private final AppendableCharSequence seq;

        HeaderParser(AppendableCharSequence seq) {
            this.seq = seq;
        }

        public AppendableCharSequence parse(ByteBuf buffer) {
            this.seq.reset();
            HttpObjectDecoder.this.headerSize = 0;
            int i = buffer.forEachByte(this);
            buffer.readerIndex(i + 1);
            return this.seq;
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char)value;
            HttpObjectDecoder.this.headerSize++;
            if (nextByte == '\r') {
                return true;
            }
            if (nextByte == '\n') {
                return false;
            }
            if (HttpObjectDecoder.this.headerSize >= HttpObjectDecoder.this.maxHeaderSize) {
                throw new TooLongFrameException("HTTP header is larger than " + HttpObjectDecoder.this.maxHeaderSize + " bytes.");
            }
            this.seq.append(nextByte);
            return true;
        }
    }

    static enum State {
        SKIP_CONTROL_CHARS,
        READ_INITIAL,
        READ_HEADER,
        READ_VARIABLE_LENGTH_CONTENT,
        READ_FIXED_LENGTH_CONTENT,
        READ_CHUNK_SIZE,
        READ_CHUNKED_CONTENT,
        READ_CHUNK_DELIMITER,
        READ_CHUNK_FOOTER,
        BAD_MESSAGE,
        UPGRADED;

    }
}

