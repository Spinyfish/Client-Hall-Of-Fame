package com.alan.clients.newevent;

public interface Event {
    default boolean callOutsideOfGame() {
        return false;
    }
}
