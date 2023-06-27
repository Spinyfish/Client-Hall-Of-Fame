package co.gongzh.procbridge;

public final class ProtocolException extends RuntimeException
{
    static final String UNRECOGNIZED_PROTOCOL = "unrecognized protocol";
    static final String INCOMPATIBLE_VERSION = "incompatible protocol version";
    static final String INCOMPLETE_DATA = "incomplete data";
    static final String INVALID_STATUS_CODE = "invalid status code";
    static final String INVALID_BODY = "invalid body";
    
    ProtocolException(final String message) {
        super(message);
    }
}
