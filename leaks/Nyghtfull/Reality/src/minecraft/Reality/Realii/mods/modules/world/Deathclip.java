/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.world;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;

public class Deathclip
        extends Module {
    private TimerUtil timer = new TimerUtil();

    public Deathclip() {
        super("DeathClip", ModuleType.Render);

    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (this.mc.thePlayer.getHealth() == 0.0f && this.mc.thePlayer.onGround) {
            this.mc.thePlayer.boundingBox.offsetAndUpdate(this.mc.thePlayer.posX, -10.0, this.mc.thePlayer.posZ);
            if (this.timer.hasReached(500.0)) {
                this.mc.thePlayer.sendChatMessage("/home");
            }
        }
    }
}

