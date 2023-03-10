/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

public class SpdyStreamStatus
implements Comparable<SpdyStreamStatus> {
    public static final SpdyStreamStatus PROTOCOL_ERROR = new SpdyStreamStatus(1, "PROTOCOL_ERROR");
    public static final SpdyStreamStatus INVALID_STREAM = new SpdyStreamStatus(2, "INVALID_STREAM");
    public static final SpdyStreamStatus REFUSED_STREAM = new SpdyStreamStatus(3, "REFUSED_STREAM");
    public static final SpdyStreamStatus UNSUPPORTED_VERSION = new SpdyStreamStatus(4, "UNSUPPORTED_VERSION");
    public static final SpdyStreamStatus CANCEL = new SpdyStreamStatus(5, "CANCEL");
    public static final SpdyStreamStatus INTERNAL_ERROR = new SpdyStreamStatus(6, "INTERNAL_ERROR");
    public static final SpdyStreamStatus FLOW_CONTROL_ERROR = new SpdyStreamStatus(7, "FLOW_CONTROL_ERROR");
    public static final SpdyStreamStatus STREAM_IN_USE = new SpdyStreamStatus(8, "STREAM_IN_USE");
    public static final SpdyStreamStatus STREAM_ALREADY_CLOSED = new SpdyStreamStatus(9, "STREAM_ALREADY_CLOSED");
    public static final SpdyStreamStatus INVALID_CREDENTIALS = new SpdyStreamStatus(10, "INVALID_CREDENTIALS");
    public static final SpdyStreamStatus FRAME_TOO_LARGE = new SpdyStreamStatus(11, "FRAME_TOO_LARGE");
    private final int code;
    private final String statusPhrase;

    public static SpdyStreamStatus valueOf(int code) {
        if (code == 0) {
            throw new IllegalArgumentException("0 is not a valid status code for a RST_STREAM");
        }
        switch (code) {
            case 1: {
                return PROTOCOL_ERROR;
            }
            case 2: {
                return INVALID_STREAM;
            }
            case 3: {
                return REFUSED_STREAM;
            }
            case 4: {
                return UNSUPPORTED_VERSION;
            }
            case 5: {
                return CANCEL;
            }
            case 6: {
                return INTERNAL_ERROR;
            }
            case 7: {
                return FLOW_CONTROL_ERROR;
            }
            case 8: {
                return STREAM_IN_USE;
            }
            case 9: {
                return STREAM_ALREADY_CLOSED;
            }
            case 10: {
                return INVALID_CREDENTIALS;
            }
            case 11: {
                return FRAME_TOO_LARGE;
            }
        }
        return new SpdyStreamStatus(code, "UNKNOWN (" + code + ')');
    }

    public SpdyStreamStatus(int code, String statusPhrase) {
        if (code == 0) {
            throw new IllegalArgumentException("0 is not a valid status code for a RST_STREAM");
        }
        if (statusPhrase == null) {
            throw new NullPointerException("statusPhrase");
        }
        this.code = code;
        this.statusPhrase = statusPhrase;
    }

    public int code() {
        return this.code;
    }

    public String statusPhrase() {
        return this.statusPhrase;
    }

    public int hashCode() {
        return this.code();
    }

    public boolean equals(Object o) {
        if (!(o instanceof SpdyStreamStatus)) {
            return false;
        }
        return this.code() == ((SpdyStreamStatus)o).code();
    }

    public String toString() {
        return this.statusPhrase();
    }

    @Override
    public int compareTo(SpdyStreamStatus o) {
        return this.code() - o.code();
    }
}

