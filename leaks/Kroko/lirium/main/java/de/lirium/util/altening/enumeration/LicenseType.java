package de.lirium.util.altening.enumeration;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LicenseType {
    NONE("none"), STARTER("starter"), BASIC("basic"), PREMIUM("premium");

    public String name;
}
