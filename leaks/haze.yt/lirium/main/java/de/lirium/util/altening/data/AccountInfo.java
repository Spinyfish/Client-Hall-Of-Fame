package de.lirium.util.altening.data;

import de.lirium.util.altening.enumeration.LicenseType;

public class AccountInfo {
    private final String name, expiresAt;
    private final LicenseType licenseType;
    private final boolean hasLicense;

    public AccountInfo(String name, LicenseType licenseType, String expiresAt) {
        this.name = name;
        this.expiresAt = expiresAt;
        this.licenseType = licenseType;
        hasLicense = true;
    }

    public AccountInfo(String name) {
        this.name = name;
        this.expiresAt = "expired";
        this.licenseType = LicenseType.NONE;
        this.hasLicense = false;
    }

    @Override
    public String toString() {
        return name + "(" + licenseType.name + ";" + expiresAt + ")";
    }
}
