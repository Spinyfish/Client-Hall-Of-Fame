/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.misc;

import net.minecraft.network.play.client.C0BPacketEntityAction;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Sneak
        extends Module {
    public Sneak() {
        super("Sneak", ModuleType.Misc);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (e.getType() == 0) {
            if (this.mc.thePlayer.isSneaking() || this.mc.thePlayer.moving()) {
                return;
            }
            this.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        } else if (e.getType() == 1) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        }
    }

    @Override
    public void onDisable() {
        this.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
    }
}

