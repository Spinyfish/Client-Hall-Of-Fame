/*
 * Copyright Felix Hans from AccountGrop coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.altmanager.group;

public class AccountGroup implements IAccountGroup {

    private String groupName;
    private boolean hasHearth = false;

    public AccountGroup setupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public AccountGroup setHearth(final boolean hearth) {
        this.hasHearth = hearth;
        return this;
    }

    @Override
    public String groupName() {
        return groupName;
    }

    @Override
    public boolean hasHearth() {
        return hasHearth;
    }
}
