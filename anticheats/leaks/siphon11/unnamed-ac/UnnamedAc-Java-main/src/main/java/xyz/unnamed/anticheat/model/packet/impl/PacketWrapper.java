package xyz.unnamed.anticheat.model.packet.impl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface PacketWrapper {
    Class<?> nmsClass();
}
