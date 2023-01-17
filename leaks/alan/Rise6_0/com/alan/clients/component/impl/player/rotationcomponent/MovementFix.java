package com.alan.clients.component.impl.player.rotationcomponent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MovementFix {
    OFF("Off"),
    NORMAL("Rise"),
    TRADITIONAL("Traditional"),
    BACKWARDS_SPRINT("Backwards Sprint");

    String name;

    @Override
    public String toString() {
        return name;
    }
}