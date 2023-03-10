/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl.game;

import lodomir.dev.event.EventUpdate;
import net.minecraft.world.World;

public class EventWorldChange
extends EventUpdate {
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

