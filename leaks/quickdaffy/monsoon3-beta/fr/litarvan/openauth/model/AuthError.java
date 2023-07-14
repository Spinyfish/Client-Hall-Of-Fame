/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.model;

public class AuthError {
    private String error;
    private String errorMessage;
    private String cause;

    public AuthError(String error, String errorMessage, String cause) {
        this.error = error;
        this.errorMessage = errorMessage;
        this.cause = cause;
    }

    public String getError() {
        return this.error;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getCause() {
        return this.cause;
    }
}

