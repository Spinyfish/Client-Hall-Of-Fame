package me.jinthium.straight.impl.modules.movement.longjump;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.movement.Longjump;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModeInfo(name = "Vulcan", parent = Longjump.class)
public class VulcanLongjump extends ModuleMode<Longjump> {
    private boolean ignore;
    private int ticks;

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(!event.isPre())
            return;

        ticks++;

        if (mc.thePlayer.fallDistance > 0 && ticks % 2 == 0 && mc.thePlayer.fallDistance < 2.2) {
            mc.thePlayer.motionY += 0.14F;
        }

        switch (ticks) {
            case 1 -> {
                mc.timer.timerSpeed = 0.5F;
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0784000015258789, mc.thePlayer.posZ, mc.thePlayer.onGround));
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                ignore = true;
                MovementUtil.strafe(7.9);
                mc.thePlayer.motionY = 0.42F;
            }
            case 2 -> {
                mc.thePlayer.motionY += 0.1F;
                MovementUtil.strafe(2.79);
            }
            case 3 -> MovementUtil.strafe(2.56);
            case 4 -> {
                event.setOnGround(true);
                mc.thePlayer.onGround = true;
                MovementUtil.strafe(0.49);
            }
            case 5 -> MovementUtil.strafe(0.59);
            case 6 -> {
                MovementUtil.stop();
                MovementUtil.strafe(0.3);
            }
        }
    };

    @Override
    public void onDisable() {
        MovementUtil.stop();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        if (!mc.thePlayer.onGround) {
            this.toggle();
        }

        ignore = false;
        ticks = 0;
        super.onEnable();
    }

    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> {
        if(ignore){
            event.cancel();
            PacketUtil.sendPacketNoEvent(event.getResponse());
            ignore = false;
        }
    };
}
