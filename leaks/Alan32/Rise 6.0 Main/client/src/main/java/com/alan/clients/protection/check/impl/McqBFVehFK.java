package com.alan.clients.protection.check.impl;

import com.alan.clients.Client;
import com.alan.clients.protection.check.ProtectionCheck;
import com.alan.clients.protection.check.api.McqBFVadWB;

/**
 * @author Strikeless
 * @since 24.04.2022
 */
public class McqBFVehFK extends ProtectionCheck {

    public McqBFVehFK() {
        super(McqBFVadWB.JOIN, false);
    }

    @Override
    public boolean check() throws Throwable {
        // Every time we join a world, check whether the repetitive
        // check handler thread has died, if it has, immediately crash the client.
        // I would do this on a repetitive trigger but... well, you know.
        final Thread repetitiveHandlerThread = Client.INSTANCE.getMcqAFVeaWB().getRepetitiveHandlerThread();

        if (!repetitiveHandlerThread.isAlive() || repetitiveHandlerThread.isInterrupted()) {
            Client.INSTANCE.getMcqAFVeaWB().crash();
            return true;
        }

        return false;
    }
}
