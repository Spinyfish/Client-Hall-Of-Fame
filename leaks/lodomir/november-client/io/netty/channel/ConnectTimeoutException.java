/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import java.net.ConnectException;

public class ConnectTimeoutException
extends ConnectException {
    private static final long serialVersionUID = 2317065249988317463L;

    public ConnectTimeoutException(String msg) {
        super(msg);
    }

    public ConnectTimeoutException() {
    }
}

