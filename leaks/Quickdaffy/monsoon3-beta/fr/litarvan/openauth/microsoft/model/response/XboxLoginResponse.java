/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.microsoft.model.response;

public class XboxLoginResponse {
    private final String IssueInstant;
    private final String NotAfter;
    private final String Token;
    private final XboxLiveLoginResponseClaims DisplayClaims;

    public XboxLoginResponse(String IssueInstant, String NotAfter, String Token2, XboxLiveLoginResponseClaims DisplayClaims) {
        this.IssueInstant = IssueInstant;
        this.NotAfter = NotAfter;
        this.Token = Token2;
        this.DisplayClaims = DisplayClaims;
    }

    public String getIssueInstant() {
        return this.IssueInstant;
    }

    public String getNotAfter() {
        return this.NotAfter;
    }

    public String getToken() {
        return this.Token;
    }

    public XboxLiveLoginResponseClaims getDisplayClaims() {
        return this.DisplayClaims;
    }

    public static class XboxLiveUserInfo {
        private final String uhs;

        public XboxLiveUserInfo(String uhs) {
            this.uhs = uhs;
        }

        public String getUserHash() {
            return this.uhs;
        }
    }

    public static class XboxLiveLoginResponseClaims {
        private final XboxLiveUserInfo[] xui;

        public XboxLiveLoginResponseClaims(XboxLiveUserInfo[] xui) {
            this.xui = xui;
        }

        public XboxLiveUserInfo[] getUsers() {
            return this.xui;
        }
    }
}

