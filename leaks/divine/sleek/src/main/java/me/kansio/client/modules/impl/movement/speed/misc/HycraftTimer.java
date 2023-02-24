package me.kansio.client.modules.impl.movement.speed.misc;

import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.network.PacketUtil;
import me.kansio.client.utils.player.PlayerUtil;
import me.kansio.client.utils.player.TimerUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import viamcp.ViaMCP;

public class HycraftTimer extends SpeedMode {

    public HycraftTimer() {
        super("Hycraft (Timer)");
    }

    @Override
    public void onEnable() {
        if (ViaMCP.getInstance().getVersion() != 755) {
            ChatUtil.log("§cYou must use 1.17 with viaversion to use this mode, also make sure you have disabler toggled with the mode Hycraft Timer.");
            getSpeed().toggle();
            return;
        }

        TimerUtil.setTimer(getSpeed().getSpeed().getValue().floatValue());
        mc.gameSettings.keyBindJump.pressed = true;
    }

    @Override
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onDisable() {
        TimerUtil.Reset();

        mc.gameSettings.keyBindJump.pressed = false;
    }
}
