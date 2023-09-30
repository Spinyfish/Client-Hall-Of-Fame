/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.microsoft.model.request;

public class XboxLoginRequest<T> {
    private final T Properties;
    private final String RelyingParty;
    private final String TokenType;

    public XboxLoginRequest(T Properties2, String RelyingParty, String TokenType2) {
        this.Properties = Properties2;
        this.RelyingParty = RelyingParty;
        this.TokenType = TokenType2;
    }

    public T getProperties() {
        return this.Properties;
    }

    public String getSiteName() {
        return this.RelyingParty;
    }

    public String getTokenType() {
        return this.TokenType;
    }
}

