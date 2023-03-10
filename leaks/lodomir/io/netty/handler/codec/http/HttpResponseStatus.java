/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;

public class HttpResponseStatus
implements Comparable<HttpResponseStatus> {
    public static final HttpResponseStatus CONTINUE = new HttpResponseStatus(100, "Continue", true);
    public static final HttpResponseStatus SWITCHING_PROTOCOLS = new HttpResponseStatus(101, "Switching Protocols", true);
    public static final HttpResponseStatus PROCESSING = new HttpResponseStatus(102, "Processing", true);
    public static final HttpResponseStatus OK = new HttpResponseStatus(200, "OK", true);
    public static final HttpResponseStatus CREATED = new HttpResponseStatus(201, "Created", true);
    public static final HttpResponseStatus ACCEPTED = new HttpResponseStatus(202, "Accepted", true);
    public static final HttpResponseStatus NON_AUTHORITATIVE_INFORMATION = new HttpResponseStatus(203, "Non-Authoritative Information", true);
    public static final HttpResponseStatus NO_CONTENT = new HttpResponseStatus(204, "No Content", true);
    public static final HttpResponseStatus RESET_CONTENT = new HttpResponseStatus(205, "Reset Content", true);
    public static final HttpResponseStatus PARTIAL_CONTENT = new HttpResponseStatus(206, "Partial Content", true);
    public static final HttpResponseStatus MULTI_STATUS = new HttpResponseStatus(207, "Multi-Status", true);
    public static final HttpResponseStatus MULTIPLE_CHOICES = new HttpResponseStatus(300, "Multiple Choices", true);
    public static final HttpResponseStatus MOVED_PERMANENTLY = new HttpResponseStatus(301, "Moved Permanently", true);
    public static final HttpResponseStatus FOUND = new HttpResponseStatus(302, "Found", true);
    public static final HttpResponseStatus SEE_OTHER = new HttpResponseStatus(303, "See Other", true);
    public static final HttpResponseStatus NOT_MODIFIED = new HttpResponseStatus(304, "Not Modified", true);
    public static final HttpResponseStatus USE_PROXY = new HttpResponseStatus(305, "Use Proxy", true);
    public static final HttpResponseStatus TEMPORARY_REDIRECT = new HttpResponseStatus(307, "Temporary Redirect", true);
    public static final HttpResponseStatus BAD_REQUEST = new HttpResponseStatus(400, "Bad Request", true);
    public static final HttpResponseStatus UNAUTHORIZED = new HttpResponseStatus(401, "Unauthorized", true);
    public static final HttpResponseStatus PAYMENT_REQUIRED = new HttpResponseStatus(402, "Payment Required", true);
    public static final HttpResponseStatus FORBIDDEN = new HttpResponseStatus(403, "Forbidden", true);
    public static final HttpResponseStatus NOT_FOUND = new HttpResponseStatus(404, "Not Found", true);
    public static final HttpResponseStatus METHOD_NOT_ALLOWED = new HttpResponseStatus(405, "Method Not Allowed", true);
    public static final HttpResponseStatus NOT_ACCEPTABLE = new HttpResponseStatus(406, "Not Acceptable", true);
    public static final HttpResponseStatus PROXY_AUTHENTICATION_REQUIRED = new HttpResponseStatus(407, "Proxy Authentication Required", true);
    public static final HttpResponseStatus REQUEST_TIMEOUT = new HttpResponseStatus(408, "Request Timeout", true);
    public static final HttpResponseStatus CONFLICT = new HttpResponseStatus(409, "Conflict", true);
    public static final HttpResponseStatus GONE = new HttpResponseStatus(410, "Gone", true);
    public static final HttpResponseStatus LENGTH_REQUIRED = new HttpResponseStatus(411, "Length Required", true);
    public static final HttpResponseStatus PRECONDITION_FAILED = new HttpResponseStatus(412, "Precondition Failed", true);
    public static final HttpResponseStatus REQUEST_ENTITY_TOO_LARGE = new HttpResponseStatus(413, "Request Entity Too Large", true);
    public static final HttpResponseStatus REQUEST_URI_TOO_LONG = new HttpResponseStatus(414, "Request-URI Too Long", true);
    public static final HttpResponseStatus UNSUPPORTED_MEDIA_TYPE = new HttpResponseStatus(415, "Unsupported Media Type", true);
    public static final HttpResponseStatus REQUESTED_RANGE_NOT_SATISFIABLE = new HttpResponseStatus(416, "Requested Range Not Satisfiable", true);
    public static final HttpResponseStatus EXPECTATION_FAILED = new HttpResponseStatus(417, "Expectation Failed", true);
    public static final HttpResponseStatus UNPROCESSABLE_ENTITY = new HttpResponseStatus(422, "Unprocessable Entity", true);
    public static final HttpResponseStatus LOCKED = new HttpResponseStatus(423, "Locked", true);
    public static final HttpResponseStatus FAILED_DEPENDENCY = new HttpResponseStatus(424, "Failed Dependency", true);
    public static final HttpResponseStatus UNORDERED_COLLECTION = new HttpResponseStatus(425, "Unordered Collection", true);
    public static final HttpResponseStatus UPGRADE_REQUIRED = new HttpResponseStatus(426, "Upgrade Required", true);
    public static final HttpResponseStatus PRECONDITION_REQUIRED = new HttpResponseStatus(428, "Precondition Required", true);
    public static final HttpResponseStatus TOO_MANY_REQUESTS = new HttpResponseStatus(429, "Too Many Requests", true);
    public static final HttpResponseStatus REQUEST_HEADER_FIELDS_TOO_LARGE = new HttpResponseStatus(431, "Request Header Fields Too Large", true);
    public static final HttpResponseStatus INTERNAL_SERVER_ERROR = new HttpResponseStatus(500, "Internal Server Error", true);
    public static final HttpResponseStatus NOT_IMPLEMENTED = new HttpResponseStatus(501, "Not Implemented", true);
    public static final HttpResponseStatus BAD_GATEWAY = new HttpResponseStatus(502, "Bad Gateway", true);
    public static final HttpResponseStatus SERVICE_UNAVAILABLE = new HttpResponseStatus(503, "Service Unavailable", true);
    public static final HttpResponseStatus GATEWAY_TIMEOUT = new HttpResponseStatus(504, "Gateway Timeout", true);
    public static final HttpResponseStatus HTTP_VERSION_NOT_SUPPORTED = new HttpResponseStatus(505, "HTTP Version Not Supported", true);
    public static final HttpResponseStatus VARIANT_ALSO_NEGOTIATES = new HttpResponseStatus(506, "Variant Also Negotiates", true);
    public static final HttpResponseStatus INSUFFICIENT_STORAGE = new HttpResponseStatus(507, "Insufficient Storage", true);
    public static final HttpResponseStatus NOT_EXTENDED = new HttpResponseStatus(510, "Not Extended", true);
    public static final HttpResponseStatus NETWORK_AUTHENTICATION_REQUIRED = new HttpResponseStatus(511, "Network Authentication Required", true);
    private final int code;
    private final String reasonPhrase;
    private final byte[] bytes;

    public static HttpResponseStatus valueOf(int code) {
        switch (code) {
            case 100: {
                return CONTINUE;
            }
            case 101: {
                return SWITCHING_PROTOCOLS;
            }
            case 102: {
                return PROCESSING;
            }
            case 200: {
                return OK;
            }
            case 201: {
                return CREATED;
            }
            case 202: {
                return ACCEPTED;
            }
            case 203: {
                return NON_AUTHORITATIVE_INFORMATION;
            }
            case 204: {
                return NO_CONTENT;
            }
            case 205: {
                return RESET_CONTENT;
            }
            case 206: {
                return PARTIAL_CONTENT;
            }
            case 207: {
                return MULTI_STATUS;
            }
            case 300: {
                return MULTIPLE_CHOICES;
            }
            case 301: {
                return MOVED_PERMANENTLY;
            }
            case 302: {
                return FOUND;
            }
            case 303: {
                return SEE_OTHER;
            }
            case 304: {
                return NOT_MODIFIED;
            }
            case 305: {
                return USE_PROXY;
            }
            case 307: {
                return TEMPORARY_REDIRECT;
            }
            case 400: {
                return BAD_REQUEST;
            }
            case 401: {
                return UNAUTHORIZED;
            }
            case 402: {
                return PAYMENT_REQUIRED;
            }
            case 403: {
                return FORBIDDEN;
            }
            case 404: {
                return NOT_FOUND;
            }
            case 405: {
                return METHOD_NOT_ALLOWED;
            }
            case 406: {
                return NOT_ACCEPTABLE;
            }
            case 407: {
                return PROXY_AUTHENTICATION_REQUIRED;
            }
            case 408: {
                return REQUEST_TIMEOUT;
            }
            case 409: {
                return CONFLICT;
            }
            case 410: {
                return GONE;
            }
            case 411: {
                return LENGTH_REQUIRED;
            }
            case 412: {
                return PRECONDITION_FAILED;
            }
            case 413: {
                return REQUEST_ENTITY_TOO_LARGE;
            }
            case 414: {
                return REQUEST_URI_TOO_LONG;
            }
            case 415: {
                return UNSUPPORTED_MEDIA_TYPE;
            }
            case 416: {
                return REQUESTED_RANGE_NOT_SATISFIABLE;
            }
            case 417: {
                return EXPECTATION_FAILED;
            }
            case 422: {
                return UNPROCESSABLE_ENTITY;
            }
            case 423: {
                return LOCKED;
            }
            case 424: {
                return FAILED_DEPENDENCY;
            }
            case 425: {
                return UNORDERED_COLLECTION;
            }
            case 426: {
                return UPGRADE_REQUIRED;
            }
            case 428: {
                return PRECONDITION_REQUIRED;
            }
            case 429: {
                return TOO_MANY_REQUESTS;
            }
            case 431: {
                return REQUEST_HEADER_FIELDS_TOO_LARGE;
            }
            case 500: {
                return INTERNAL_SERVER_ERROR;
            }
            case 501: {
                return NOT_IMPLEMENTED;
            }
            case 502: {
                return BAD_GATEWAY;
            }
            case 503: {
                return SERVICE_UNAVAILABLE;
            }
            case 504: {
                return GATEWAY_TIMEOUT;
            }
            case 505: {
                return HTTP_VERSION_NOT_SUPPORTED;
            }
            case 506: {
                return VARIANT_ALSO_NEGOTIATES;
            }
            case 507: {
                return INSUFFICIENT_STORAGE;
            }
            case 510: {
                return NOT_EXTENDED;
            }
            case 511: {
                return NETWORK_AUTHENTICATION_REQUIRED;
            }
        }
        String reasonPhrase = code < 100 ? "Unknown Status" : (code < 200 ? "Informational" : (code < 300 ? "Successful" : (code < 400 ? "Redirection" : (code < 500 ? "Client Error" : (code < 600 ? "Server Error" : "Unknown Status")))));
        return new HttpResponseStatus(code, reasonPhrase + " (" + code + ')');
    }

    public HttpResponseStatus(int code, String reasonPhrase) {
        this(code, reasonPhrase, false);
    }

    private HttpResponseStatus(int code, String reasonPhrase, boolean bytes) {
        if (code < 0) {
            throw new IllegalArgumentException("code: " + code + " (expected: 0+)");
        }
        if (reasonPhrase == null) {
            throw new NullPointerException("reasonPhrase");
        }
        for (int i = 0; i < reasonPhrase.length(); ++i) {
            char c = reasonPhrase.charAt(i);
            switch (c) {
                case '\n': 
                case '\r': {
                    throw new IllegalArgumentException("reasonPhrase contains one of the following prohibited characters: \\r\\n: " + reasonPhrase);
                }
            }
        }
        this.code = code;
        this.reasonPhrase = reasonPhrase;
        this.bytes = (byte[])(bytes ? (code + " " + reasonPhrase).getBytes(CharsetUtil.US_ASCII) : null);
    }

    public int code() {
        return this.code;
    }

    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    public int hashCode() {
        return this.code();
    }

    public boolean equals(Object o) {
        if (!(o instanceof HttpResponseStatus)) {
            return false;
        }
        return this.code() == ((HttpResponseStatus)o).code();
    }

    @Override
    public int compareTo(HttpResponseStatus o) {
        return this.code() - o.code();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(this.reasonPhrase.length() + 5);
        buf.append(this.code);
        buf.append(' ');
        buf.append(this.reasonPhrase);
        return buf.toString();
    }

    void encode(ByteBuf buf) {
        if (this.bytes == null) {
            HttpHeaders.encodeAscii0(String.valueOf(this.code()), buf);
            buf.writeByte(32);
            HttpHeaders.encodeAscii0(String.valueOf(this.reasonPhrase()), buf);
        } else {
            buf.writeBytes(this.bytes);
        }
    }
}

