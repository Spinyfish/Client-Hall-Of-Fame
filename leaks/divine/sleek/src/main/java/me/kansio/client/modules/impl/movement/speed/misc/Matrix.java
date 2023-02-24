package me.kansio.client.modules.impl.movement.speed.misc;

import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.modules.impl.player.Timer;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.math.BPSUtil;
import me.kansio.client.utils.network.PacketUtil;
import me.kansio.client.utils.player.PlayerUtil;
import me.kansio.client.utils.player.TimerUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import viamcp.ViaMCP;

public class Matrix extends SpeedMode {

    public Matrix() {
        super("Matrix Hop");
    }

    @Override
    public void onEnable() {
        if (ViaMCP.getInstance().getVersion() != 755) {
            ChatUtil.log("§cYou must use 1.17 with viaversion to use this mode.");
            getSpeed().toggle();
        }

        mc.gameSettings.keyBindJump.pressed = true;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindJump.pressed = false;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround) {
            TimerUtil.setTimer(0.5f);
        } else {
            TimerUtil.setTimer(1.5f);
        }

        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition().down(255), 256, null, 0, 0, 0));
    }
}
