/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.thealtening.api.response;

import com.google.gson.annotations.SerializedName;

public class License {
    private String username;
    @SerializedName(value="hasLicense")
    private boolean premium;
    @SerializedName(value="licenseType")
    private String premiumName;
    @SerializedName(value="expires")
    private String expiryDate;

    public String getUsername() {
        return this.username;
    }

    public boolean isPremium() {
        return this.premium;
    }

    public String getPremiumName() {
        return this.premiumName;
    }

    public String getExpiryDate() {
        return this.expiryDate;
    }

    public String toString() {
        return String.format("License[%s:%s:%s:%s]", this.username, this.premium, this.premiumName, this.expiryDate);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof License)) {
            return false;
        }
        License other = (License)obj;
        return other.getExpiryDate().equals(this.getExpiryDate()) && other.getPremiumName().equals(this.getPremiumName()) && other.isPremium() == this.isPremium() && other.getUsername().equals(this.getUsername());
    }
}

