package de.verschwiegener.atero.module.modules.player;


import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventReceivedPacket;
import net.minecraft.client.Minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;

public class Blink extends Module {
    TimeUtils timeUtils;
public boolean cancel;
    public Blink() {
        super("Blink", "Blink", Keyboard.KEY_NONE, Category.Player);
    }

    public void onEnable() {
        Minecraft.getMinecraft().thePlayer.setSprinting(true);
        super.onEnable();
    }

    public void onDisable() {
        Minecraft.getMinecraft().thePlayer.setSprinting(false);
        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            setExtraTag("Watchdog");

        }
    }
    @EventTarget
    public void onUpdate(EventReceivedPacket ppe) {
        Packet p = ppe.getPacket();

        mc.timer.timerSpeed = 1F;

    }
}
