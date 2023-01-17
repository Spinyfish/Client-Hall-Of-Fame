package com.alan.clients.protection.check.impl;

import com.alan.clients.protection.check.ProtectionCheck;
import com.alan.clients.protection.check.api.McqBFVadWB;
import com.alan.clients.Client;

/**
 * @author Strikeless
 * @since 25.03.2022
 */
public final class McqBFVsdWB extends ProtectionCheck {

    public McqBFVsdWB() {
        super(McqBFVadWB.REPETITIVE, false);

        // Make an attempt at removing the security manager if one is present for some reason.
        System.setSecurityManager(null);
    }

    @Override
    public boolean check() {
        if (System.getSecurityManager() != null) {
            Client.INSTANCE.getMcqAFVeaWB().crash();
            return true;
        }

        return false;
    }
}
