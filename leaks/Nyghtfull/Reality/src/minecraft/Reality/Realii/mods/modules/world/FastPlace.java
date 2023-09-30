/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.world;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventTick;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class FastPlace
extends Module {

    public FastPlace(){
        super("FastPlace", ModuleType.World);
    }

    @EventHandler
    private void onTick(EventTick e) {
        this.mc.rightClickDelayTimer = 0;
    }
}

