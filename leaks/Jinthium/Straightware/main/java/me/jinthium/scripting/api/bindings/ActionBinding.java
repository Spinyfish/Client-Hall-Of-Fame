package me.jinthium.scripting.api.bindings;



public class ActionBinding {
    public final int START_SNEAKING = 0,
            STOP_SNEAKING = 1,
            STOP_SLEEPING = 2,
            START_SPRINTING = 3,
            STOP_SPRINTING = 4,
            RIDING_JUMP = 5,
            OPEN_INVENTORY = 6;

    public final int INTERACT = 0,
            ATTACK = 1,
            INTERACT_AT = 2;

    public final int START_DESTROY_BLOCK = 0,
            ABORT_DESTROY_BLOCK = 1,
            STOP_DESTROY_BLOCK = 2,
            DROP_ALL_ITEMS = 3,
            DROP_ITEM = 4,
            RELEASE_USE_ITEM = 5;

    public final int PERFORM_RESPAWN = 0,
            REQUEST_STATS = 1,
            OPEN_INVENTORY_ACHIEVEMENT = 2;
}
