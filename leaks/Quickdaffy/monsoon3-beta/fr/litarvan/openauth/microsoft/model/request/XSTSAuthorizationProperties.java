/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.microsoft.model.request;

public class XSTSAuthorizationProperties {
    private final String SandboxId;
    private final String[] UserTokens;

    public XSTSAuthorizationProperties(String SandboxId, String[] UserTokens) {
        this.SandboxId = SandboxId;
        this.UserTokens = UserTokens;
    }

    public String getSandboxId() {
        return this.SandboxId;
    }

    public String[] getUserTokens() {
        return this.UserTokens;
    }
}

