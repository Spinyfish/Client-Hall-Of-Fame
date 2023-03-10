/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl;

import lodomir.dev.event.Event;
import net.minecraft.world.World;

public class EventWorldChange
extends Event {
    private World oldWorld;
    private World newWorld;

    public World getOldWorld() {
        return this.oldWorld;
    }

    public void setOldWorld(World oldWorld) {
        this.oldWorld = oldWorld;
    }

    public World getNewWorld() {
        return this.newWorld;
    }

    public void setNewWorld(World newWorld) {
        this.newWorld = newWorld;
    }
}

