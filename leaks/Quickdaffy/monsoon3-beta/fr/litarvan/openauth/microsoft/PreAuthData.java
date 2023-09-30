/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.microsoft;

public class PreAuthData {
    private final String ppft;
    private final String urlPost;

    public PreAuthData(String ppft, String urlPost) {
        this.ppft = ppft;
        this.urlPost = urlPost;
    }

    public String getPPFT() {
        return this.ppft;
    }

    public String getUrlPost() {
        return this.urlPost;
    }
}

