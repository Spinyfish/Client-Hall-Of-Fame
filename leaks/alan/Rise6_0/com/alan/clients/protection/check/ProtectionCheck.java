package com.alan.clients.protection.check;

import com.alan.clients.protection.check.api.McqBFVadWB;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Strikeless
 * @since 25.03.2022
 */
@Getter
@RequiredArgsConstructor
public abstract class ProtectionCheck {

    private final McqBFVadWB trigger;
    private final boolean exemptDev;

    public abstract boolean check() throws Throwable;
}
