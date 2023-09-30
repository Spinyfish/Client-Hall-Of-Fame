/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth;

import fr.litarvan.openauth.model.AuthError;

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

