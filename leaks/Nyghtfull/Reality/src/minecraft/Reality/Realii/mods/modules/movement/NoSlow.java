/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.movement;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class NoSlow
        extends Module {
    public static Option noAttackSlow = new Option("NoAttackSlow", true);

    public NoSlow() {
        super("NoSlow", ModuleType.Movement);
        addValues(noAttackSlow);
    }


    @EventHandler
    private void onUpdate(EventPreUpdate e) {
    }
}

