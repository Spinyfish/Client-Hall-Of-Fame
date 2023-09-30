/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.misc;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import Realioty.server.utile.TimerUtil;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPacketRecieve;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.Helper;

public class NoRotate
        extends Module {
    public NoRotate() {
        super("NoStuck", ModuleType.Misc);
    }

    TimerUtil timerUtil = new TimerUtil();
    int stuck = 0;

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if (timerUtil.delay(10000)) {
            stuck = 0;
            timerUtil.reset();
        }
    }

    @EventHandler
    private void onPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook && stuck < 3) {
            S08PacketPlayerPosLook look = (S08PacketPlayerPosLook) e.getPacket();
            look.yaw = this.mc.thePlayer.rotationYaw;
            look.pitch = this.mc.thePlayer.rotationPitch;
            Helper.sendMessage("(NoStuck!):" + stuck + " " + look.yaw + "  " + look.pitch);
        }
    }
}

