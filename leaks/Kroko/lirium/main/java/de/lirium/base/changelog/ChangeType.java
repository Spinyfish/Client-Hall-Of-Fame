package de.lirium.base.changelog;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChangeType {
    ADDED('+', "&aAdded &e"), CHANGED('*', "&b* &e"), REMOVED('-', "&c&l&m");

    public final char identifier;
    public final String display;
}
