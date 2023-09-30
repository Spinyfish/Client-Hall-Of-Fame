/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.render;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Bobbing
extends Module {
    private Numbers<Number> boob = new Numbers<Number>("Amount", "Amount", 1.0, 0.1, 5.0, 0.5);

    public Bobbing() {
        super("Bobbing", ModuleType.Render);
        this.addValues(this.boob);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (this.mc.thePlayer.onGround) {
            this.mc.thePlayer.cameraYaw = (float)(0.09090908616781235 * this.boob.getValue().floatValue());
        }
    }
}

