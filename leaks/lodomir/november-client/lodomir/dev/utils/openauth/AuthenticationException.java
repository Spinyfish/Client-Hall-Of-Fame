/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.openauth;

import lodomir.dev.utils.openauth.model.AuthError;

public class AuthenticationException
extends Exception {
    private AuthError model;

    public AuthenticationException(AuthError model) {
        super(model.getErrorMessage());
        this.model = model;
    }

    public AuthError getErrorModel() {
        return this.model;
    }
}

