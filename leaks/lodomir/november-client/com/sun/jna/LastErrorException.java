/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Platform;

public class LastErrorException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int errorCode;

    private static String formatMessage(int code) {
        return Platform.isWindows() ? "GetLastError() returned " + code : "errno was " + code;
    }

    private static String parseMessage(String m) {
        try {
            return LastErrorException.formatMessage(Integer.parseInt(m));
        }
        catch (NumberFormatException e) {
            return m;
        }
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public LastErrorException(String msg) {
        super(LastErrorException.parseMessage(msg.trim()));
        try {
            if (msg.startsWith("[")) {
                msg = msg.substring(1, msg.indexOf("]"));
            }
            this.errorCode = Integer.parseInt(msg);
        }
        catch (NumberFormatException e) {
            this.errorCode = -1;
        }
    }

    public LastErrorException(int code) {
        this(code, LastErrorException.formatMessage(code));
    }

    protected LastErrorException(int code, String msg) {
        super(msg);
        this.errorCode = code;
    }
}

