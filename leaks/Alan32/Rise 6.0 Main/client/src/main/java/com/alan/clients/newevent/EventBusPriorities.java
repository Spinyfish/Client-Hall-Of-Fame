package com.alan.clients.newevent;

public final class EventBusPriorities {

    public static final byte LOWEST    = Priorities.VERY_HIGH;
    public static final byte LOW       = Priorities.HIGH;
    public static final byte MEDIUM    = Priorities.MEDIUM;
    public static final byte HIGH      = Priorities.LOW;
    public static final byte HIGHEST   = Priorities.VERY_LOW;

    private EventBusPriorities() {}

}