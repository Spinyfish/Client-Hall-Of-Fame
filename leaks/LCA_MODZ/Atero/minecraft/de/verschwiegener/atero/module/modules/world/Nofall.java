package de.verschwiegener.atero.module.modules.world;

import net.minecraft.client.Minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;

public class Nofall extends Module {
    TimeUtils timeUtils;

    public Nofall() {
        super("Nofall", "Nofall", Keyboard.KEY_NONE, Category.World);
    }

    public void onEnable() {

        super.onEnable();
    }

    public void onDisable() {

        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            setExtraTag("Watchdog");
            if (Minecraft.thePlayer.fallDistance > 2.7F) {
                Minecraft.thePlayer.sendQueue.addToSendQueue((Packet) new C03PacketPlayer(true));
            }


        }
    }

}
