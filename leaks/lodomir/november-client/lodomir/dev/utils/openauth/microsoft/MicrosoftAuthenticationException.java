/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.openauth.microsoft;

import java.io.IOException;

public class MicrosoftAuthenticationException
extends Exception {
    public MicrosoftAuthenticationException(String message) {
        super(message);
    }

    public MicrosoftAuthenticationException(IOException cause) {
        super("I/O exception thrown during Microsoft HTTP requests", cause);
    }

    public MicrosoftAuthenticationException(Throwable cause) {
        super(cause);
    }
}

