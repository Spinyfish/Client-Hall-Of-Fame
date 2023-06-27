package co.gongzh.procbridge;

import org.json.*;
import java.nio.charset.*;
import java.util.*;
import java.io.*;
import org.jetbrains.annotations.*;

final class Protocol
{
    private static final byte[] FLAG;
    
    @NotNull
    private static Map.Entry<StatusCode, JSONObject> read(@NotNull final InputStream stream) throws IOException, ProtocolException {
        int b = stream.read();
        if (b == -1 || b != Protocol.FLAG[0]) {
            throw new ProtocolException("unrecognized protocol");
        }
        b = stream.read();
        if (b == -1 || b != Protocol.FLAG[1]) {
            throw new ProtocolException("unrecognized protocol");
        }
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        if (b != Versions.CURRENT[0]) {
            throw new ProtocolException("incompatible protocol version");
        }
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        if (b != Versions.CURRENT[1]) {
            throw new ProtocolException("incompatible protocol version");
        }
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        final StatusCode statusCode = StatusCode.fromRawValue(b);
        if (statusCode == null) {
            throw new ProtocolException("invalid status code");
        }
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        int bodyLen = b;
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        bodyLen |= b << 8;
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        bodyLen |= b << 16;
        b = stream.read();
        if (b == -1) {
            throw new ProtocolException("incomplete data");
        }
        bodyLen |= b << 24;
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int restCount = bodyLen;
        byte[] buf = new byte[Math.min(bodyLen, 1048576)];
        int readCount;
        while ((readCount = stream.read(buf, 0, Math.min(buf.length, restCount))) != -1) {
            buffer.write(buf, 0, readCount);
            restCount -= readCount;
            if (restCount == 0) {
                break;
            }
        }
        if (buffer.size() != bodyLen) {
            throw new ProtocolException("incomplete data");
        }
        buffer.flush();
        buf = buffer.toByteArray();
        try {
            final String jsonText = new String(buf, StandardCharsets.UTF_8);
            final JSONObject body = new JSONObject(jsonText);
            return new AbstractMap.SimpleEntry<StatusCode, JSONObject>(statusCode, body);
        }
        catch (Exception ex) {
            throw new ProtocolException("invalid body");
        }
    }
    
    private static void write(@NotNull final OutputStream stream, @NotNull final StatusCode statusCode, @NotNull final JSONObject body) throws IOException {
        stream.write(Protocol.FLAG);
        stream.write(Versions.CURRENT);
        stream.write(statusCode.rawValue);
        stream.write(0);
        stream.write(0);
        final byte[] buf = body.toString().getBytes(StandardCharsets.UTF_8);
        final int len = buf.length;
        final int b0 = len & 0xFF;
        final int b2 = (len & 0xFF00) >> 8;
        final int b3 = (len & 0xFF0000) >> 16;
        final int b4 = (len & 0xFF000000) >> 24;
        stream.write(b0);
        stream.write(b2);
        stream.write(b3);
        stream.write(b4);
        stream.write(buf);
        stream.flush();
    }
    
    @NotNull
    static Map.Entry<String, Object> readRequest(@NotNull final InputStream stream) throws IOException, ProtocolException {
        final Map.Entry<StatusCode, JSONObject> entry = read(stream);
        final StatusCode statusCode = entry.getKey();
        final JSONObject body = entry.getValue();
        if (statusCode != StatusCode.REQUEST) {
            throw new ProtocolException("invalid status code");
        }
        final String method = body.optString("method");
        final Object payload = body.opt("payload");
        return new AbstractMap.SimpleEntry<String, Object>(method, payload);
    }
    
    @NotNull
    static Map.Entry<StatusCode, Object> readResponse(@NotNull final InputStream stream) throws IOException, ProtocolException {
        final Map.Entry<StatusCode, JSONObject> entry = read(stream);
        final StatusCode statusCode = entry.getKey();
        final JSONObject body = entry.getValue();
        if (statusCode == StatusCode.GOOD_RESPONSE) {
            return new AbstractMap.SimpleEntry<StatusCode, Object>(StatusCode.GOOD_RESPONSE, body.opt("payload"));
        }
        if (statusCode == StatusCode.BAD_RESPONSE) {
            return new AbstractMap.SimpleEntry<StatusCode, Object>(StatusCode.BAD_RESPONSE, body.optString("message"));
        }
        throw new ProtocolException("invalid status code");
    }
    
    static void writeRequest(@NotNull final OutputStream stream, @Nullable final String method, @Nullable final Object payload) throws IOException {
        final JSONObject body = new JSONObject();
        if (method != null) {
            body.put("method", method);
        }
        if (payload != null) {
            body.put("payload", payload);
        }
        write(stream, StatusCode.REQUEST, body);
    }
    
    static void writeGoodResponse(@NotNull final OutputStream stream, @Nullable final Object payload) throws IOException {
        final JSONObject body = new JSONObject();
        if (payload != null) {
            body.put("payload", payload);
        }
        write(stream, StatusCode.GOOD_RESPONSE, body);
    }
    
    static void writeBadResponse(@NotNull final OutputStream stream, @Nullable final String message) throws IOException {
        final JSONObject body = new JSONObject();
        if (message != null) {
            body.put("message", message);
        }
        write(stream, StatusCode.BAD_RESPONSE, body);
    }
    
    private Protocol() {
    }
    
    static {
        FLAG = new byte[] { 112, 98 };
    }
}
